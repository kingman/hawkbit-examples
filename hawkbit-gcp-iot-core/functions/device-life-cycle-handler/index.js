/**
 * Triggered from a message on a Cloud Pub/Sub topic.
 *
 * @param {!Object} event Event payload.
 * @param {!Object} context Metadata for the event.
 */

/*
process.env.HAWKBIT_INTEGRATOR_URL = 35.228.102.236
process.env.HAWKBIT_INTEGRATOR_PORT = 8083
process.env.HAWKBIT_INTEGRATOR_PATH = /gcp
process.env.HAWKBIT_INTEGRATOR_USER = user
process.env.HAWKBIT_INTEGRATOR_PASSWORD =
const HAWKBIT_NOTIFICATION_URL = "http://:/gcp"
*/
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();

const http = require('http');

const options = {
  hostname: process.env.HAWKBIT_INTEGRATOR_URL,
  port: parseInt(process.env.HAWKBIT_INTEGRATOR_PORT),
  path: process.env.HAWKBIT_INTEGRATOR_PATH,
  method: 'GET',
  headers: {
    'Authorization': 'Basic ' + new Buffer(process.env.HAWKBIT_INTEGRATOR_USER + ':' + process.env.HAWKBIT_INTEGRATOR_PASSWORD).toString('base64')
  }
};


exports.handleEvent = (event, context) => {
  const message = event.data
    ? Buffer.from(event.data, 'base64').toString()
    : 'Hello, World';


  const req = http.request(options, (res) => {
      res.on('data', (d) => {
      console.log("create device in hawkbit")
    });
  });
  req.on('error', error => {
    console.error(error)
  });
  req.end();

  var msgObj = JSON.parse(message);
  deviceId = msgObj.protoPayload.response.id;
  writeToFirestore(deviceId)
  .then(() => console.log("Created device in firestore", deviceId))
  .catch(console.error)
}

function writeToFirestore(deviceId) {
  const docRef = db.collection('devices').doc(`${deviceId}`);
  return docRef.set({
    acl: {
      owner:[]
    },
    state: {}
  }, {merge:true});
}