#!/bin/bash
DEPLOY_PATH=/home/ec2-user/build/
APPLICATION_JAR_NAME=codemoim_application.jar
APPLICATION_JAR=$DEPLOY_PATH$APPLICATION_JAR_NAME

echo "> $APPLICATION_JAR 배포"
#sudo nohup java -jar -Dspring.profiles.active=prod $APPLICATION_JAR > /dev/null 2> /dev/null < /dev/null &
nohup java -jar -Dspring.profiles.active=prod $APPLICATION_JAR > /dev/null 2> /dev/null < /dev/null &