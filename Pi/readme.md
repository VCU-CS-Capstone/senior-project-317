This repo contains the code and instructions used to create an iBeacon scanner out of our Raspberry Pis

The Pis are setup to be plug-and-play. Simply plug them in, and they will start scanning and uploading.

See pi_setup.md for instructions on setting up the Pi.

hostnames: cmsc452-317pi1 , cmsc452-317pi2
(However, unless DNS is setup, you'll need IPs. See pi_setup.md)
Also, note that VCU SafeNet does NOT allow peer-to-peer ssh. So, you'll have to either connect to external monitor on campus, or take the Pi off campus to access.

Note that several keys or files are missing, because we don't want to publish passwords on GitHub.
This includes: 
serviceAccountKey.json - Can get a new one off of firebase
wpa_supplicant.conf - WiFi password deleted. Any VCU user will do.
Pi passwords - ask Justin


Description of files:
crontab - Contains jobs to be placed in root's crontab. Runs our script on reboot. Also reboots Pi periodically, in case our script had issues and exited. Edit how often by editing this file.

ibeacon_scan.sh - A helper script. Performs the actual scanning. Called by tagScanAndUpload.py

tagScanAndUpload.py - The main file. Call this script. Must be run as root, python 3 or above. In Raspbian, this is called 'sudo python3 tagScanAndUpload.py'. File currently assumes everything is stored in same directory.
See this file for description of how we scan, how often we scan, etc.
	Script produces a log file. Use this for troubleshooting.