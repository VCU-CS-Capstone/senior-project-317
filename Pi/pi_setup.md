To access the Pis: Either connect them to monitors and run `hostname -I` to get local IPs, or go through router-admin.

# These tutorials are helpful:
https://www.raspberrypi.org/documentation/raspbian/
https://www.raspberrypi.org/documentation/configuration/

# Initial Pi setup:
1. You'll need to either connect to monitor and keyboard, or SSH in.
2. Install Raspbian Lite / Jessie Lite, from an SD card.
3. Run `sudo raspi-config`
		- Set timezone
		- Enabled SSH, disabled VNC
4. Changed password
5. Changed hostname
6. Set sudo to require a password.
7. Run updates: `sudo apt-get update` , `sudo apt-get dist-upgrade`
8. Add daily ssh security update to root's cron job. (See crontab file.) 
9. See wpa_supplicant.conf for instructions on connecting to VCU wifi

# To get our script running:
1. `sudo apt-get install python3-pip`
2. `sudo pip3 install firebase-admin`
3. `sudo apt-get install bluez bluez-hcidump`
4. Place files into some directory on the machine.
5. Make ibeacon_scan.sh executable with `sudo chmod +x ibeacon_scan.sh`
6. Set various parameters (filepaths, location info) in tagScanAndUpload.py
7. Run `sudo su` and then `crontab -e`. Place info from our crontab file into there.
	
Finally, you can run `sudo python3 tagScanAndupload.py`, or just plug&play the Pi. It should start uploading! Either look at Firebase real-time website to verify, or check log file.
	