/* Get first level divisions according to a filter by Country_ID */
SELECT
	*
FROM
	first_level_divisions
WHERE
    Country_ID LIKE ?