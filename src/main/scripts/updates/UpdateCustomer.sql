/* UPDATE an existing customer in customers by Customer_ID */
UPDATE
	customers
SET
	Customer_Name=?,
    Address=?,
    Postal_Code=?,
    Phone=?,
    Last_Update=?,
    Last_Updated_By=?,
    Division_ID=?
WHERE
	Customer_ID=?