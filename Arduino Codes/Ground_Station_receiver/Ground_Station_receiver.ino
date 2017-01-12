int destinationAddress;
int packetLength;
int speed;

//sensor data
int teamID;
boolean glider;
long missionTime;
int packetCount;
float altitude;
float pressure;
float gliderSpeed;
float temp;
float voltage;
float heading;
int softwareState;

float latitudeGPS;
float longitudeGPS;

void setup() {
  Serial.begin(speed);
}

void loop() {
  if(Serial.available() > packetLength){
    if(Serial.read() == 0x7e ){//start
      
    }
  }
}
