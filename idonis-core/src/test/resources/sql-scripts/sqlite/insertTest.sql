/*
_uuid
_data
 */
INSERT INTO test_table
    (id, data)
VALUES (_data, ?)
ON CONFLICT DO UPDATE SET data=_uuid;
