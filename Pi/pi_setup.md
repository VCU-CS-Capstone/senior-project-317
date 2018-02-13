To access the Pis: Either connect them to monitors and run 'hostname -I' to get local IPs, or go through router-admin.

These tutorials are helpful:
https://www.raspberrypi.org/documentation/raspbian/
https://www.raspberrypi.org/documentation/configuration/

Initial Pi setup:
	You'll need to either connect to monitor and keyboard, or SSH in.
	Install Raspbian Lite / Jessie Lite, from an SD card.
	Run 'sudo raspi-config'
		Set timezone
		Enabled SSH, disabled VNC
	Changed password
	Changed hostname
	Set sudo to require a password.
	Run updates: 'sudo apt-get update' , 'sudo apt-get dist-upgrade'
	Add daily ssh security update to root's cron job. (See crontab file.) 
	See wpa_supplicant.conf for instructions on connecting to VCU wifi

To get our script running:
	'sudo apt-get install python3-pip'
	'sudo pip3 install firebase-admin'
	'sudo apt-get install bluez bluez-hcidump'
	Place files into some directory on the machine.
	Make ibeacon_scan.sh executable with 'sudo chmod +x ibeacon_scan.sh'
	Set various parameters (filepaths, location info) in tagScanAndUpload.py
	Run 'sudo su' and then 'crontab -e'. Place info from our crontab file into there.
	
	Finally, you can run 'sudo python3 tagScanAndupload.py", or just plug&play the Pi.
	It should start uploading! Either look at Firebase real-time website to verify, or check log file.
	