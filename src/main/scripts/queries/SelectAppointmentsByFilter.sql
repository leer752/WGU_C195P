/* Get appointments according to a filter by Customer_ID, Start, and End  */
SELECT
	*
FROM 
	appointments
WHERE
	Customer_ID LIKE ? AND
    Start >= { ts ? } AND
    End <= { ts ? }