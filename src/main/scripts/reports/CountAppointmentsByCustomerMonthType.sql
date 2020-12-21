/* Count the number of appointments per month per type for specified customers */
SELECT
    MONTHNAME(A.Start) AS "Month",
    A.Type,
    COUNT(A.Appointment_ID) AS "Count"
FROM
	appointments A
LEFT JOIN
	customers C on A.Customer_ID = C.Customer_ID
WHERE
	A.Customer_ID LIKE ?
GROUP BY
    MONTHNAME(A.Start),
    A.Type
ORDER BY
    MONTHNAME(A.Start),
    A.Type