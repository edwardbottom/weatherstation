
#include "font.h"
char var;
char readByte(){
  if(Serial.available() > 0){
    if(Serial.read() == '!'){
      var = Serial.read();
      Serial.print("variable");
      Serial.println(var);
      return var;
    }
  }
}

void potentiometerValue(){
  long rawBits = analogRead(0);
  Serial.println(rawBits);
 /* unsigned long rawBits;
  rawBits = *(unsigned long *) & pot;*/
  Serial.write('!');
  Serial.write(rawBits >> 24);
  Serial.write(rawBits >> 16);
  Serial.write(rawBits >> 8);
  Serial.write(rawBits);
  }
int plex;
void multiPlex(){
  if(var == 'C'){
    plex = 1;
  }
  else if(var == 'F'){
    plex = 2;
  }
  else if(var == 'W'){
    plex = 5;
  }
  else if(var == 'S'){
    plex = 4;
  }
  else{//var == P
    plex = 3;
  }
   for(int i = 0; i < 5; ++i){
     int a = font_5x7[plex][i];

       digitalWrite(9,HIGH);
       digitalWrite(10,HIGH);
       digitalWrite(11,HIGH);
       digitalWrite(12,HIGH);
       digitalWrite(13,HIGH);

     digitalWrite(i+9,LOW);
     
     for (int j = 1; j < 8; j++){
      
        if (bitRead(a,j)) {
           digitalWrite(j+1,HIGH);
        }
       else{
         digitalWrite(j+1,LOW);
       }


      }
       delay(1);
   }
}

void setup(){
 pinMode(2,OUTPUT);
 pinMode(3,OUTPUT);
 pinMode(4,OUTPUT);
 pinMode(5,OUTPUT);
 pinMode(6,OUTPUT);
 pinMode(7,OUTPUT);
 pinMode(8,OUTPUT);
 pinMode(9,OUTPUT);
 pinMode(10,OUTPUT);
 pinMode(11,OUTPUT);
 pinMode(12,OUTPUT);
 pinMode(13,OUTPUT);
 analogReference(DEFAULT);
 Serial.begin(9600);
}


void loop(){
  potentiometerValue();
  readByte();
  multiPlex();

}

