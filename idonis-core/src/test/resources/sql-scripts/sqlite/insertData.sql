/*
@name
@id
 */
INSERT INTO test (id, name)
VALUES (@id, @name)
ON CONFLICT(id) DO UPDATE SET name=@name;
