/* Get first level divisions according to a filter by Division_ID */
SELECT
	*
FROM
	first_level_divisions
WHERE
    Division_ID LIKE ?