#!/bin/bash
if [ "$1" = "start" ]
then
	nohup java -jar target/manunia-web-0.0.1-SNAPSHOT.jar $1 > manuniaweb.log 2>&1 &
	echo $! > save_pid.txt
elif [ "$1" = "stop" ]
then
kill -9 `cat save_pid.txt`
rm save_pid.txt
elif [ "$1" = "" ]
then echo "You have to execute this jar with 1 argument: start / stop"
fi
