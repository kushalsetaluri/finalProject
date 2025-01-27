#include "madBlocks.h"
#include <ESP8266WiFi.h>

#define relay1 D0
#define relay2 D1
#define relay3 D2
#define relay4 D4

madBlocks mb;

String apiKey = "7c51417326bb5b21991e450b9d6cc485";

void setup() 
{
  Serial.begin(9600);
  pinMode(relay1,OUTPUT);
  pinMode(relay2,OUTPUT);
  pinMode(relay3,OUTPUT);
  pinMode(relay4,OUTPUT);
  WiFi.begin("Realme 2 Pro","12345678");
  while ((!(WiFi.status() == WL_CONNECTED)))
  {
    delay(300);
    Serial.println(".");

  }
  Serial.println("Connected");
  digitalWrite(relay1,1);
  digitalWrite(relay2,1);
  digitalWrite(relay3,1);
  digitalWrite(relay4,1);
}

void loop() 
{
  String data = mb.read(apiKey);
  if(data == "switch1")
  {         
    Serial.println("socket1 Operated");
    digitalWrite(relay1,!digitalRead(relay1));
    mb.write(apiKey,"0");
  }
  else if(data == "switch2")
  {
    Serial.println("socket2 Operated");
    digitalWrite(relay2,!digitalRead(relay2));
    mb.write(apiKey,"0");
  }
  else if(data == "switch3")
  {
    Serial.println("socket3 Operated");
    digitalWrite(relay3,!digitalRead(relay3));
    mb.write(apiKey,"0");
  }
  else if(data == "switch4")
  {
    Serial.println("socket4 Operated");
    digitalWrite(relay4,!digitalRead(relay4));
    mb.write(apiKey,"0");
  }
  delay(5000);
}
