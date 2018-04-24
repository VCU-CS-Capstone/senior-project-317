This repo contains the code and instructions used to create an iBeacon scanner out of our Raspberry Pis.

The Pis are setup to be plug-and-play. Simply plug them in, and they will start scanning and uploading.

Note: Pis are setup to restart every 90min. So, it is possible they'll turn off when you don't expect (see crontab file).

See pi_setup.md for instructions on setting up a new Pi.

You'll need the local IPs to ssh in (though note, VCU blocks ssh on SafeNet).
Otherwise, you'll need to plug in to monitor and keyboard and run `hostname -I`, or you'll need DNS set up.

Note that several keys or files are missing, because we don't want to publish passwords on GitHub.
This includes: 
- serviceAccountKey.json - Can get a new one off of firebase
- wpa_supplicant.conf - WiFi password deleted. Any VCU user will do.
- Pi passwords - ask Justin

Description of files:
- wpa_supplicant.conf - Info for connecting Pi to VCU SafeNet.
- AddTrustExternalCARoot.crt - Used to connect to VCU SafeNet. Wee wpa_supplicant.conf for instructions.
- crontab - Contains jobs to be placed in root's crontab. Runs our script on reboot. Also reboots Pi periodically, in case our script had issues and exited. Edit how often by editing this file.
- ibeacon_scan.sh - A helper script. Performs the actual scanning. Called by tagScanAndUpload.py
- tagScanAndUpload.py - The main file. 
	- Call this script. Must be run as root, Python 3 or above. In Raspbian, this is called as `sudo python3 tagScanAndUpload.py`. 
	- This file has various editable parameters declared at the top. See this file for description of how we scan, how often we scan, etc.. 
	- This script produces a log file. Use this for troubleshooting.
	
Protip: If you manually kill either ibeacon_scan.sh or tagScanAndUpload.py , you may have left a child process hcidump running.
Run `pidof hcidump`. If any PIDs are output, run `sudo pkill hcidump`, then `sudo hciconfig hci0 down`, then `sudo hciconfig hci0 up`.