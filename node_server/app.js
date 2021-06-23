const express = require('express')
const http = require('http')
const app = express()
const server = http.createServer(app)
// const bodyParser = require('body-parser')
const clientMqtt = require('./mqttService')
const { admin } = require('./firebase-config')

const { io } = require('./socket')
io.listen(server)
app.use(express.json())
app.use(
    express.urlencoded({
	extended: true,
    })
)

const hostname = '127.0.0.1'
const port = 3000

server.listen(port, () => {
  console.log(`Server running at http://${hostname}:${port}/`)
})


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
  clientMqtt.changeRelay(JSON.stringify(req.body))
  sendAlert(registrationtoken)
  res.status(200).json({
    message: "good"
  })
})

app.post('/set-registrationtoken', (req, res) => {
  console.log(req.body)
  registrationtoken = req.body.message
  res.status(200).json({
    message: "good"
  })
})