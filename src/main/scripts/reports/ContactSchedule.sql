/* Get an appointment schedule for contacts */
SELECT
    A.Contact_ID,
    C.Contact_Name,
    A.Appointment_ID,
    A.Title,
    A.Description,
    A.Type,
    A.Start,
    A.End
FROM
	appointments A
LEFT JOIN
	contacts C on A.Contact_ID = C.Contact_ID
WHERE
	A.Contact_ID LIKE ?
GROUP BY
	A.Contact_ID,
    A.Start,
    A.Appointment_ID
ORDER BY
	A.Contact_ID,
    A.Start,
    A.Appointment_ID