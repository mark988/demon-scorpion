#!/bin/bash

docker run \
 -p 3306:3306 \
 --name mymysql2 \
 -v /home/canal/mysql/conf:/etc/mysql/conf.d \
 -v /home/canal/mysql/logs:/logs \
 -v /home/canal/mysql/data:/var/lib/mysql \
 -e MYSQL_ROOT_PASSWORD=xxx234 \
 --memory=512m \
 --privileged \
 -d \
 mysql:5.7