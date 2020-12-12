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
In order to run the server on a different machine, a new Certificate must be created for that machine and signed by a CA.
The certificate must be stored in a key store along with the public key. The key store path must be changed in the line 37 of the SimpleHttpsServer.java file.
