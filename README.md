# senior-project-317: Campus Bluetooth Tag Network
VCU CS Senior Project 317, Fall 2017 - Spring 2018. 

Faculty adviser/mentor: Dr. Bulut

Team members: Nicholas Bennnett, Jordan Mays-Rowland, Justin Yirka

## Contents
- Android code and documentation is in [Android](./Android)
- iOS code and documentation is in [SeniorProjectBYM](./SeniorProjectBYM)
- Raspberry Pi code and documentation is in [Pi](./Pi)
- Documentation, as well as some code which we ended up not using, for our Firebase instance is in [Firebase](./Firebase)
- Assignment and presentations related to the Senior Design class are in [Assignments and Presentations](./Assignments%20and%20Presentations)
- Documentation regarding the beacons is in [Hardware Documentation](./Hardware%20Documentation)
  - This includes a list of all tags and their Major:Minor IDs [Beacon IDs List](./Hardware%20Documentation/Beacon%20IDs%20list.xlsx)

## Project goal 
Design an open-source alternative to commercial item-tracking products such as Tile and TrackR, which use Bluetooth Low Energy (BLE) tags to track and locate lost items. The system should include an Android and iOS app for users.

## Description of product
We developed an Android and iOS app which interacted with a Google Firebase database, succesfully replicating many of the features of existing products like Tile.

A significant novel feature was the design of 'fixed scanners' using Raspberry Pis.

There were several features we considered implementing, especially for security, but did not have time for. You can find exmaples of these ideas by scrolling through our old meeting notes.

And, there are some improvements which should be made in the future.
- Change passwords and keys for everything from their default values, including for the database and the beacons.
- Setup stronger security in Firebase.
- Implement security and privacy suggestions from the paper included in the Assignment and Presentations folder.
- Continue improving the interfaces of the apps. Especially think of ways to bettery convey more information to the user.
- Fine-tune the frequency of scanning and uploading, and find ways to improve location precision (Dr. Bulut had suggested filtering by beacon signal strength, for example).
