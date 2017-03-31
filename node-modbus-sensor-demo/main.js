var gpio = require('gpio');
var net = require('net');
var modbus = require('modbus-tcp');
var modbusServer = new modbus.Server();
var fs = require('fs')

const port = 8888;
const buzzerCoil = 665
const ledCoil = 666


var exportGpio = function (num) {
  return gpio.export(num, {
     direction: 'out',
     interval: 200,
     ready: function() {
       console.log('GPIO ' + num + 'exported')
     }
  })
};

var gpioBuzzer = exportGpio(17);
var gpioLed = exportGpio(18);

var tcpServer = net.createServer();

tcpServer.listen(port,function(){
    console.log('TCP Socket bound to port '+port);
});

tcpServer.on('connection', function(socket){
    console.log('client has connected');
    modbusServer.pipe(socket);

    socket.on('error', function(e){
        console.log('Connection error: '+e);
        socket.destroy();
    });

    socket.on('close', function(e){
        console.log('Client has closed connection.');
    });
});

var writeSingleCoil = function (from,data,reply) {
  console.log('requested write coil: ' + from)
  console.log('value: ' + data[0])
  console.log('requested read coil: ' + from)
  var writeGpio = function(gpio, val) {
    gpio.setDirection('out', () => {
	gpio.set(val)
	})
  }
  if (from == buzzerCoil) {+
    writeGpio(gpioBuzzer, data[0])
  } else if (from == ledCoil) {
    writeGpio(gpioLed, data[0])
  }
  reply()
}

var readCoils = function (from,to,reply) {
  console.log('requested read coil: ' + from)
  var readGpio = function(gpio) {
    reply(null, [gpio.value])
  }
  if (from == buzzerCoil) {+
    readGpio(gpioBuzzer)
  } else if (from == ledCoil) {
    readGpio(gpioLed)
  } else {
  reply(null, [0])
  }
}

var readHoldingRegisters = function (from,to,reply) {
    console.log('Read holding registers '+from+'-'+to);
    var values = [6]; // sample values just to see if it works.
    return reply(null,bufferify(values));
}

modbusServer.on('write-single-coil', writeSingleCoil)
modbusServer.on('read-coils', readCoils)
modbusServer.on('read-holding-registers', readHoldingRegisters)

function bufferify(itemsArray) {
    // When client reads values, have to supply an
    // array of Buffers (not just an array of numbers) to the reply function.
    var n = itemsArray.length;
    var registers = [];
    for (var i=0; i<n; i++) {
        registers[i] = Buffer.alloc(2);
        registers[i].writeInt16BE(itemsArray[i],0);
    }
    return registers;
}
