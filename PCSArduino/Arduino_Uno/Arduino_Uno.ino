#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include <MySQL_Encrypt_Sha1.h>
#include <MySQL_Packet.h>
#include <SPI.h>
#include <Ethernet.h>
EthernetClient client;
#define DEBUG true // 개발자용 메세지 표시 유무 지정. 

#include <SoftwareSerial.h>             // 소프트웨어 시리얼 통신 라이브러리 설정 
SoftwareSerial No2Arduino(2, 3);        // 수신 RX: D2, 송신 TX: D3 설정
// 아두이노 통신
SoftwareSerial esp8266(12, 11);

#include <LiquidCrystal_I2C.h>
#include <Wire.h>
LiquidCrystal_I2C lcd(0x27, 16, 2);
// LCD

char str_buf[20];

int fanOne = 6;
int fanTwo = 7;
// 팬
int pumpOne = 4;
int pumpTwo = 5;
// 펌프
int green = 10;
int yellow = 9;
int red = 8;
// 신호등
int soil;
// 토양센서
String communication;
// 조건시작 수신값 저장
String communication2;
// 조건 (온도값 높았을때) 수신값 저장
String communication3;
// 조건 (온도값 낮았을때) 수신값 저장
String communication4;
// 조건 (습도값 높았을때) 수신값 저장
String communication5;
// 조건 (습도값 낮았을때) 수신값 저장
String division[4];
// 수신값 나누기

int espfan = 0;
int esppump = 0;
int espLED = 0;
int espauto = 0;
int pinNumber1;
void setup() {
  No2Arduino.begin(9600);               // 아두이노 No1과의 통신 속도 설정
  Serial.begin(9600);                   // 시리얼 모니터에 표시할 통신 속도 설정

  esp8266.begin(9600);
  //esp 셋업

  lcd.init();
  lcd.init();
  lcd.backlight();
  // LCD 셋업

  pinMode(fanOne, OUTPUT);
  pinMode(fanTwo, OUTPUT);
  // 팬
  pinMode(pumpOne, OUTPUT);
  pinMode(pumpTwo, OUTPUT);
  // 펌프
  pinMode(green, OUTPUT);
  pinMode(yellow, OUTPUT);
  pinMode(red, OUTPUT);
  // 신호등

  sendData("AT+RST\r\n", 2000, DEBUG);
  sendData("AT+CWLAP\r\n", 3000, DEBUG); // wifi 목록 확인
  sendData("AT+CWJAP=\"your_wifi_id\",\"your_wifi_password\"\r\n", 5000, DEBUG); // ap login
  sendData("AT+CWMODE=1\r\n", 1000, DEBUG); //{1:CLIENT, 2:SERVER, 3:BOTH}
  delay(1000);
  sendData("AT+CIFSR\r\n", 1000, DEBUG); // IP 주소 확인
  sendData("AT+CIPMUX=1\r\n", 1000, DEBUG); // 멀티 커넥션 (0~4 까지 TCP 연결 지원
  sendData("AT+CIPSERVER=1,8008\r\n", 1000, DEBUG); // 서버를 켜고, 8008 포트 사용

}

void loop() {

  int soilOne = analogRead(A0);
  int soilTwo = analogRead(A1);

  int soilHumi = (soilOne + soilTwo) / 2;

  if (esp8266.available()) {

    if (esp8266.find("+IPD,")) { // +IPD,482:HTTP/1.1 200 OK , 정상 응답, 웹페이지 시작.
      //요청에 대한 응답을 서버로부터 수신
      //+IPD,n:xxxxxxxxxx // received n bytes, data=xxxxxxxxxxx
      delay(1000);

      int connectionId = esp8266.read() - 48; // 48=(char 숫자0), connectionId=0

      if (esp8266.find("pin=")) {

        pinNumber1 = 0;
        // pin= 이후값 읽기.
        pinNumber1 = (esp8266.read() - 48) * 10; // 10의 자리
        pinNumber1 += (esp8266.read() - 48); // 1의 자리.
        String closeCommand = "AT+CIPCLOSE=";
        Serial.print("pinNumber1 : ");
        Serial.println(pinNumber1);
        closeCommand += connectionId;
        closeCommand += "\r\n"; // AT+CIPCLOSE=0 : 연결 종료.
      }
      // 조건시작
      if (pinNumber1 == 11) {
        // 팬 번호
        if (espfan % 2 == 0) {
          //짝수(시작)
          digitalWrite(fanOne, LOW);
          digitalWrite(fanTwo, HIGH);
          // 팬 on
        } else {
          //홀수
          digitalWrite(fanOne, HIGH);
          digitalWrite(fanTwo, LOW);
          // 팬 off
        }
        espfan++;
        Serial.print("espfan:");
        Serial.println(espfan);
      } else if (pinNumber1 == 13) {
        // 펌프번호
        if (esppump % 2 == 0) {
          // 짝수(시작)
          digitalWrite(pumpOne, HIGH);
          digitalWrite(pumpTwo, LOW);
          // 펌프 on
        } else {
          // 홀수
          digitalWrite(pumpOne, LOW);
          digitalWrite(pumpTwo, LOW);
          // 펌프 off
        }
        esppump++;
        Serial.print("esppump:");
        Serial.println(esppump);
      } else if (pinNumber1 == 15) {
        // LED 번호
        if (espLED % 2 == 0) {
          // 짝수(시작)
        } else {
          // 홀수
        }
        espLED++;
        Serial.print("espLED:");
        Serial.println(espLED);
      } else if (pinNumber1 == 17) {
        // 오토 번호
        boolean flag1 = true;
        while (flag1) {
          // 통신값 시작
          while (No2Arduino.available()) {
            // 수신값이 있다면
            char sentence = (char)(No2Arduino.read());
            // 수신 값(정수 값)을 char형으로 형변환해 char형으로 형변환해 char형 변수를 sentence 저장
            if ((String)sentence != "\n") {
              communication += (String)sentence;
            }
          }
          Serial.print("communication : ");
          Serial.println(communication);
          // 수신값 출력
          communication.trim();
          // 공백값 제거
          Split(communication, ',');
          // 수신값 , 기준으로 스플릿
          // 결과값 division[0] = 온도값, division[1] = 습도값, division[2] = 조도값, division[3] = 조건값

          lcd.setCursor(0, 0);
          lcd.print("temp is ");
          lcd.setCursor(9, 0);
          lcd.print(division[0]);
          lcd.setCursor(0, 1);
          lcd.print("humi is ");
          lcd.setCursor(9, 1);
          lcd.print(division[1]);
          lcd.setCursor(1, 2);
          lcd.print("ill is ");
          lcd.setCursor(8, 2);
          lcd.print(division[2]);

          Serial.print("division[0]");
          Serial.println(division[0]);
          Serial.print("division[1]");
          Serial.println(division[1]);
          Serial.print("division[2]");
          Serial.println(division[2]);
          Serial.print("division[3]");
          Serial.println(division[3]);

          // 조건시작
          if (division[3] != "") {
            // 동작값이 비어있지 않고
            Serial.println("com");

            if (division[3].equals("1")) {
              // 온도가 높다면
              Serial.println("com1-1");
              boolean flag1 = true;
              digitalWrite(fanOne, LOW);
              digitalWrite(fanTwo, HIGH);
              // 팬 on
              digitalWrite(green, LOW);
              digitalWrite(yellow, LOW);
              digitalWrite(red, HIGH);
              // 신호등 빨간색
              while (flag1) {
                while (No2Arduino.available()) {
                  // 수신값이 있다면
                  char sentence2 = (char)(No2Arduino.read());
                  // 수신 값(정수 값)을 char형으로 형변환해 char형으로 형변환해 char형 변수를 sentence 저장
                  if ((String)sentence2 != "\n") {
                    communication2 += (String)sentence2;
                  }
                }
                Serial.println("com1-2");
                Serial.println(communication2);
                communication2.trim();
                delay(1000);
                if (communication2.equals("3")) {
                  Serial.println("com1-3");
                  flag1 = false;
                  digitalWrite(fanOne, HIGH);
                  digitalWrite(fanTwo, LOW);
                  // 팬 off
                  digitalWrite(green, HIGH);
                  digitalWrite(yellow, LOW);
                  digitalWrite(red, LOW);
                  // 신호등 그린
                }
              }
            } else if (division[3].equals("2")) {
              // 온도가 낮다면
              digitalWrite(green, LOW);
              digitalWrite(yellow, HIGH);
              digitalWrite(red, LOW);
              // 신호등 노란색
              boolean flag2 = true;
              while (flag2) {
                while (No2Arduino.available()) {
                  // 수신값이 있다면
                  char sentence3 = (char)(No2Arduino.read());
                  // 수신 값(정수 값)을 char형으로 형변환해 char형으로 형변환해 char형 변수를 sentence 저장
                  if ((String)sentence3 != "\n") {
                    communication3 += (String)sentence3;
                  }
                }
                Serial.println(communication3);
                communication3.trim();
                if (communication3 == "3") {
                  flag = false;
                  digitalWrite(green, HIGH);
                  digitalWrite(yellow, LOW);
                  digitalWrite(red, LOW);
                  // 신호등 그린
                }

              }
            } else if (division[3].equals("4")) {
              // 습도가 높다면
              Serial.println("com4-1");
              boolean flag4 = true;
              digitalWrite(fanOne, LOW);
              digitalWrite(fanTwo, HIGH);
              // 팬 on
              digitalWrite(green, LOW);
              digitalWrite(yellow, LOW);
              digitalWrite(red, HIGH);
              // 신호등 레드
              while (flag4) {
                while (No2Arduino.available()) {
                  // 수신값이 있다면
                  char sentence4 = (char)(No2Arduino.read());
                  // 수신 값(정수 값)을 char형으로 형변환해 char형으로 형변환해 char형 변수를 sentence 저장
                  if ((String)sentence4 != "\n") {
                    communication4 += (String)sentence4;
                  }
                }
                Serial.println("com4-2");
                Serial.println(communication4);
                communication4.trim();
                delay(1000);
                if (communication4.equals("6")) {
                  Serial.println("com4-3");
                  flag4 = false;
                  digitalWrite(fanOne, HIGH);
                  digitalWrite(fanTwo, LOW);
                  // 팬 off
                  digitalWrite(green, HIGH);
                  digitalWrite(yellow, LOW);
                  digitalWrite(red, LOW);
                  // 신호등 그린
                }
              }
            } else if (division[3].equals("5") || soilHumi > 800) {
              // 습도가 낮다면
              Serial.println("com5-1");
              boolean flag5 = true;
              digitalWrite(pumpOne, HIGH);
              digitalWrite(pumpTwo, LOW);
              // 펌프 on
              digitalWrite(green, LOW);
              digitalWrite(yellow, HIGH);
              digitalWrite(red, LOW);
              // 신호등 노란색
              while (flag5) {
                while (No2Arduino.available()) {
                  // 수신값이 있다면
                  char sentence5 = (char)(No2Arduino.read());
                  // 수신 값(정수 값)을 char형으로 형변환해 char형으로 형변환해 char형 변수를 sentence 저장
                  if ((String)sentence5 != "\n") {
                    communication5 += (String)sentence5;
                  }
                }
                Serial.println("com5-2");
                Serial.println(communication5);
                communication5.trim();
                if (communication5.equals("6")) {
                  Serial.println("com5-3");
                  flag5 = false;
                  digitalWrite(pumpOne, HIGH);
                  digitalWrite(pumpTwo, LOW);
                  // 펌프 off
                  digitalWrite(green, HIGH);
                  digitalWrite(yellow, LOW);
                  digitalWrite(red, LOW);
                  // 신호등 그린
                }
              }
            }

          } else {
            // division[3] 값이 null이다
            Serial.println("division[3] is null");
          }
          // 조건 끝

          if (esp8266.available()) {
            if (esp8266.find("+IPD,")) { // +IPD,482:HTTP/1.1 200 OK , 정상 응답, 웹페이지 시작.
              //요청에 대한 응답을 서버로부터 수신
              //+IPD,n:xxxxxxxxxx // received n bytes, data=xxxxxxxxxxx
              delay(1000);
              int connectionId = esp8266.read() - 48; // 48=(char 숫자0), connectionId=0

              if (esp8266.find("pin=")) {
                int pinNumber1;
                pinNumber1 = 0;
                // pin= 이후값 읽기.
                pinNumber1 = (esp8266.read() - 48) * 10; // 10의 자리
                pinNumber1 += (esp8266.read() - 48); // 1의 자리.
                String closeCommand = "AT+CIPCLOSE=";
                Serial.print("pinNumber1 : ");
                Serial.println(pinNumber1);
                closeCommand += connectionId;
                closeCommand += "\r\n"; // AT+CIPCLOSE=0 : 연결 종료.
              }
              if (pinNumber1 == 17) {
                flag1 = false;
              }
            }


          }

          division[0] = "";
          division[1] = "";
          division[2] = "";
          division[3] = "";
          communication = "";
          delay(2000);
        }
        espauto++;
        Serial.print("espauto:");
        Serial.println(espauto);
      }
      else {
        Serial.println("pinNumber1 is null");
      }

    }
  }
}
void Split(String sData, char cSeparator) {
  int nGetIndex = 0 ;
  int i = 0;
  //임시저장
  String sTemp = "";
  //원본 복사
  String sCopy = sData;

  while (true) {
    //구분자 찾기
    nGetIndex = sCopy.indexOf(cSeparator);
    //리턴된 인덱스가 있나?
    if (-1 != nGetIndex) {
      //있다.
      //데이터 넣고
      sTemp = sCopy.substring(0, nGetIndex);
      division[i] = sCopy.substring(0, nGetIndex);
      //뺀 데이터 만큼 잘라낸다.
      sCopy = sCopy.substring(nGetIndex + 1);
    }
    else {
      //없으면 마무리 한다.
      division[3] = sCopy;
      break;
    }

    //다음 문자로~
    i++;
  }

}
String sendData(String command, const int timeout, boolean debug) {
  String response = "";
  esp8266.print(command);
  long time = millis(); // millis : 아두이노 시작후 지난 시간
  while ((time + timeout) > millis()) { // 정확한 딜레이
    while (esp8266.available()) {
      char c = esp8266.read();
      response += c; // wifi 로 받은 메세지가 있다면, 문자열 연결.
    }
  }
  if (debug) { // 디버깅용s
    Serial.println("this is sendData");
    Serial.print(response); //시리얼 모니터 출력.

  }
  return response; // 받아 처리하는 부분이 없으면 사라짐.
}
