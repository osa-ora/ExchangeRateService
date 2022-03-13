#!/bin/sh
echo "Please Login to OCP using oc login ..... "  
echo "Make sure Openshift Serverless Operator is installed"
echo "Make sure knative-serving namespace is created and an instance is already provisioned"
echo "Press [Enter] key to resume..." 
read

oc new-project dev
oc apply -f https://raw.githubusercontent.com/osa-ora/ExchangeRateService/master/configs/secret.yaml
oc apply -f https://raw.githubusercontent.com/osa-ora/ExchangeRateService/master/configs/redis-exchange.yaml
oc apply -f https://raw.githubusercontent.com/osa-ora/ExchangeRateService/master/configs/redis-exchange-srv.yaml
echo "Service 'Redis-Exchange' deployed successfully as ephemeral" 
oc apply -f https://raw.githubusercontent.com/osa-ora/ExchangeRateService/master/configs/configmap.yaml
oc apply -f https://raw.githubusercontent.com/osa-ora/ExchangeRateService/master/configs/image.yaml 
oc apply -f https://raw.githubusercontent.com/osa-ora/ExchangeRateService/master/configs/build-config.yaml
oc apply -f https://raw.githubusercontent.com/osa-ora/ExchangeRateService/master/configs/exchange-serverless.yaml 
echo "Service 'ExchangeService' deployed successfully as a serverless" 
