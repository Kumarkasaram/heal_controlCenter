#!/bin/sh

if [ "x$UID" != "x" ]; then
    groupadd -g $GID appuser
	useradd -g $GID -u $UID -p appuser -s /bin/bash appuser
fi

myip=`env | grep NOMAD_IP | head -1 | cut -d '=' -f2`

MIN_HEAP="256"
if [ -z "$NOMAD_MEMORY_LIMIT" ]; then
        MAX_HEAP="1024"
else
        MAX_HEAP=$(($NOMAD_MEMORY_LIMIT*8/10))
fi

if [ -z "$JMX_PORT" ]; then
        JMX_PORT=9010
fi

consul-template -config=/etc/consul-template/conf -consul-addr=${CONSUL_URL} -once -consul-ssl -consul-ssl-verify=false

chown -R appuser:appuser /tmp/logs
chown -R appuser:appuser /opt/heal*

echo "cd /opt/heal-controlcenter" >> /tmp/start.sh
echo "java -XX:+UseConcMarkSweepGC -Xms${MIN_HEAP}M -Xmx${MAX_HEAP}M -XX:OnOutOfMemoryError='kill -9 %p' -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=${JMX_PORT} -Dcom.sun.management.jmxremote.rmi.port=${JMX_PORT} -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=${myip} -jar heal-controlcenter-*.jar 2>&1" >> /tmp/start.sh

su appuser -c 'bash -x /tmp/start.sh'