# senior-project-317: Campus Bluetooth Tag Network
## Pi
We designed a novel feature for our system: implementing 'fixed scanners' using Raspberry Pis.

Unlike products like Tile, our product was meant for particular campus. We use this fact and install scanners around campus, inside buildings, to scan for beacons in our network. This helps overcome the issue of GPS imprecision, **especially indoors**. For example, observations reported from these scanners may include messages like "3rd floor of library" or "room 301", which will be displayed to users.

## Pi Passwords 
All Pis are currently setup with these credentials **(This should be deleted and the passwords changed before Pis are put back into use!!)**
- Username: pi
- Password: MowingLabDahlbergsBest

## Pi setup
See [Pi_Setup](./pi_setup.md) instructions on how to set up new fixed scanners.

## List of Pis
See [Locations](./locations.md) for a list of Pis and their programmed locations.

## How to use a Pi (after setup)
The Pis are setup to be plug-and-play. Simply plug them in, and they will start scanning and uploading.

Note: Pis are setup to restart every 90min. So, it is possible they'll turn off when you don't expect (see info below regarding the crontab file).

## Contents
- wpa_supplicant.conf - WiFi credentials. Currently setup to VCU SafeNet.
- AddTrustExternalCARoot.crt - Required for VCU SafeNet wireless. See wpa_supplicant.conf for instructions.
- crontab - Contains jobs to be placed in root's crontab. Runs our script on reboot. Also reboots Pi periodically in case our script had issues and exited; edit how often by editing this file.
- ibeacon_scan.sh - A helper script which performs the actual scanning. Called by tagScanAndUpload.py
- tagScanAndUpload.py - The main script. 
	- You may manually perform scanning by running this script. It must be run as root with Python 3. In Raspbian, this is called as `sudo python3 tagScanAndUpload.py`. 
	- This file has various editable parameters declared at the top. See this file for description of how we scan, how often we scan, etc..
	- This script produces a log file. Use this for troubleshooting.
	
Note that several files or lines in files are missing because we don't want to publish passwords on GitHub.
This includes: 
- serviceAccountKey.json - A copy can be created/downloaded from the Firebase website.
- wpa_supplicant.conf - The WiFi credentials are deleted. Technically any VCU user will do, though VCU IT required that we use a specially created group account.
