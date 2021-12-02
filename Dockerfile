FROM azul/zulu-openjdk:8u282

RUN apt update && apt -y install bash openssl

ADD http://192.168.13.69:8081/nexus/repository/third-party/consul-template /usr/local/bin/consul-template
RUN chmod 755 /usr/local/bin/consul-template

ADD ./conf /etc/consul-template/conf
ADD ./templates /etc/consul-template/templates

COPY ./heal-controlcenter /opt/heal-controlcenter/
COPY ./entrypoint.sh /opt/heal-controlcenter/entrypoint.sh

RUN chmod +x /opt/heal-controlcenter/entrypoint.sh
RUN mkdir -p /tmp/logs

ENTRYPOINT ["/opt/heal-controlcenter/entrypoint.sh"]