template {
    source      = "/etc/consul-template/templates/conf.properties.tmpl"
    destination = "/opt/heal-controlcenter/config/conf.properties"
}

template {
    source      = "/etc/consul-template/templates/logback.xml.tmpl"
    destination = "/opt/heal-controlcenter/config/logback.xml"
}