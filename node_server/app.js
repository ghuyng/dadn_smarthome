const express = require('express')
const http = require('http')
const app = express()
// const bodyParser = require('body-parser')
const clientMqtt = require('./mqttService')

app.use(express.json())
app.use(
    express.urlencoded({
	extended: true,
    })
)

const hostname = '0.0.0.0'
const port = 3000


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
  res.status(200).json({
    message: "good"
  })
})

app.listen(port, () => {
  console.log(`Server running at http://${hostname}:${port}/`)
})