/* Get records that were created or updated by a given user */
SELECT
	U.User_Name,
	'Customer' AS File_Type,
	C.Customer_ID AS File_ID,
	CASE
		WHEN U.User_Name = C.Last_Updated_By THEN C.Last_Update
		ELSE C.Create_Date
	END AS Update_Time
FROM
	customers C
INNER JOIN
	users U 
    ON U.User_Name = C.Created_By 
    OR U.User_Name = C.Last_Updated_By 
WHERE
	C.Last_Update >= { ts ? }
    AND U.User_Name = ?

UNION    

SELECT
	U.User_Name,
	'Appointment' AS File_Type,
	A.Appointment_ID AS File_ID,
	CASE
		WHEN U.User_Name = A.Last_Updated_By THEN A.Last_Update
		ELSE A.Create_Date
	END AS Update_Time
FROM
	appointments A
INNER JOIN
	users U 
    ON U.User_Name = A.Created_By 
    OR U.User_Name = A.Last_Updated_By 
WHERE
	A.Last_Update >= { ts ? }
    AND U.User_Name = ?

ORDER BY
	Update_Time
