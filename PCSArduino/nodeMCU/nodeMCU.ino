#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <DHT.h>

#define DHTPIN 10
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);
// 온습도

const char* ssid = "your_wifi_id"; // wifi 아이디
const char* password = "your_wifi_password"; // wifi 비밀번호
ESP8266WebServer server(80);
WiFiClient client;
MySQL_Connection conn((Client *)&client);

char INSERT_SQL[] = "INSERT INTO pcs.pcs(temp, humi, ill, datestamp) VALUES (concat(%f), concat(%f), concat(%f) ,now())";
// mysql에 사용하는 쿼리문. insert into '스키마'.'테이블'(컬럼명,컬럼명) values (데이터값,데이터값);
char query[128]; // 쿼리문을 char 형태로 선언한다는 건가?
IPAddress server_addr(192, 168, 45, 136);
// MySQL server IP
// win + r -> cmd -> ipconfig 에서 나오는 무선 ip 주소를 복붙 해준다음에 .(마침표) 를 ,(쉼표)로 고쳐주기.
char* user = "mysql_admin"; // MySQL user
char* sqlpw = "mysql_admin"; // MySQL password

int pin_th = 3;
// 온습도

float temp;
float humi;
float ill;
float temp2;
float humi2;
float ill2;

String ep;

void writeMySql() {
  sprintf(query, INSERT_SQL, temp, humi, ill);
  MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);
  cur_mem->execute(query);
  delete cur_mem;
}

void setup(void) {
  Serial.begin(9600);
  // 시리얼
  dht.begin();
  // 온습도

  WiFi.begin(ssid, password);
  Serial.println("");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print("wait");
  }

  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  while (conn.connect(server_addr, 3306, user, sqlpw) != true) {
    delay(200);
    Serial.print ("wait2 ");
  }
  Serial.println("");
  Serial.println("Connected to SQL Server!");
}

void loop(void) {
  server.handleClient();

  temp = dht.readTemperature();
  // 온도 입력
  humi = dht.readHumidity();
  // 습도 입력
  ill = analogRead(A0);
  // 조도 입력

  if (isnan(temp) || isnan(humi)) {
    // isanan = 숫자가 아니다
    Serial.println("Failed to read from DHT SENSOR!");
    return;
  }

  if (isnan(ill)) {
    // isanan = 숫자가 아니다
    Serial.println("Failed to read from LIGHT SENSOR!");
    return;
  }

   if (temp > 28 && humi < 37 && humi > 32) {
    // 온도가 높고 습도가 정상이라면
    boolean flag1 = true;
    digitalWrite(fanOne, LOW);
    digitalWrite(fanTwo, HIGH);
    Serial.println("TEMP,IS,HIGH,1");
    while (flag1) {
      temp = 0;
      temp = dht.readTemperature();
      if (temp < 28) {
        digitalWrite(fanOne, HIGH);
        digitalWrite(fanTwo, LOW);
        flag1 = false;
        Serial.println("3");
      }
      delay(2000);
    }
  } else if (humi > 37 && temp < 28 && temp > 23) {
    // 습도가 높고 온도가 정상이라면
    boolean flag2 = true;
    digitalWrite(fanOne, LOW);
    digitalWrite(fanTwo, HIGH)
    Serial.println("HUMI,IS,HIGH,4");
    while (flag2) {
      humi = dht.readHumidity();
      if (humi < 37) {
        flag3 = false;
        digitalWrite(fanOne, HIGH);
        digitalWrite(fanTwo, LOW);
        Serial.println("6");
      }
      delay(2000);
    }
  } else if (humi > 32 && temp < 28 && temp > 23) {
    // 습도가 낮고 온도가 정상이라면
    boolean flag3 = true;
    Serial.println("HUMI,IS,LOW,5");
    while (flag3) {
      humi = dht.readHumidity();
      if (humi > 32) {
        flag3 = false;
        Serial.println("6");
      }
    }
  } else {
    // 온습도에 문제가 없다면
    snd += temp;
    snd += ",";
    snd += humi;
    snd += ",";
    snd += ill;
    snd += ",0";
    Serial.println(snd);
    snd = "";
  }
  writeMySql();
  delay(5000);
}
