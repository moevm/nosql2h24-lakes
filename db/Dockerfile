FROM neo4j:5.12

ENV NEO4J_AUTH=neo4j/neo4jneo4j

WORKDIR /var/lib/neo4j

COPY restore-script.sh /restore-script.sh

RUN chmod +x /restore-script.sh

CMD ["/bin/bash", "/restore-script.sh", "&&", "neo4j", "console"]