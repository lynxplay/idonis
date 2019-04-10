/*
@uuid=1
 */
INSERT INTO test_table
    (id, data)
VALUES (@uuid, @id)
ON CONFLICT DO UPDATE SET data=excluded.data
