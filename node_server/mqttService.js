const { json } = require('express')
const mqtt = require('mqtt')

const uname = "CSE_BBC"
const uname1 = "CSE_BBC1"
// const uname = "ghuyng"
// const uname1 = "ghuyng"
const clientBBC = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: uname,
  password: "",
  reconnectPeriod: 0
})

const clientBBC1 = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: uname1,
  password: "",
  reconnectPeriod: 0
})


const relayTopic = uname1 + '/feeds/bk-iot-relay'
const lightSensorTopic = uname1 + '/feeds/bk-iot-light'
const magneticTopic = uname + '/feeds/bk-iot-magnetic'
const buzzerTopic = uname + '/feeds/bk-iot-speaker'

const { io } = require('./socket')
const { admin } = require('./firebase-config')
const { defaultLight, defaultDoor} = require('./track-device')

const database = admin.database()
var registrationtoken = 'exDJ9ZTZRk6AUiwCtNJ82C:APA91bHb_GmqllUFihxRRfD-GqIgbH_LEhUWquyclZlUgAfn9qFXTW7zFEpOXu1Cg_wIq8wHglO6zG-cIkmGgIKog75KXuPpG7iPvY8b0fpr0Bs8CiR2B38mCHxMc7Hqz12jnGaJ9DR1'
// var registrationtoken = ''
const sendAlert = (regToken => {
  const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
  };
  const message = {
    notification: {
      title: 'SmartHome ALERT',
      body: 'ALERT!! YOUR DOOR WAS OPENED',
      click_action: 'VIEW_NOTIFICATION'
    }
  };
  admin.messaging().sendToDevice(regToken, message, notification_options)
      .then( res => {
        console.log(res)
      })
      .catch( error => {
          console.log(error);
      });
})

const saveRegToken = (regToken => {
  registrationtoken = regToken
})

clientBBC.on('error', (error) =>{
  console.log(error.message)
})
clientBBC.on('connect', ()=>{
  console.log('connected to server CSE_BBC')
  clientBBC.subscribe(magneticTopic)
  clientBBC.subscribe(buzzerTopic)
})

clientBBC.on('message', (topic, message) =>{
  console.log(`topic : ${topic}, message : ${message}`)
  const msg = JSON.parse(message)
  if (defaultDoor.status && topic == magneticTopic && msg["data"] == "1"){
    console.log("heare")
    changeAlert(1000)
    sendAlert(registrationtoken)
  }
})

clientBBC1.on('error', (error) =>{
  console.log(error.message)
})
clientBBC1.on('connect', ()=>{
  console.log('connected to server CSE_BBC1')
  clientBBC1.subscribe(relayTopic)
  clientBBC1.subscribe(lightSensorTopic)
})

clientBBC1.on('message', (topic, message) =>{
  console.log(`topic : ${topic}, message : ${message}`)
  if (topic == lightSensorTopic){
    const jsonObj = JSON.parse(message)
    if (jsonObj["data"] < defaultLight.lowLimit && !defaultLight.status
      || jsonObj["data"] > defaultLight.highLimit && defaultLight.status){
      changeRelay({
        data: !defaultLight.status,
        room: defaultLight.room,
        device: defaultLight.name
      })
    }
  }
})

function changeRelay(message){
  if (message.type != 'Door'){
    clientBBC1
      .publish(relayTopic, JSON.stringify({
        "id":"11",
        "name":"RELAY",
        "data": `${message.data? "1" : "0"}`,
        "unit": ""
      }), err => {
        if (err) {
          //Handle error

          return
        }
      })
  }
    const dbRef = database.ref(`Room/${message.room}/${message.device}`)
    dbRef.child('Status').set(message.data)
    dbRef.child(`${message.data? "On": "Off"}`).get().then((snapshot) => {
      if (snapshot.exists()) {
        var timeList = snapshot.val()
        var currentTime = new Date()
        timeList.push(`${currentTime.getFullYear()}-${currentTime.getMonth()}-${currentTime.getDate()} ${currentTime.getHours()}:${currentTime.getMinutes()}:${currentTime.getSeconds()}`)
        dbRef.child(`${message.data? "On": "Off"}`).set(timeList)
        console.log(timeList);
      } else {
        console.log("No data available");
      }
    }).catch((error) => {
      console.error(error);
    });
}

function changeAlert(val) {
    var jsonObj = {
      "id":"2",
      "name":"SPEAKER",
      "data": val.toString(),
      "unit":""
    }
    clientBBC.publish(buzzerTopic, JSON.stringify(jsonObj))
}

module.exports = {
    changeRelay,
    saveRegToken,
    changeAlert
}
