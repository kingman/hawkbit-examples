/**
 * Triggered from a message on a Cloud Pub/Sub topic.
 *
 * @param {!Object} event Event payload.
 * @param {!Object} context Metadata for the event.
 */
const Firestore = require('@google-cloud/firestore');
const firestore = new Firestore();

exports.eventUpdate = (event, context) => {
  const pubsubMessage = event.data;
  const deviceId = event.attributes.deviceId;
  var msgObj = JSON.parse(Buffer.from(pubsubMessage, 'base64').toString());
  writeToFirestore(deviceId, msgObj)
  .then(() => console.log("Value written to firestore for device: %s", deviceId))
  .catch(console.error);
};

function writeToFirestore(deviceId, data) {
  let documentRef = firestore.doc(`devices/${deviceId}`);
  return documentRef.set({state: data}, {merge:true});
}