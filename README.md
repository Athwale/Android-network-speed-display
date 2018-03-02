# Android-network-speed-display
This app provides a network upload and download speeds in the notification bar. Does not have a launcher icon in order to not pollute 
app drawer, satarts once with a widget starter and then keeps a service running in the background to check net speeds.
It is supposed to be minimalistic. Automatically turns off when the screen is off to conserve power.

Currently: Working on controling a thread from the service which will provide the measurements and publish results into main thread for display. 
