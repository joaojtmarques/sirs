# sirs
# Secure Child Locator
This application has 3 main components. A guardian app, a child app, and a Server.

Both apps run on Android. You can run the guardian app either from the Android Studio Emulator or through your own mobile. To run the Child app, you should run it in your mobile in order to have access to location services and a camera to read a QrCode.

# Running Android Apps

To compile and run the apps, first start by opening them on Android Studio and building the code.
If you want to run the guardian app on the emulator, just select the emulator of your choice and press run in Android Studio.
If you want to run the app on your mobile phone, you first have to turn developer mode on. On our devices, it is simply achieved by tapping the build number on our About Phone settings. Once you have that done, you have to turn on the USB Debugging Option on the developer settings. Afterwards, you have to connect your phone to the computer running Android Studio using an USB cable. Select your mobile device on Android Studio and press Run. You should now have installed the app on your phone.

# Running the Server

As for the Server, we set it up so that it would run on one of our computers. It has a certificate that was created for that pc's specific IP.
In order to run the server on a different machine, a new Certificate must be created for that machine and signed by the CA. To do so:

In the Server/ directory:

* Modify domains.ext so that the new machine's IP appears after "IP.1 =".
* Run, filling the fields of "subj" as desired: openssl req -new -nodes -newkey rsa:2048 -keyout serverPrivKey.key -out serverCsr.csr -subj "/C=PT/ST=YourState/L=YourCity/O=Example-Certificates/CN=localhost.local"
* Run: openssl x509 -req -sha256 -days 1024 -in serverCsr.csr -CA ../CA/CAPrivate.pem -CAkey ../CA/CAPrivate.key -CAcreateserial -extfile domains.ext -out serverCertificate.crt (The asked password is "mypassword")
1. Run: openssl pkcs12 -export -in serverCertificate.crt -inkey serverPrivKey.key -certfile serverCertificate.crt -out serverKeyStore.p12 (generates KeyStore with user-defined password)
2. Run: keytool -importcert -alias ca -file ../CA/CAPrivate.pem -trustcacerts -keystore truststore.jks -storetype JKS (generates a TrustStore with the CA's certificate and a user-defined password)

Do the following changes to SimpleJavaHttpServer/src/com/server/SimpleHttpsServer.java:

* The KeyStore path must be changed in the line 37 to the one created in (1).
* Change the password in line 38 to the one you defined in (2).
* Change the password in line 39 to the one you defined in (1).
* The TrustStore path must be changed in line 45 to the one created on (2).

# Setting up the apps for running with the new Server

Change baseUrl in each of these files to the ip you entered in the domains.ext file:

* guardian-app/app/src/main/java/com/example/guardian_app/RetrofitAPI/RetrofitCreator.java - line 31
* child_app/app/src/main/java/com/example/child_app/RetrofitAPI/RetrofitCreator - line 33
