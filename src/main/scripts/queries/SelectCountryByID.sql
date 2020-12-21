/* Get countries according to a filter by Country_ID */
SELECT
	*
FROM
	countries
WHERE
    Country_ID LIKE ?