###Docker Composer Steps

##// Make sure you have the right application.properties setup for mysql ( this are default settings for docker ) 
    spring.datasource.username=root
    spring.datasource.password=password

##// To generate jar files

    mvn clean install 

##// to build image for backend
    
    docker build -t authserver . 

##// create network 

    docker network create heimdallnetwork

##// Compose your docker-compose file
    docker-compose up
