#!/usr/bin/env bash
sudo fuser -k 80/tcp
nohup java -DRDS_HOSTNAME=synlabs-db-cluster-do-user-6775492-0.a.db.ondigitalocean.com -DRDS_PORT=25060 -DRDS_DB_NAME=defaultdb -DRDS_USERNAME=doadmin -DRDS_PASSWORD=h8olnfl80thfurla -Dserver.port=80 -Duser.timezone=IST -Dspring.profiles.active=prod -jar synvision-9.jar