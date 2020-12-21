/* Get appointments according to a filter by Customer_ID, Start, and End that do NOT match a specific appointment */
SELECT
	*
FROM
	appointments
WHERE
    Appointment_ID NOT LIKE ? AND
	(Customer_ID LIKE ? OR
    Contact_ID LIKE ?) AND
    (({ ts ? } >= Start AND { ts ? } < End) OR
    ({ ts ? } > Start AND { ts ? } <= End) OR
    (Start >= { ts ? } AND Start < { ts ? }) OR
    (End > { ts ? } AND End <= { ts ? }))