// const { server } = require('./app')
const io = require('socket.io')()


io.on('connection', (socket) => {
  console.log('user connected')

  socket.on('switchRelay', (message) => {
    console.log(message)
  })


})

module.exports = {
    io: io
}