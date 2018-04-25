# senior-project-317: Campus Bluetooth Tag Network
## Hardware Documentation
This folder includes information on the BLE tags which we ordered via Alibaba, including all of the information the manufacturer gave us, and some information on another product we tried using, Estimote.

See the file [Beacon IDs list](./Beacon%20IDs%20list.xlsx) for the list of all beacons and their Major:Minor IDs.

## Estimote
The first BLE tag supplier we tried was Estimote https://estimote.com/

We ordered the 10 pack of Stickers which cost $99.

There were several issues with Estimote:
- Estimote is a hardware and software company. They make it so that you can't really work with their hardware without their software, which they force you to pay for after some number of beacons. But, we didn't want to pay. This turned out to be a problem with almost every domestic company we found - their systems required you to use and pay for their software service.
- The battery died quickly. All the stickers were daed within one semester. Supposedly, battery life would have improved if we'd used Estimotes custom SDK, but we wanted to customize it.

## Alibaba Order
To get around the problems with products like Estimote, we later ordered two shipments of beacons through Alibaba.

We ordered model iB004N from "Shenzhen Ankhmaway Electronic Technology Co." https://ankhmaway.en.alibaba.com/ . This model broadcasts a BLE signal and also incldues a small buzzer/speaker.

The sample order cost $11.00 per tag. For the second order, they gave us a discount and we paid $8.50 per tag, plus $1.00 for logo printing, plus shipping.

Overall, we were happy with the company's service. The device batteries should last about 1.5 to 2 years. The tag's software was fully customizable. We were even able to have them print a logo on each tag. But, the tags are clearly larger than those used by Tile or TrackR. 

## Instructions
All of the files provided by the Alibaba supplier are included here. 

The manufactuer suggests using this app to set any parameters: https://itunes.apple.com/us/app/ebeacon-ibeacon-eddystone/id7302
- Tags are currently set to not be available for pairing until they are tapped. Tap one until you hear the buzzer go off, and it will be in pairing mode for 45 sec.
- To make the app easier to use, in the settings menu you can tell it to automatically connect when possible. This will make it so you don't have to sort through the many bluetooth devices nearby.
- The default password is 0x666666. We have not changed it.

We chose to use the iBeacon protocol. There was no strong preference for iBeacon vs Eddystone, it's just what we chose.	We set the iBeacon UUID to the randomly generated string D0D3FA86-CA76-45EC-9BD9-6AF4F6016926. All of our tags were set to this UUID.
