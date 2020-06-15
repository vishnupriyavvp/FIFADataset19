FROM node

RUN git clone https://github.com/vishnupriyavvp/FIFADataset19.git  /opt/FIFADataset19
WORKDIR /opt/FIFADataset19

RUN npm install

EXPOSE 8080
CMD [ "npm", "start" ]

COPY . .
