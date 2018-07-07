#include <PubSubClient.h>

#include <Dhcp.h>
#include <Dns.h>
#include <Ethernet.h>
#include <EthernetClient.h>
#include <EthernetServer.h>
#include <EthernetUdp.h>

#include <SPI.h>
#include <ctype.h>

#include "IRControle.hpp"

byte willQoS = 0;

boolean retain = false;
boolean connectedEthernet = false;
boolean connectedBroker = false;

int LDR = 0;
int TEMP = A0;

char message_buff[100];

const char* topico_sensor = "iot/sensor/temp1";
const char* topico_control = "iot/control";

byte mac[] = {0xf0,0x4d,0xa2,0xe4,0xc7,0x65};
const char* server = "192.168.1.100";
EthernetClient client;
PubSubClient mqttClient(client);

IRControle irc;

//PubSubClient mqttClient;


void callback(char* topic, byte* payload, unsigned int length);

void publishMessage( String sensorReading);

String readSensor();

void printIPAddress();

void connectEthernet();

boolean isValidNumber(String str);


void setup() {

  Serial.println( "Starting Arduino..." );
  pinMode( LDR, INPUT );
//  pinMode( TEMP, INPUT );
  
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  // this check is only needed on the Leonardo:
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  // start the Ethernet connection:
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // no point in carrying on, so do nothing forevermore:
    delay( 1000 );
  } else {
    // print your local IP address:
    printIPAddress();  
  }
  mqttClient.setCallback(callback);
}

void loop() {
//  Serial.println( "Starting Arduino..." );
  connectEthernet();  
  
  if( connectedEthernet ){
  
      if( !connectedBroker ){
//          Serial.println( "Connecting to broker..." );
          mqttClient.setServer(server, 1883);
          connectedBroker = mqttClient.connect("myClientID");
      } 
   }
  
   if( connectedBroker ){
//      Serial.println( "Reading sensor..." );
      String sensor = readSensor();
      publishMessage(sensor);
      mqttClient.subscribe(topico_control);
    }

  delay(500);
    
  mqttClient.loop();
}


void callback(char* topic, byte* payload, unsigned int length) {

  int i = 0;
  //converte o array em String
  for(i=0; i<length; i++) {
    message_buff[i] = payload[i];
  }
  message_buff[i] = '\0';
  
  String msg = String(message_buff);
  Serial.println(msg);

  if (msg.equals("l")) {
    irc.ligar();
  } else if (msg.equals("d")) {
    irc.desligar();
  } else if (isValidNumber(msg)) {
    int arg = msg.toInt();
    irc.alt_temp(arg);
  } else {
    Serial.println("Msg invalida.");
  }
  
}

boolean isValidNumber(String str){
   for(byte i=0;i<str.length();i++)
   {
      if(isDigit(str.charAt(i))) return true;
        }
   return false;
} 


void publishMessage( String sensorReading) {
  int str_len = sensorReading.length() + 1; 
  char message[str_len];
  sensorReading.toCharArray( message, str_len );
  int length = strlen(message);
  mqttClient.publish( topico_sensor, (byte*)message, length, retain);  
  
}


String readSensor() {
//  float cel = ( float((analogRead( TEMP ))*5)/1023);
//  float temp = analogRead(TEMP);
//  float voltage = temp * 5.0;
//  voltage /= 1024.0; 
//  
//  float cel = (voltage - 0.5) * 100 ;
//  
//  
//  Serial.println(reading);

  int reading = analogRead(TEMP);
  float cel = reading / 9.31;
  String cel_str = String(cel);
  
  return cel_str;
}


void printIPAddress() {
  Serial.print("My IP address: ");
  for (byte thisByte = 0; thisByte < 4; thisByte++) {
    // print the value of each byte of the IP address:
    Serial.print(Ethernet.localIP()[thisByte], DEC);
    Serial.print(".");
  }

  Serial.println();
  connectedEthernet = true;
  
}


void connectEthernet() {
  
  switch ( Ethernet.maintain() )
  {
    case 1:
      //renewed fail
      Serial.println("Error: renewed fail");
      connectedEthernet = false;
      
      break;

    case 2:
      //renewed success
      Serial.println("Renewed success");

      //print your local IP address:
      printIPAddress();
      connectedEthernet = true;
      break;

    case 3:
      //rebind fail
      Serial.println("Error: rebind fail");
      connectedEthernet = false;
      break;

    case 4:
      //rebind success
      Serial.println("Rebind success");

      //print your local IP address:
      printIPAddress();
      connectedEthernet = true;
      break;

    default:
      connectedEthernet = true;
      break;

  }  
}
