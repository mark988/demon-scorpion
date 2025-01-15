#!/bin/bash
docker run -p 11111:11111 --name canal-server2 \
-e canal.destinations=heima \
-e canal.instance.master.address=demo.yx.cool:3306  \
-e canal.instance.dbUsername=canal  \
-e canal.instance.dbPassword=canal123  \
-e canal.instance.connectionCharset=UTF-8 \
-e canal.instance.tsdb.enable=true \
-e canal.instance.gtidon=false  \
-e canal.instance.filter.regex=heima\\..* \
--network mynetwork \
-d canal/canal-server:v1.1.5