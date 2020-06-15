FROM ubuntu:18.04
ENV SPARK_VERSION=2.3.0
ENV HADOOP_VERSION=2.7
ENV POSTGRES_VERSION=11.5
ENV SCALA_VERSION=2.11.0

RUN apt-get update  &&  apt-get install -y  wget openjdk-8-jdk scala postgresql postgresql-client postgresql-contrib
USER postgres

RUN /etc/init.d/postgresql start && \
        psql --command "CREATE USER postgres WITH PASSWORD 'postgres';" && \
        CREATE DATABASE test_db;

RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/11.5/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/11.5/main/postgresql.conf
EXPOSE 5432

RUN wget --no-verbose http://www.gtlib.gatech.edu/pub/apache/spark/spark-2.3.0/spark-2.3.0-bin-hadoop2.7.tgz
RUN tar -xzf /spark-2.3.0-bin-hadoop2.7.tgz && \
    mv spark-2.3.0-bin-hadoop2.7 spark && \
    echo "export PATH=$PATH:/spark/bin" >> ~/.bashrc && \
    echo "export $SPARK_HOME=/spark" >> ~/.bashrc && \

RUN git clone https://github.com/vishnupriyavvp/FIFADataset19.git  /opt/FIFADataset19
WORKDIR /opt/FIFADataset19

RUN npm install

EXPOSE 8080
CMD [ "npm", "start" ]

