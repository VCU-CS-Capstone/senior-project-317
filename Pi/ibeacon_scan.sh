#!/bin/bash

# Adapated from https://gist.github.com/elliotlarson/1e637da6613dbe3e777c
# 	Which was adapted from http://developer.radiusnetworks.com/ibeacon/idk/ibeacon_scan

# Process:
# 1. start hcitool lescan
# 2. begin reading from hcidump
# 3. Assemble packets from multiline stdin
# 4. For each packet, process into uuid, major, minor, power, and RSSI
# 5. When finished (SIGINT): makes sure to close out hcitool

# Output format: uuid major minor power rssi
# Exit code non-zero means error. 1 means hcitool not starting.

# Needs to run as sudo
# Does not report duplicate packets - i.e. same beacon, with same power,rssi.

halt_hcitool_lescan() {
  sudo pkill --signal SIGINT hcitool
}

trap halt_hcitool_lescan INT

process_complete_packet() {
  local packet=${1//[\ |>]/}

  # only work with iBeacon packets
  if [[ ! $packet =~ ^043E2[AB]0201.{18}0201.{10}0215 ]]; then
    return
  fi

  uuid="${packet:46:8}-${packet:54:4}-${packet:58:4}-${packet:62:4}-${packet:66:12}"
  major=$((0x${packet:78:4}))
  minor=$((0x${packet:82:4}))
  power=$[$((0x${packet:86:2})) - 256]
  rssi=$[$((0x${packet:88:2})) - 256]

  echo "$uuid $major $minor $power $rssi"
}

read_blescan_packet_dump() {
  # packets span multiple lines and need to be built up
  packet=""
  while read line; do
    # packets start with ">"
    if [[ $line =~ ^\> ]]; then
      # process the completed packet (unless this is the first time through)
      if [ "$packet" ]; then
        process_complete_packet "$packet"
      fi
      # start the new packet
      packet=$line
    else
      # continue building the packet
      packet="$packet $line"
    fi
  done
}

# begin BLE scanning
sudo hcitool lescan > /dev/null & #Add --duplicates after lescan if want observations repeated
sleep 1
# make sure the scan started
if [ "$(pidof hcitool)" ]; then
  # start the scan packet dump and process the stream
  sudo hcidump --raw | read_blescan_packet_dump
else
  # the hcitool/hcidump isn't starting up correctly
  # echo "ERROR" >&2 
  exit 1 # Send exit code 1
fi
