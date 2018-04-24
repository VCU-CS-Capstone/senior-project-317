# Pi Setup Instructions

## These tutorials are helpful:
https://www.raspberrypi.org/learning/software-guide/quickstart/
https://www.raspberrypi.org/documentation/configuration/

## Easiest method:
Image the SD card of an already setup Pi onto a new SD card for the new Pi.

## Initial Pi setup:
1. You'll need to connect with a monitor and keyboard.
2. Install Raspbian Lite / Jessie Lite from an SD card (which you formatted with 'Noobs' OS available online)
	- Note: The VCU library has SD card readers available.
	- Default login for Raspbian: user pi, password raspberry 
3. Run `sudo raspi-config`
	- Run 8: Update
	- Run 1: Set password.
	- Run 2: Network Options. Change hostname.
	- Run 3: Boot Options. Change to boot into CLI require login. Choose to wait for network at boot.
	- Run 4: Localization Options. Set timezone.
	- Run 5: Interfacing Options. Enable ssh, Disable VNC.
4. Run `hostname -I` to get local IP. Now, you can finish everything else via ssh.
	- **Note that VCU SafeNet blocks ssh**. We had to take the Pis to our home networks, then return to school.
6. Set sudo to require a password by running `sudo nano /etc/sudoers.d/010_pi-nopasswd` and edit the file to include `pi ALL=(ALL) PASSWD: ALL`
7. Run updates: `sudo apt-get update` , `sudo apt-get dist-upgrade`
8. Add daily ssh security update to root's cron job. (See crontab file.) 
9. See wpa_supplicant.conf for instructions on connecting to VCU wifi

## To install script:
1. `sudo apt-get install python3-pip`
2. `sudo pip3 install firebase-admin`
3. `sudo apt-get install bluez bluez-hcidump`
4. Place files into some directory on the machine.
5. Make ibeacon_scan.sh executable with `sudo chmod +x ibeacon_scan.sh`
6. Set various parameters (filepaths, location info) in tagScanAndUpload.py
7. Run `sudo su` and then `crontab -e`. Place info from our crontab file into there.
	
Finally, you can run `sudo python3 tagScanAndupload.py` or just restart the Pi. It should start uploading! Either look at Firebase real-time website to verify or check the log file.

Note: If you manually kill either ibeacon_scan.sh or tagScanAndUpload.py while they are running, you may have left a child process `hcidump` running. To check, run `pidof hcidump`. If any PIDs are output, then run `sudo pkill hcidump`, `sudo hciconfig hci0 down`, and then `sudo hciconfig hci0 up`.
