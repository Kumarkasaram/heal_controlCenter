#!/bin/sh
#
# init script for a Java application
#

# Comments to support chkconfig on RedHat Linux
# chkconfig: 2345 95 05


GREEN="\e[0;32m"
RED="\e[0;31m"
NORMAL="\e[0m"


# Check the application status
#
# This function checks if the application is running
check_status() {

  # Running pgrep to check if the PID exists
  s=`pgrep -f "java.*-jar ${rpm.install.directory}/${project.artifactId}-${project.version}.jar"`

  # If something was returned by the ps command, this function returns the PID
  if [ $s ] ; then
    echo $s
  else
    echo 0
  fi
}

# Starts the application
start() {

  # At first checks if the application is already started calling the check_status
  # function
  pid=$( check_status )

  if [ $pid -ne 0 ] ; then
    echo -e "${project.artifactId} ($pid) is already running   [${RED}FAILED${NORMAL}]"
  else
    # If the application isn't running, starts it
    echo -en "Starting ${project.artifactId}.."
    su - ${unix.user.name} -c "java -XX:+UseConcMarkSweepGC -Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9145 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar ${rpm.install.directory}/${project.artifactId}-${project.version}.jar >> ${rpm.install.directory}/log/${project.artifactId}.log 2>&1 &"
    statuscode=0
    for i in $(seq 10)
    do
        sleep 1
        echo -n "."
        pid=$( check_status )
        if [ $pid -ne 0 ] ;  then
          statuscode=1
          break
        fi
    done

    if [ $statuscode -eq 1 ] ;  then
      echo -e "                  [${GREEN}  OK  ${NORMAL}]"
    else
     echo -e "[${RED}FAILED${NORMAL}]"
    fi

  fi
}

# Stops the application
stop() {

  # Like as the start function, checks the application status
  pid=$( check_status )

  if [ $pid -eq 0 ] ; then
    echo -e "${project.artifactId} is not running              [${RED}FAILED${NORMAL}]"
  else
    # Kills the application process
    echo -en "Shutting down ${project.artifactId}"
    su - ${unix.user.name} -c "kill  $pid >/dev/null 2>&1"

    statuscode=1
    for i in $(seq 45)
    do
        sleep 1
        echo -n "."
        if [ ! -d "/proc/$pid" ] ; then
          statuscode=0
          break
        fi
    done

    if [ $statuscode -eq 0 ] ;  then
      echo -e "            [${GREEN}  OK  ${NORMAL}]"
    else
     echo -e "[${RED}FAILED${NORMAL}]"
    fi

  fi
}

# Show the application status
status() {

  # The check_status function, again...
  pid=$( check_status )

  # If the PID was returned means the application is running
  if [ $pid -ne 0 ] ; then
    echo -e "${project.artifactId} ($pid) is running           [${GREEN}  UP  ${NORMAL}]"
  else
    echo -e "${project.artifactId}  is not running             [${RED} DOWN ${NORMAL}]"
    $SETCOLOR_RED
  fi

}

# Main logic, a simple case to call functions
case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart|reload)
    stop
    start
    ;;
  *)
    echo "Usage: ${project.artifactId} {start|stop|restart|reload|status}"
    exit 1
esac

exit 0