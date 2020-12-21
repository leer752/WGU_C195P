Title: Appointment Scheduler
Purpose: Provides a GUI-based scheduling desktop application that allows an authenticated user to add, update, and/or delete customers and appointments from a MySQL database. 
Additionally, the application provides 3 reports that can be generated:
Report #1 - The total number of customer appointments by type and month.
Report #2 - The appointment schedule for each contact in the database.
Report #3 - The current user's session history for record changes, provided their changes haven't been completely overwritten by another user since logging in.

Author: Lee Rhodes
Contact Information: lee.n.rhodes@gmail.com
Application Version: 1.0
Date: 12-16-2020

IDE: IntelliJ IDEA 2020.2.3 (Community Edition)
JDK: Java SE 11.0.9
JavaFX: JavaFX 11 SDK 11

How to run the program:
Within the IDE of your choice, build the project using the Rhodes_C195_PA folder.
This should contain all files necessary to run the program.
Once the project is loaded in the IDE, navigate to Rhodes_C195_PA/src/main and open Main.java.
From there, it should be as simple as looking for a "Run 'Main'" button at the top of the IDE.
For example, IntelliJ has a Run tab that has "Run 'Main'" option underneath it. It's shortcut for IntelliJ is Shift+F10.
Once the program is running, it should be relatively intuitive how to use the program.
You'll enter your login credentials - for the purpose of testing, the username is "test" and the password is "test".
From there, you have the opportunity to look at the records for appointments and customers and will be able to add, update, or delete appointments and customers of your choosing.
Additionally, you can press on the "Generate reports" button to be taken to the reports pane. From there, you have the opportunity to run 3 separate reports that have been prepared.
Keep in mind that Report #3 will not generate anything until you have started adding and updating records.

Description of additional report (#3):
This additional report takes a look at the logged-in user's username to scan appointment and customer records to see if any of these records have the logged-in user's username in their Created_By or Last_Updated_By fields.
If they do, that means the logged-in user has touched that file at some point. Additionally, the report takes the log-in time of the current user and filters the records even further so that it only shows records that the user has updated since they logged-in.
The report tells you what type of record was updated (appointment or customer), the ID of that record, and what time it was updated.