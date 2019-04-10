INSERT INTO test_table
    (id, data)
VALUES (?, ?)
ON CONFLICT DO UPDATE SET data=excluded.data
