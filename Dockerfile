FROM tomcat:9-jdk11-corretto
EXPOSE 8080
COPY ROOT.war /usr/local/tomcat/webapps