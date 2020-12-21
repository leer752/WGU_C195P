/* Get Appointment_ID of appointments for a single specific customer by Customer_ID */
SELECT 
	Appointment_ID
FROM 
	appointments
WHERE 
	Customer_ID = ?