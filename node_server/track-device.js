const {admin} = require('./firebase-config')

const defaultLight = {
  room: "Garage",
  name: "Xiaomi Smart Light",
  status: false,
  lowLimit: 100,
  highLimit: 100
}

const defaultDoor = {
  room: "Main Doors",
  name: "Front Door",
  status: false // true = locked, false = unlocked
}

var lightStatusRef = admin.database().ref(`Room/${defaultLight.room}/${defaultLight.name}`);
lightStatusRef.on('value', (snapshot) => {
  const data = snapshot.val();
  defaultLight.status = data.Status
  defaultLight.lowLimit = data.TurnOffValue
  defaultLight.highLimit = data.TurnOnValue
});

var doorStatusRef = admin.database().ref(`Room/${defaultDoor.room}/${defaultDoor.name}`);
doorStatusRef.on('value', (snapshot) => {
  const data = snapshot.val();
  defaultDoor.status = data.Status
  console.log(defaultDoor.status)
});

module.exports = {
  defaultLight,
  defaultDoor
}