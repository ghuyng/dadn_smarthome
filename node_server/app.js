const express = require('express')
const http = require('http')
const app = express()
// const bodyParser = require('body-parser')
const mqtt = require('mqtt')
// const gcm = require('node-gcm')
const client = mqtt.connect('tcp://io.adafruit.com:1883',{
  username: "ghuyng",
  password: "aio_EUvR21ah5iw1BUu1PJ07n9wj7A4J"
})

app.use(express.json())
app.use(
    express.urlencoded({
	extended: true,
    })
)

const hostname = '0.0.0.0'
const port = 3000

client.on('connect', ()=>{
  client.subscribe('ghuyng/feeds/my-item')
  client.subscribe('ghuyng/feeds/bbc-led')
})

client.on('message', (topic, message) =>{
  console.log(`topic : ${topic}, message : ${message}`)
})

app.get('/', (req, res) => {
  // res.send('Hello World\n')
  res.status(200).json({
    id: "10",
    data: "hello"
  })
})

app.get('/user', (req, res) => {
  console.log('connected')
  res.status(200).json({
    message: "good"
  })
})

app.post('/turn-device', (req, res) =>{
  console.log(req.body)
  client.publish('ghuyng/feeds/my-item', JSON.stringify(req.body))
  res.status(200).json({
    message: "good"
  })
})
app.listen(port, () => {
  console.log(`Server running at http://${hostname}:${port}/`)
})