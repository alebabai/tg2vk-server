#!/bin/bash
cd "$( dirname "${BASH_SOURCE[0]}" )"
rm tg2vk.jks tg2vk.p12 tg2vk.pem
keytool -genkey -keyalg RSA -alias tg2vk.ddns.net -keystore tg2vk.jks -storepass changeit -validity 360 -keysize 2048
keytool -importkeystore -srckeystore tg2vk.jks -destkeystore tg2vk.p12 -srcstoretype jks -deststoretype pkcs12
openssl pkcs12 -in tg2vk.p12 -out tg2vk.pem
