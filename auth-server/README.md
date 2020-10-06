## Docker Composer Steps

#### // Make sure you have the right application.properties setup for mysql
    spring.datasource.username=root
    spring.datasource.password=password

#### // To generate jar files

    mvn clean install 

#### // to build image for backend
    
    docker build -t authserver . 

#### // create network 

    docker network create heimdallnetwork

#### // Compose your docker-compose file
    docker-compose up


DEFAULT 

#Port
server.port=8081
#Database
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.url=jdbc:mysql://localhost:3306/heimdall?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.jpa.hibernate.ddl-auto=create
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect

##CORS URLS
authorizationServerFrontedURL=http://localhost:4201
clientFrontedURL=http://localhost:4200
clientBackendURL=http://localhost:8080


#Email
#SMTP configuration
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.auth=true
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=heimdallteam0@gmail.com
spring.mail.password=Proiectbazat10

