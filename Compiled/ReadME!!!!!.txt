ReadME Project3
@Author Eric-Frontend
@Author Dardan-Backend
@Author Jaroslaw-Testing and Documentation+ backend help
Version:1.00c

To Lunch in Level 2  within same router(local network)
lunch Server.class by
>java Server.Class 1099
it should return in console
Server running...
Names bound to RMI registry at host 192.168.XXX.XXX and port 1099:
RMI
Copy address ip and  replace lines in ServletMain.java 
Client client = new Client(2099, "192.168.XXX.XXX", "txt");
and
new Client(2099, "192.168.XXX.XXX", filePart, fileFromServer, mergeType);
then Start Tomcat Server
add project to TomCat Server
test wa not done on war file some tweaking may be needed
time was restricting and testing was not done fully  due to very small amout of time.
given more time we would be able to do more
REQUIREMENTS:
	-Windows OS(windows 7 or newer)
	-Minimum Java version 1.7.0_79 
	-TomCat version 7.0.48
	-Pentium 4 (3.0)/ Pentium M(1.7) or Equivalent
	-1GB of RAM
	-Browsers: Opera-33,Chrome Version 46.0.2490.86 m, Minimum IE6\ Microsoft Edge, Firefox 40.0.1 or newer
______________________________________________________________________________________________________________________________________
Limitations 
	-Software able to download file from anywhere on RMI Server.
	-Merge is not returned to the browser Stored on RMI server.
	-only works  with .XML 
	Hardcoded IP
	- RMI server side files only from pre specified  Location
	-Level  2 Connection between RMI Server and Client/ServletMain
	Merged file Ocures on RMI Server and stored on RMI Server Side
	- you can not run mobile
	