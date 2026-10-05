SUMATIVA3

Tecnologías
Java 17
Spring Boot 3.5.x
Spring Cloud 2025.0.0 (Config Server, Eureka)
Spring Authorization Server / OAuth2 Resource Server
Apache Kafka 3.9.1
Docker
Maven 3.9

Estructura del proyecto:

week7-kafka-study/
├── pom.xml                      # Parent multi-módulo
├── docker-compose.yml
├── .dockerignore
├── config-server/               # Servidor de configuración centralizada
│   └── src/main/resources/config-repo/
│       ├── application.yml
│       ├── application-docker.yml
│       ├── discovery-server.yml
│       ├── auth-server.yml
│       ├── transaccion-service.yml
│       └── transaccion-service-docker.yml
├── discovery-server/            # Eureka Server
├── auth-server/                 # Servidor OAuth2
├── shared-events/               # Librería con los eventos (no es un servicio)
└── transaccion-service/         # Microservicio de transacciones (Kafka + Saga)

shared-events es solo una librería de clases compartidas. Se compila junto a transaccion-service, no tiene contenedor ni configuración propia.

Contenedores y puertos

Kafka	week7-kafka	9092:9092	Broker Kafka
Config Server	config-server	8888:8888	Entrega la configuración a los demás servicios
Discovery Server	discovery-server	8761:8761	Registro de servicios (Eureka)
Auth Server	auth-server	9000:9000	Emisión de tokens OAuth2 / JWT
Transacción Service	transaccion-service	8081:8081	API de transacciones y eventos Kafka


Orden de arranque

Docker Compose controla el orden mediante healthchecks:

config-server (healthcheck en /actuator/health)
discovery-server (espera a que config-server esté saludable)
auth-server (espera a config-server y discovery-server)
transaccion-service (espera a config-server, discovery-server y Kafka)

Kafka arranca en paralelo con los demás.

Si el Config Server no está disponible, los servicios no arrancan. Es el comportamiento esperado.

Cómo funciona la configuración centralizada

Cada microservicio (discovery-server, auth-server, transaccion-service) tiene un application.yml mínimo:

yaml
spring:
  application:
    name: <nombre-del-servicio>
  config:
    import: "configserver:${CONFIG_SERVER_URL:http://localhost:8888}"

Todo lo demás (puertos, Kafka, seguridad, Eureka) vive en config-server/src/main/resources/config-repo/. En Docker, cada servicio recibe dos variables de entorno:

SPRING_PROFILES_ACTIVE=docker: activa los archivos *-docker.yml.
CONFIG_SERVER_URL=http://config-server:8888: indica dónde pedir la configuración.

Orden de prioridad de los archivos (de mayor a menor), por ejemplo para transaccion-service con perfil docker:

transaccion-service-docker.yml
application-docker.yml
transaccion-service.yml
application.yml

config-repo está dentro del JAR del config-server. Cada cambio en esos archivos requiere reconstruir la imagen: docker compose up --build config-server.

Requisitos previos
Docker Desktop (o Docker Engine + Compose v2)
(Opcional, para compilar fuera de Docker) JDK 17 y Maven 3.9
Ejecución con Docker

Desde la carpeta raíz del proyecto:

bash
docker compose up --build

ENDPOINTS:

/actuator/health es un endpoint GET.

Obtener token POST http://localhost:9000/oauth2/token Usa el client y el flujo registrados en el auth-server (por ejemplo client_credentials con autenticación Basic: client_id y client_secret).
Llamar a un endpoint protegido http://localhost:8081/<endpoint> con el header:
   Authorization: Bearer <access_token>

POST: http://localhost:8081/api/transacciones
GET: http://localhost:8081/api/transacciones
GET: http://localhost:8081/api/transacciones/{id}
POST: http://localhost:8081/api/transacciones/{id}/approve
POST: http://localhost:8081/api/transacciones/{id}/reject
POST: http://localhost:8081/api/transacciones/{id}/replay-created



