#!/bin/bash
APPLICATION_JAR_NAME=codemoim_application.jar

echo "> 현재 실행중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -f $APPLICATION_JAR_NAME)

if [ -z $CURRENT_PID ] #-z: 문자열의 길이가 0이면 참
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  #sudo kill -15 $CURRENT_PID
  kill -15 $CURRENT_PID
  sleep 5
fi