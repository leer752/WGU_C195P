/* Get Appointment_ID and Start from appointments with a filter by Start within a specified range */
SELECT
	Appointment_ID,
    Start
FROM 
	appointments
WHERE
    Start >= { ts ? } AND
    Start <= { ts ? }