/* Get customers according to a filter by Customer_ID */
SELECT
	*
FROM
	customers
WHERE
	Customer_ID LIKE ?