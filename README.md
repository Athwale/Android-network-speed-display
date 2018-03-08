# Android-network-speed-display
This app will provide network upload and download speeds in the notification bar as a notification and as an icon. The app does not have a launcher icon in order to not pollute app drawer with stuff people would launch only once. The service is started once with a starter widget. This widget can be then removed.
This app is supposed to be a minimalistic tool that provides one function and that is to see the net speeds. It does not keep logs of downloaded files or any other logs and does not collect any user activity. The service automatically turns off when the screen is off to conserve power and restarts when the user interacts with the phone.

Currently: 
Working on creating a bitmap icon for the notification bar

How to run this project:
Once the project is imported into Android Studio you may need to set Sdk location again in settings. To run the code, since this does not have any main activity, you need to edit Run -> Edit configurations -> set Nothing to Launch options
