FROM neo4j:5.12

COPY backups/neo4j.dump /backups/neo4j.dump

COPY restore-script.sh /restore-script.sh

RUN chmod +x /restore-script.sh

CMD ["bash", "/restore-script.sh"]