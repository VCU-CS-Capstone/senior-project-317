# tagScanAndUpload.py
# Run this script to scan and upload iBeacons, filtered by our UUID, to firebase.
# Script runs infinitely, until exception or until killed by sys/user.
# 	If there's an exception, we log and we quit.
# See associated log file for troublshooting.
#
# Must be run as root/sudo in order to use bluetooth!
# Also note: the firebase_cred file that we use needs to be kept secure! - don't put on github.
#
# If script is killed manually, be sure to kill any children manually (hcidump,ibeacon_scan.sh,...)
#
# See https://github.com/firebase/firebase-admin-python , https://firebase.google.com/docs/database/admin/start

import firebase_admin
from firebase_admin import credentials, db

from subprocess import Popen, PIPE
import os
import signal
import sys
import time


ACCEPTED_UUID = "D0D3FA86-CA76-45EC-9BD9-6AF4F6016926"
PI_LATITUDE = 37.546746
PI_LONGITUDE = -77.450358 # Monroe park
PI_LOC_MESSAGE = "Pi Number 1"

SCAN_LENGTH_SECONDS = 20
SECONDS_BETWEEN_SCANS = 0 # Keep in mind, script blocks while uploading to db.

FIREBASE_CRED_FILENAME = "serviceAccountKey.json"
FIREBASE_DATABASE_URL = 'https://seniorprojectbym.firebaseio.com'
FIREBASE_DATABASE_REF_PATH = "observations"

LOG_FILENAME = "log_file.txt"
FORMAT_TIME_STRING = "%a, %d %b %Y %H:%M:%S" # Used to format times in log


# Uses credentials stored in separate file to prepare to connect to firebase.
# No actual connections or attempts to use internet are made here.
# Returns 'reference' to be used to access particular location in database later.
def initializeFirebase():
	# Fetch account key from JSON file
	cred = credentials.Certificate(FIREBASE_CRED_FILENAME)
	# Initialize the app. Admin privleges.
	firebase_admin.initialize_app(cred, {
		'databaseURL': FIREBASE_DATABASE_URL
	})
	# Get the database reference to where we'll store our data
	return db.reference(FIREBASE_DATABASE_REF_PATH)

# Writes timestamp + message + newLine.
def writeToLogFile(message=""):
	try:
		logFile = open(LOG_FILENAME, "a")
		logFile.write(time.strftime(FORMAT_TIME_STRING) + "\t" + message + "\n")
		logFile.close()
	except:
		# Specifically, we're worried about if the log file gets too large.
		# If there's an error, we overwrite entire file.
		logFile = open(LOG_FILENAME, "w")
		logFile.write(time.strftime(FORMAT_TIME_STRING) + "\tLOG ERROR. OVERWRITE LOG FILE. NEW LOG FILE.\n")
		logFile.close()
		
# Launches a shell script to scan for bluetooth beacons in area.
# Defaults to scanning for 10 seconds. Optionally pass parameter to set duration.
# durationInSeconds is duration of scan, not of method. May be a couple seconds extra.
# The script ibeacon_scan.sh does not return duplicate packets.
# Return results from ibeacon_scan.sh. Format is "uuid major minor power rssi" on each line.
#   Does not filter for uuid's
# If ibeacon_scan.sh gives a return code indicating error, we do not return. We log, and quit.
def scanForiBeacons(durationInSeconds=10):
	# Need os.setsid argument for both script and all its children (hcidump) to be killed later.
	proc = Popen( ['./ibeacon_scan.sh'],
				preexec_fn = os.setsid,
				stdout = PIPE,
				stderr = PIPE )
	time.sleep(durationInSeconds)

	os.killpg(proc.pid,signal.SIGINT)
	time.sleep(1) # Make sure the process has been killed

	procReturnCode = proc.poll()
	if(procReturnCode == None or procReturnCode == 1):
	# Either proc hasn't stopped or an error occured with bluetooth, respectively.
		writeToLogFile("ERROR: ibeacon_scan.sh returned code: "
				+ str(procReturnCode) + "\tSTDERR: "
				+ proc.stderr.read().decode('UTF-8') )
		sys.exit(1) # Quit, to prevent anything 'bad'... will resume whenever pi restarts script.
	else:
	# All is well. Return the data.
		returnVal = proc.stdout.read().decode('UTF-8')
		proc.stdout.close()
		proc.stderr.close() # Just to be safe.
		return returnVal

# Input is a string in format "data1 data2 ... dataK\ndata1 data2 ... dataK\n ..."
# If there are any empty lines, they'll be skipped.
# Returns 2D list of [indivBeacons, beaconData]
def parseDataIntoList(beaconsDataString):
	beaconsList = beaconsDataString.split("\n")

	splitBeaconsList = []
	for indivBeaconData in beaconsList:
		if(indivBeaconData != ''):
			splitBeaconsList.append(indivBeaconData.split(" "))

	return splitBeaconsList

# Input is 2D list of [indivBeacons, beaconData], where beaconData is [UUID, Major, Minor,...]
# Returns 2D list, with only beacons matching our UUID, only with [Major, Minor].
# Assumes all sublists are properly formatted... no checking.
def filterBeaconsByUUID(beaconsList):
	filteredBeaconsList = []
	for indivBeaconList in beaconsList:
		if(indivBeaconList[0].upper() == ACCEPTED_UUID.upper()): # Ignore case
			filteredBeaconsList.append(indivBeaconList[1:3]) # Just keep major,minor
	return filteredBeaconsList

# Returns json object continaing time & location info
def getJSONforUpload():
	json = { str(int(time.time()))+"000" :{
	# Need to pad time value, until standardiz number of decimals with team.
					"latitude" : PI_LATITUDE,
					"longitude" : PI_LONGITUDE,
					"message" : PI_LOC_MESSAGE
					}
		}
	return json

# Uploads each observation to firebase
# If exception is raised, then logs, and quits script.
# Input is reference to "observations" directory in db, and 2D list of [beacons, data],
# 	where data is [major, minor]
# This is a blocking operation. Execution will be slow while waiting for upload to complete.
def upload(dbRef, beaconsList):
	jsonData = getJSONforUpload() # Get data with current time and location for upload.

	for beacon in beaconsList: # Need to go one at a time, else will erase existing entries.
		majorMinorStr = beacon[0] + ":" + beacon[1]
		childRef = dbRef.child(majorMinorStr)
		try:
			childRef.update(jsonData) # Update, not set. Set will erase existing entries.
		except Exception as e:
			writeToLogFile("ERROR: exception during upload. Message: " + e)
			sys.exit(1) # Just quit. We'll try again next time script is run...

# "MAIN" i.e. where execution starts.
# Loops infinitely, until exception raised or system/user kills.
writeToLogFile("START")
dbReference = initializeFirebase()
while True:
	rawBeaconsData = scanForiBeacons(SCAN_LENGTH_SECONDS)
	allBeaconsList = parseDataIntoList(rawBeaconsData)
	filteredBeaconsList = filterBeaconsByUUID(allBeaconsList)
	upload(dbReference, filteredBeaconsList)
	writeToLogFile("UPLOAD: Upload Complete. Number of observations uploaded: "
			+ str(len(filteredBeaconsList)) )
	time.sleep(SECONDS_BETWEEN_SCANS)
