const mqtt = require('mqtt')

const clientBBC = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: "CSE_BBC",
  password: "aio_<key of CSE_BBC>"
})

const clientBBC1 = mqtt.connect('mqtts://io.adafruit.com:8883',{
  username: "CSE_BBC1",
  password: "aio_<key of CSE_BBC1>"
})


const relayTopic = 'CSE_BBC1/feeds/bk-iot-relay'
const lightSensorTopic = 'CSE_BBC1/feeds/bk-iot-light'
const magneticTopic = 'CSE_BBC/feeds/bk-iot-magnetic'

clientBBC.on('error', (error) =>{
  console.log(error.message)
})
clientBBC.on('connect', ()=>{
  console.log('connected to server CSE_BBC')
  clientBBC.subscribe(magneticTopic)
})

clientBBC.on('message', (topic, message) =>{
  console.log(`topic : ${topic}, message : ${message}`)
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
})

function changeRelay(message){
    clientBBC1.publish(relayTopic, message)
}

module.exports = {
    changeRelay
}
