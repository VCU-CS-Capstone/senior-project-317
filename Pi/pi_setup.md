To access the Pis: Either connect them to monitors and run 'hostname -I' to get local IPs, or go through router-admin.

Initial Pi setup (specific commands can be found thru below links):
https://www.raspberrypi.org/documentation/raspbian/
https://www.raspberrypi.org/documentation/configuration/
	You'll need to either connect to monitor and keyboard, or SSH in.
	Install Raspbian Lite / Jessie Lite.
	Run 'sudo raspi-config'
		Set timezone
		Enabled SSH, disabled VNC
	Changed password
	Changed hostname
	Set sudo to require a password.
	Run updates: 'sudo apt-get update' , 'sudo apt-get dist-upgrade'
	Add daily ssh security update to cron job. See crontab file. 
	See wpa_supplicant.conf for instructions on connecting to VCU wifi

To get our script running:
	'sudo apt-get install python3-pip'
	'sudo pip3 install firebase-admin'
	Place files...
	make file executable...
	crontab...
	Set parameters.
	
	Sudo apt-get install pi-bluetooth
	Sudo apt-get install libglib2.0-dev
	Sudo pip3 install pybluez[ble]
	