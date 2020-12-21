/* Get contacts according to a filter by Contact_ID */
SELECT 
	*
FROM 
	contacts
WHERE
	Contact_ID LIKE ?