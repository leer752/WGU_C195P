/* Get customers according to a filter by Customer_Name  */
SELECT
	*
FROM 
	customers
WHERE
	Customer_Name LIKE ?