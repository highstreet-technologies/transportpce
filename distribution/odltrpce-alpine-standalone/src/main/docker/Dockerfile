# Base alpine with added packages needed for Opendaylight
FROM ${base.image.repo}

# Add tools needed for OpenDaylight
RUN apk update && apk --no-cache add bash unzip rsync nss

# -----------------------------------------------------
#ENV JAVA_HOME /usr/lib/jvm/java-1.11-openjdk
ENV JAVA_HOME /opt/java/openjdk
ENV ODL_HOME /opt/opendaylight
ENV TRPCE_REPO mvn:org.opendaylight.transportpce/odl-transportpce/${trpce.features.version}/xml/features
#,mvn:org.opendaylight.transportpce/odl-transportpce-tapi/${trpce.features.version}/xml/features
ENV JAVA_SECURITY_DIR /etc/ssl/certs/java

# copy ODL
COPY odl/opendaylight-${odl.karaf.version} $ODL_HOME
# copy apps dependencies
#RUN rm -rf $ODL_HOME/system/org/opendaylight/transportpce
#COPY system /tmp/system
#RUN rsync -a --ignore-existing /tmp/system $ODL_HOME
COPY system $ODL_HOME/system
# copy scripts
COPY opt /opt

# Add additonal repositories to boot repositories
RUN cp $ODL_HOME/etc/org.apache.karaf.features.cfg $ODL_HOME/etc/org.apache.karaf.features.cfg.orig
RUN sed -i -e "\|featuresRepositories|s|$|, $TRPCE_REPO|" $ODL_HOME/etc/org.apache.karaf.features.cfg
RUN sed -i -e /featuresBoot/d $ODL_HOME/etc/org.apache.karaf.features.cfg
#RUN echo featuresBoot=8ed6b938-844e-4e32-9757-313811989a9f,odl-restconf-all,odl-netconf-topology,features-mdsal,odl-transportpce >> $ODL_HOME/etc/org.apache.karaf.features.cfg
RUN echo featuresBoot=df136e7b-2f88-43e1-ad7c-36c863ff336c,odl-mdsal-apidocs,odl-transportpce >> $ODL_HOME/etc/org.apache.karaf.features.cfg
#,transportpce-tapi,odl-netconf-console

#Copy patches
#COPY patches/org.apache.karaf.shell.ssh-4.2.10.jar $ODL_HOME/system/org/apache/karaf/shell/org.apache.karaf.shell.ssh/4.2.10/
# Install ssl and java certificates
COPY certs/truststoreONAPall.jks $JAVA_SECURITY_DIR/

RUN keytool -importkeystore -srckeystore $JAVA_SECURITY_DIR/truststoreONAPall.jks -srcstorepass changeit -destkeystore $JAVA_SECURITY_DIR/cacerts  -deststorepass changeit -noprompt
RUN keytool -importkeystore -srckeystore $JAVA_SECURITY_DIR/truststoreONAPall.jks -srcstorepass changeit -destkeystore /opt/java/openjdk/lib/security/cacerts  -deststorepass changeit -noprompt
# Set start script
RUN chmod 755 $ODL_HOME/bin/startODL.sh
ENTRYPOINT $ODL_HOME/bin/startODL.sh
WORKDIR /opt/opendaylight
EXPOSE 2550 8181