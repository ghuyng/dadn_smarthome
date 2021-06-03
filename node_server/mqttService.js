const { json } = require('express')
const mqtt = require('mqtt')

const clientBBC = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: "CSE_BBC",
  password: ""
})

const clientBBC1 = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: "CSE_BBC1",
  password: ""
})


const relayTopic = 'CSE_BBC1/feeds/bk-iot-relay'
const lightSensorTopic = 'CSE_BBC1/feeds/bk-iot-light'
const magneticTopic = 'CSE_BBC/feeds/bk-iot-magnetic'
const buzzerTopic = 'CSE_BBC/feeds/bk-iot-speaker'
var isLocked = true
var lightStatus = false
const lightLowerBound = 100 
const lightUpperBound = 700 

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
  if (isLocked && topic == magneticTopic){
    jsonObj = {
      "id":"2",
      "name":"SPEAKER",
      "data":"500",
      "unit":""
    }
    clientBBC.publish(buzzerTopic, JSON.stringify(jsonObj))
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
    clientBBC1.publish(relayTopic, message)
    isLocked = !isLocked
    jsonObj = {
      "id":"2",
      "name":"SPEAKER",
      "data":"500",
      "unit":""
    }
    clientBBC.publish(buzzerTopic, JSON.stringify(jsonObj))
}

module.exports = {
    changeRelay
}
