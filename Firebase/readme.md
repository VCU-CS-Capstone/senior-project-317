# senior-project-317: Campus Bluetooth Tag Network
## Firebase
This folder includes information some lessons learned from our use of Firebase as well as some code we created, but did not use, to automatically edit the database.

Firebase includes services for Database as well as Authentication, Analytics, and Backend hosting. Our project took advantage of the Database and Authentication services.

Dr. Bulut should still have access to our Firebase instance in the future. If not, setup is simple enough; simply visit Firebase and follow the tutorials.

Note that our project worked with the Firebase Realtime Database. By the time we finished, Firebase was rolling out Cloud Firestore. So, eventually, these instructions may be outdated.

## Tips for Firebase
For any serious use of the database, the online GUI will not sufficient. See https://firebase.google.com/docs/cli/ for information on how to setup a CLI interface.

The CLI interface is also useful for troubleshooting. For example, the CLI can be used to profile database usage and reduce data consumption https://firebase.google.com/docs/database/usage/profile

If you are considering using a different database service, note that you'll also probably need to migrate your Authentication service, since we use Firebase for both.

## Firebase functions
We implemented code which would run using the Firebase "Functions" product to automatically edit the database. Our idea was to prune the database, limiting the number of observations per tag. 

Later, we realized that we did not need to worry about database size given our current number of users. Also, we kept exceeding the quota for Firebase function calls because we set ours up to run every time an entry was added to the database. Any future implementation should instead use the option for REST calls.

Instructions on how to get started using Firebase functions, including how to deploy new ones: https://firebase.google.com/docs/functions/get-started
How to write/edit functions: https://firebase.google.com/docs/functions/database-events

The code for our functions is all stored in [index.js](./functions/index.js)
