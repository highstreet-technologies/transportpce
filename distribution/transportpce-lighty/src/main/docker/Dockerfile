# Java 21 JRE Alpine base image — minimal footprint, no Karaf needed
FROM ${java.base.image}

# Install required packages
RUN apk update && apk --no-cache add bash curl

ENV JAVA_HOME /opt/java
ENV TPCE_HOME /opt/transportpce

# Copy the unpacked lighty tpce distribution
COPY tpce/ ${TPCE_HOME}/

# Ensure the entrypoint script is executable
RUN chmod 755 ${TPCE_HOME}/start-tpce.sh
RUN cd ${TPCE_HOME} && \
  mv config_template.json config.json && \
  sed -i 's/ODL_RESTCONF_PORT/8181/' config.json && \
  sed -i 's/"inetAddress": "127.0.0.1"/"inetAddress": "0.0.0.0"/' config.json
# Expose RESTCONF port
EXPOSE 8181

# Start TransportPCE via Lighty
ENTRYPOINT ["/opt/transportpce/start-tpce.sh"]

WORKDIR ${TPCE_HOME}
