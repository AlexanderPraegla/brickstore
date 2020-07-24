# Masterarbeit

## Demo für API Gateway, Service Discovery und OAuth2.0

### Start Up
* _mvn clean install_ zum Bauen des gesamten Projekts
* _docker-compose -f docker-compose-demo.yml up --build_ baut die Docker Images neu und startet die Anwendung in docker compose

### Endpoints
* [http://localhost:8761](http://localhost:8761): Hier ist die Eureka Oberfläche zu finden. Dort sollten 2 verschiedene Services registriert sein. Das API Gateway und zwei Instancen vom demo-service
* [http://localhost:8080/demo/activeProfile](http://localhost:8080/demo/activeProfile) demonstriert das Loadbalancing über das API Gateway zusammen mit Eureka. Hierfür wird kein Login benötigt
* [http://localhost:8080/demo/jwt](http://localhost:8080/demo/jwt): Zeigt den aktuellen JWT Token an. Erfordert einen Login via Okta. Es gibt zwei Nutzer:
    * Nutzername _justus.jonas@dreifragezeichen.com_ mit Passwort _TestTest1_
    * Nutzername _peter.shaw@dreifragezeichen.com_ mit Passwort _TestTest2_
* [http://localhost:8080/demo/adminAccess](http://localhost:8080/demo/adminAccess): Der Nutzer _justus.jonas@dreifragezeichen.com_ hat die Gruppe "admins" zugewiesen, mit welcher er auf diese Resource zugreifen kann. Auf die anderen in der nächsten Zeile jedoch nicht 
* [http://localhost:8080/demo/customerAccess](http://localhost:8080/demo/customerAccess): Der Nutzer _peter.shaw@dreifragezeichen.com_ hat die Gruppe "customers" zugewiesen und kann damit nur auf diese Resource zugreifen 

## Start des Prototypen

### Start Up (docker-compose)
* _mvn clean install_ zum Bauen des gesamten Projekts
* _docker-compose -f docker-compose-http-prototype.yml up --build_ für den HTTP basierten Prototypen
* _docker-compose -f docker-compose-event-prototype.yml up --build_ für den Event basierten Prototypen
* Nachdem alle Services hochgefahren sind muss für den Event basierte Prototyp noch folgendes Ausgeführt werden, zu Erzeugung eines Standalone Clusters:
    * _docker exec -it mongo_database mongo_ 
    * _rs.initiate()_ 

### Endpoints
Unter [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) sind alle API Endpunkte zu finden. Die Authentifizierung finde mittels des OAuth2.0 Servers statt.
Es gibt zwei Nutzer:
* admin:adminPassword (mit den Authorities 'admins,customers')
* customer:customerPassword (mit der Authority 'customers')
ClientId und ClientSecret sind bereits ausgefüllt

### Tests
* _docker-compose -f docker-compose-http-prototype-test.yml up --build_ für den gewünschten Prototypen starten
* _docker-compose -f docker-compose-event-prototype-test.yml up --build_ für den gewünschten Prototypen starten
* Wie oben auch, müssen folgende Befehle ausgeführt werden:
    * _docker exec -it mongo_database mongo_ 
    * _rs.initiate()_ 
* _mvn clean test_ im Order "system-tests" ausführen

## Lokale MongoDb
Wird der Event-basierte Prototyp nicht mittels docker-compose gestartet, so ist es nötig eine lokale MongoDb zu konfigurieren und mit einem replica-set zu starten. \
Gestartet wird die Datenbank mit _mongod --port 27017 --dbpath /srv/mongodb/db0 --replSet rs0 --bind_ip localhost_ wobei "--dbpath /srv/mongodb/db0" optional sind und den Pfad angeben, wo die Daten im System abgelegt werden.
Anschließend muss wie bei docker-compose auf die CLI der MongoDb mit dem Befehl _mongo_ zugriffen und _rs.initiate()_ ausgeführt werden.  

## RabbitMQ Docker Image
Für den lokalen Setup ohne docker-compose muss RabbitMQ manuell gestartet werden.
docker run -d -p 127.0.0.1:5672:5672 -p 127.0.0.1:15672:15672 --name my-rabbit rabbitmq:3-management

* Username: guest
* Passwort: guest
