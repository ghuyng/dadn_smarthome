const { json } = require('express')
const mqtt = require('mqtt')

// const uname = "CSE_BBC"
// const uname1 = "CSE_BBC1"
const uname = "ghuyng"
const uname1 = "ghuyng"
const clientBBC = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: uname,
  password: "aio_xOdz02Yaoj8MOYscLABbyCOLwLXM",
  reconnectPeriod: 0
})

const clientBBC1 = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: uname1,
  password: "aio_xOdz02Yaoj8MOYscLABbyCOLwLXM",
  reconnectPeriod: 0
})


const relayTopic = uname1 + '/feeds/bk-iot-relay'
const lightSensorTopic = uname1 + '/feeds/bk-iot-light'
const magneticTopic = uname + '/feeds/bk-iot-magnetic'
const buzzerTopic = uname + '/feeds/bk-iot-speaker'
var isLocked = true
var lightStatus = false
const lightLowerBound = 100 
const lightUpperBound = 700 

const { io } = require('./socket')
const { admin } = require('./firebase-config')

const database = admin.database()
var registrationtoken = ''
const sendAlert = (regToken => {
  const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
  };
  const message = {
    notification: {
      title: 'SmartHome ALERT',
      body: 'ALERT!! YOUR DOOR WAS OPENED'
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

clientBBC.on('message', async (topic, message) =>{
  // const sockets = await io.fetchSockets()
  console.log(`topic : ${topic}, message : ${message}`)
  if (isLocked && topic == magneticTopic){
    console.log("here")
    jsonObj = {
      "id":"2",
      "name":"SPEAKER",
      "data":"500",
      "unit":""
    }
    clientBBC.publish(buzzerTopic, JSON.stringify(jsonObj))
    sendAlert(registrationtoken)
    // for (const socket of sockets){
    //   socket.emit("alert")
    // }
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
    // if (jsonObj["data"] < lightLowerBound && !lightStatus){
    if (jsonObj["data"] < lightLowerBound){
      lightStatus = !lightStatus;
      clientBBC1.publish(relayTopic, JSON.stringify({
        "id":"11",
        "name":"RELAY",
        "data":"1",
        "unit":""
      }))
    }
    // else if (jsonObj["data"] > lightUpperBound && lightStatus){
    else if (jsonObj["data"] > lightUpperBound){
      lightStatus = !lightStatus;
      clientBBC1.publish(relayTopic, JSON.stringify({
        "id":"11",
        "name":"RELAY",
        "data":"0",
        "unit":""
      }))
    }
  }
})

function changeRelay(message){
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
        database.ref(`Room/${message.room}/${message.device}/Status`).set(message.data)
      })
}

module.exports = {
    changeRelay,
    saveRegToken
}
