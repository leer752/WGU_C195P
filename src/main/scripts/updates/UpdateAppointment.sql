/* UPDATE an existing appointment in appointments by Appointment_ID */
UPDATE
	appointments
SET
	Title=?,
    Description=?,
    Location=?,
    Type=?,
    Start=?,
    End=?,
    Last_Update=?,
    Last_Updated_By=?,
	Customer_ID=?,
    Contact_ID=?
WHERE
	Appointment_ID=?