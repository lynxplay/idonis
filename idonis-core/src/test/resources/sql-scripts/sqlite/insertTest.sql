/*
@uuid
@data
 */
INSERT INTO test_table
    (id, data, other_data)
VALUES (@data, ?, @data)
ON CONFLICT(id) DO UPDATE SET data=@uuid,
                              other_data=@data;
