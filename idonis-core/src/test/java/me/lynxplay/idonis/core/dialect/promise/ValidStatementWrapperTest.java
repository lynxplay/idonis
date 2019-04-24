/*
 * MIT License
 *
 * Copyright (c) 2019 Bjarne Koll
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.lynxplay.idonis.core.dialect.promise;

import me.lynxplay.idonis.core.util.StatementConsumer;
import me.lynxplay.idonis.core.util.StatementReturnConsumer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.core.CoreStatement;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ValidStatementWrapperTest {

    private static final String QUERY = "SELECT * FROM sqlite_master WHERE name = ?";
    private static final Field BATCH_FIELD;

    static {
        try {
            BATCH_FIELD = CoreStatement.class.getDeclaredField("batch");
            BATCH_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not setup reflective access", e);
        }
    }

    private Connection connection;

    private PreparedStatement normal;
    private ValidStatementWrapper wrapped;
    private ValidStatementWrapper wrappedNull;

    @Before
    public void before() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            this.normal = connection.prepareStatement(QUERY);
            this.wrapped = new ValidStatementWrapper(connection.prepareStatement(QUERY), Map.of(1, List.of(1)));
            this.wrappedNull = new ValidStatementWrapper(new EmptyStatementMock(), Map.of());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load sql drivers", e);
        } catch (SQLException e) {
            throw new RuntimeException("Could not open sql connection");
        }
    }

    @After
    public void after() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not close sql connection", e);
        }
    }

    /**
     * Tests with the given statement consumer
     *
     * @param c the consumer
     */
    public void test(StatementConsumer c) throws SQLException, IllegalAccessException {
        c.accept(wrappedNull);
        c.accept(wrapped); // Call wrapped first to execute own code
        c.accept(normal);

        Assert.assertArrayEquals((Object[]) BATCH_FIELD.get(normal)
                , (Object[]) BATCH_FIELD.get(wrapped.getWrapped()));
    }

    /**
     * Tests with the given statement consumer
     *
     * @param c the consumer
     */
    public void testReturn(StatementReturnConsumer c) throws SQLException, IllegalAccessException {
        Object wrapped = c.accept(this.wrapped);// Call wrapped first to execute own code
        Object expected = c.accept(normal);

        Assert.assertEquals(expected, wrapped);

        Assert.assertArrayEquals((Object[]) BATCH_FIELD.get(normal)
                , (Object[]) BATCH_FIELD.get(this.wrapped.getWrapped()));
    }

    @Test
    public void setObject() throws SQLException, IllegalAccessException {
        Object object = new Object();
        test(s -> s.setObject(1, object));
    }

    @Test
    public void setObject1() throws SQLException, IllegalAccessException {
        Object o = new Object();
        test(s -> s.setObject(1, o, Types.BLOB));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void executeLargeUpdate() throws SQLException, IllegalAccessException {
        test(PreparedStatement::executeLargeUpdate);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getLargeUpdateCount() throws SQLException, IllegalAccessException {
        testReturn(Statement::getLargeUpdateCount);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setLargeMaxRows() throws SQLException, IllegalAccessException {
        test(s -> s.setLargeMaxRows(1));
    }

    @Test
    public void getLargeMaxRows() throws SQLException, IllegalAccessException {
        testReturn(Statement::getLargeMaxRows);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void executeLargeBatch() throws SQLException, IllegalAccessException {
        testReturn(Statement::executeLargeBatch);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void executeLargeUpdate1() throws SQLException, IllegalAccessException {
        testReturn(s -> s.executeLargeUpdate(""));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void executeLargeUpdate2() throws SQLException, IllegalAccessException {
        testReturn(s -> s.executeLargeUpdate("", new int[0]));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void executeLargeUpdate3() throws SQLException, IllegalAccessException {
        testReturn(s -> s.executeLargeUpdate("", new String[0]));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void executeLargeUpdate4() throws SQLException, IllegalAccessException {
        testReturn(s -> s.executeLargeUpdate("", 0));
    }

    @Test
    public void enquoteLiteral() throws SQLException, IllegalAccessException {
        testReturn(s -> s.enquoteLiteral("test"));
    }

    @Test
    public void enquoteIdentifier() throws SQLException, IllegalAccessException {
        testReturn(s -> s.enquoteIdentifier("test", true));
    }

    @Test
    public void isSimpleIdentifier() throws SQLException, IllegalAccessException {
        testReturn(s -> s.isSimpleIdentifier("?"));
    }

    @Test
    public void enquoteNCharLiteral() throws SQLException, IllegalAccessException {
        testReturn(s -> s.enquoteNCharLiteral("test"));
    }

    @Test
    public void executeQuery() throws SQLException, IllegalAccessException {
        test(s -> {
            s.setString(1, "test");
            s.executeQuery();
        });
    }

    @Test(expected = SQLException.class) // Updating on a query will throw an exception
    public void executeUpdate() throws SQLException, IllegalAccessException {
        test(s -> {
            s.setString(1, "test");
            s.executeUpdate();
        });
    }

    @Test
    public void setNull() throws SQLException, IllegalAccessException {
        test(s -> s.setNull(1, Types.BLOB));
    }

    @Test
    public void setBoolean() throws SQLException, IllegalAccessException {
        test(s -> s.setBoolean(1, true));
    }

    @Test
    public void setByte() throws SQLException, IllegalAccessException {
        test(s -> s.setByte(1, (byte) 0x0));
    }

    @Test
    public void setShort() throws SQLException, IllegalAccessException {
        test(s -> s.setShort(1, (short) 0));
    }

    @Test
    public void setInt() throws SQLException, IllegalAccessException {
        test(s -> s.setInt(1, 0));
    }

    @Test
    public void setLong() throws SQLException, IllegalAccessException {
        test(s -> s.setLong(1, Long.MAX_VALUE));
    }

    @Test
    public void setFloat() throws SQLException, IllegalAccessException {
        test(s -> s.setFloat(1, 0F));
    }

    @Test
    public void setDouble() throws SQLException, IllegalAccessException {
        test(s -> s.setDouble(1, 15F));
    }

    @Test
    public void setBigDecimal() throws SQLException, IllegalAccessException {
        test(s -> s.setBigDecimal(1, new BigDecimal("15")));
    }

    @Test
    public void setString() throws SQLException, IllegalAccessException {
        test(s -> s.setString(1, "name"));
    }

    @Test
    public void setBytes() throws SQLException, IllegalAccessException {
        test(s -> s.setBytes(1, "test".getBytes()));
    }

    @Test
    public void setDate() throws SQLException, IllegalAccessException {
        Date date = new Date(System.currentTimeMillis());
        test(s -> s.setDate(1, date));
    }

    @Test
    public void setTime() throws SQLException, IllegalAccessException {
        Time time = Time.valueOf(LocalTime.now());
        test(s -> s.setTime(1, time));
    }

    @Test
    public void setTimestamp() throws SQLException, IllegalAccessException {
        Timestamp t = Timestamp.from(Instant.now());
        test(s -> s.setTimestamp(1, t));
    }

    @Test
    public void setAsciiStream() throws SQLException, IllegalAccessException {
        byte[] bytes = "a".getBytes();
        test(s -> s.setAsciiStream(1, new ByteArrayInputStream(bytes), bytes.length));
    }

    @Test
    @Deprecated
    public void setUnicodeStream() throws SQLException, IllegalAccessException {
        byte[] bytes = "b".getBytes();
        test(s -> s.setUnicodeStream(1, new ByteArrayInputStream(bytes), bytes.length));
    }

    @Test
    public void setBinaryStream() throws SQLException, IllegalAccessException {
        byte[] b = {0x1, 0xf};
        test(s -> s.setBinaryStream(1, new ByteArrayInputStream(b), b.length));
    }

    @Test
    public void clearParameters() throws SQLException, IllegalAccessException {
        test(PreparedStatement::clearParameters);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setObject2() throws SQLException, IllegalAccessException {
        Object o = new Object();
        test(s -> s.setObject(1, o, JDBCType.BLOB));
    }

    @Test
    public void setObject3() throws SQLException, IllegalAccessException {
        Object o = new Object();
        test(s -> s.setObject(1, o, Types.BLOB, 1));
    }

    @Test
    public void execute() throws SQLException, IllegalAccessException {
        test(s -> {
            s.setString(1, "test");
            s.execute();
        });
    }

    @Test
    public void addBatch() throws SQLException, IllegalAccessException {
        test(s -> {
            s.setString(1, "test");
            s.addBatch();
        });
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setCharacterStream() throws SQLException, IllegalAccessException {
        test(s -> s.setCharacterStream(1, new CharArrayReader("test".toCharArray()), 4L));
    }

    @Test(expected = SQLException.class)
    public void setRef() throws SQLException, IllegalAccessException {
        test(s -> s.setRef(1, null));
    }

    @Test(expected = SQLException.class)
    public void setBlob() throws SQLException, IllegalAccessException {
        test(s -> s.setBlob(1, new SerialBlob("test".getBytes())));
    }

    @Test(expected = SQLException.class)
    public void setClob() throws SQLException, IllegalAccessException {
        test(s -> s.setClob(1, new SerialClob("test".toCharArray())));
    }

    @Test(expected = SQLException.class)
    public void setArray() throws SQLException, IllegalAccessException {
        test(s -> s.setArray(1, null));
    }

    @Test
    public void getMetaData() throws SQLException, IllegalAccessException {
        test(PreparedStatement::getMetaData);
    }

    @Test
    public void setDate1() throws SQLException, IllegalAccessException {
        Date d = new Date(System.currentTimeMillis());
        test(s -> s.setDate(1, d, Calendar.getInstance()));
    }

    @Test
    public void setTime1() throws SQLException, IllegalAccessException {
        Time t = Time.valueOf(LocalTime.now());
        test(s -> s.setTime(1, t, Calendar.getInstance()));
    }

    @Test
    public void setTimestamp1() throws SQLException, IllegalAccessException {
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        test(s -> s.setTimestamp(1, t, Calendar.getInstance()));
    }

    @Test
    public void setNull1() throws SQLException, IllegalAccessException {
        test(s -> s.setNull(1, Types.BLOB, "blob"));
    }

    @Test(expected = SQLException.class)
    public void setURL() throws SQLException, IllegalAccessException {
        test(s -> {
            try {
                s.setURL(1, new URL("http://localhost:8080"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void getParameterMetaData() throws SQLException, IllegalAccessException {
        test(PreparedStatement::getParameterMetaData);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setRowId() throws SQLException, IllegalAccessException {
        test(s -> s.setRowId(1, () -> {
            return new byte[0];
        }));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setNString() throws SQLException, IllegalAccessException {
        test(s -> s.setNString(1, ""));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setNCharacterStream() throws SQLException, IllegalAccessException {
        test(s -> s.setNCharacterStream(1, new StringReader("test")));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setNClob() throws SQLException, IllegalAccessException {
        test(s -> s.setNClob(1, (NClob) null));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setClob1() throws SQLException, IllegalAccessException {
        test(s -> s.setClob(1, new StringReader("a")));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setBlob1() throws SQLException, IllegalAccessException {
        test(s -> s.setBlob(1, new ByteArrayInputStream(new byte[0]), 0));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setNClob1() throws SQLException, IllegalAccessException {
        test(s -> s.setNClob(1, (Reader) null));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setSQLXML() throws SQLException, IllegalAccessException {
        test(s -> s.setSQLXML(1, null));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setObject4() throws SQLException, IllegalAccessException {
        Object o = new Object();
        test(s -> s.setObject(1, o, JDBCType.BLOB, 1));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setAsciiStream1() throws SQLException, IllegalAccessException {
        test(s -> s.setAsciiStream(1, new ByteArrayInputStream(new byte[0]), 0L));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setBinaryStream1() throws SQLException, IllegalAccessException {
        test(s -> s.setBinaryStream(1, new ByteArrayInputStream(new byte[0]), 0L));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setCharacterStream1() throws SQLException, IllegalAccessException {
        test(s -> s.setCharacterStream(1, new StringReader("a")));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setAsciiStream2() throws SQLException, IllegalAccessException {
        test(s -> s.setAsciiStream(1, new ByteArrayInputStream(new byte[0])));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setBinaryStream2() throws SQLException, IllegalAccessException {
        test(s -> s.setBinaryStream(1, new ByteArrayInputStream(new byte[0])));
    }

    @Test
    public void setCharacterStream2() throws SQLException, IllegalAccessException {
        test(s -> s.setCharacterStream(1, new StringReader(""), 0));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setNCharacterStream1() throws SQLException, IllegalAccessException {
        test(s -> s.setNCharacterStream(1, new StringReader(""), 0L));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setClob2() throws SQLException, IllegalAccessException {
        test(s -> s.setClob(1, new StringReader(""), 0L));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setBlob2() throws SQLException, IllegalAccessException {
        test(s -> s.setBlob(1, new ByteArrayInputStream(new byte[0]), 0L));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setBlob3() throws SQLException, IllegalAccessException {
        test(s -> s.setBlob(1, new ByteArrayInputStream(new byte[0])));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void setNClob2() throws SQLException, IllegalAccessException {
        test(s -> s.setNClob(1, new StringReader(""), 0L));
    }

    @Test(expected = SQLException.class)
    public void executeQuery1() throws SQLException, IllegalAccessException {
        test(s -> s.executeQuery("SELECT * FROM sqlite_master"));
    }

    @Test(expected = SQLException.class)
    public void executeUpdate1() throws SQLException, IllegalAccessException {
        test(s -> s.executeUpdate(""));
    }

    @Test
    public void close() throws SQLException, IllegalAccessException {
        test(Statement::close);
    }

    @Test
    public void getMaxFieldSize() throws SQLException, IllegalAccessException {
        testReturn(Statement::getMaxFieldSize);
    }

    @Test
    public void setMaxFieldSize() throws SQLException, IllegalAccessException {
        test(s -> s.setMaxFieldSize(0));
    }

    @Test
    public void getMaxRows() throws SQLException, IllegalAccessException {
        testReturn(Statement::getMaxRows);
    }

    @Test
    public void setMaxRows() throws SQLException, IllegalAccessException {
        test(s -> s.setMaxRows(1));
    }

    @Test
    public void setEscapeProcessing() throws SQLException, IllegalAccessException {
        test(s -> s.setEscapeProcessing(false));
    }

    @Test
    public void getQueryTimeout() throws SQLException, IllegalAccessException {
        testReturn(Statement::getQueryTimeout);
    }

    @Test
    public void setQueryTimeout() throws SQLException, IllegalAccessException {
        test(s -> s.setQueryTimeout(10));
    }

    @Test
    public void cancel() throws SQLException, IllegalAccessException {
        test(Statement::cancel);
    }

    @Test
    public void getWarnings() throws SQLException, IllegalAccessException {
        testReturn(Statement::getWarnings);
    }

    @Test
    public void clearWarnings() throws SQLException, IllegalAccessException {
        test(Statement::clearWarnings);
    }

    @Test
    public void setCursorName() throws SQLException, IllegalAccessException {
        test(s -> s.setCursorName("test"));
    }

    @Test(expected = SQLException.class)
    public void execute1() throws SQLException, IllegalAccessException {
        test(s -> s.execute(""));
    }

    @Test
    public void getResultSet() throws SQLException, IllegalAccessException {
        test(Statement::getResultSet);
    }

    @Test
    public void getUpdateCount() throws SQLException, IllegalAccessException {
        testReturn(Statement::getUpdateCount);
    }

    @Test
    public void getMoreResults() throws SQLException, IllegalAccessException {
        testReturn(Statement::getMoreResults);
    }

    @Test(expected = SQLException.class)
    public void setFetchDirection() throws SQLException, IllegalAccessException {
        test(s -> s.setFetchDirection(ResultSet.FETCH_FORWARD));
    }

    @Test(expected = SQLException.class)
    public void getFetchDirection() throws SQLException, IllegalAccessException {
        testReturn(Statement::getFetchDirection);
    }

    @Test
    public void setFetchSize() throws SQLException, IllegalAccessException {
        test(s -> s.setFetchSize(10));
    }

    @Test
    public void getFetchSize() throws SQLException, IllegalAccessException {
        testReturn(Statement::getFetchSize);
    }

    @Test
    public void getResultSetConcurrency() throws SQLException, IllegalAccessException {
        testReturn(Statement::getResultSetConcurrency);
    }

    @Test
    public void getResultSetType() throws SQLException, IllegalAccessException {
        testReturn(Statement::getResultSetType);
    }

    @Test(expected = SQLException.class)
    public void addBatch1() throws SQLException, IllegalAccessException {
        test(s -> {
            s.setString(1, "");
            s.addBatch("");
        });
    }

    @Test
    public void clearBatch() throws SQLException, IllegalAccessException {
        test(Statement::clearBatch);
    }

    @Test
    public void executeBatch() throws SQLException, IllegalAccessException {
        test(Statement::executeBatch);
    }

    @Test
    public void getConnection() throws SQLException, IllegalAccessException {
        testReturn(Statement::getConnection);
    }

    @Test
    public void getMoreResults1() throws SQLException, IllegalAccessException {
        testReturn(s -> s.getMoreResults(Statement.KEEP_CURRENT_RESULT));
    }

    @Test
    public void getGeneratedKeys() throws SQLException, IllegalAccessException {
        testReturn(Statement::getGeneratedKeys);
    }

    @Test(expected = SQLException.class)
    public void executeUpdate2() throws SQLException, IllegalAccessException {
        test(s -> s.executeUpdate(" ", new int[0]));
    }

    @Test(expected = SQLException.class)
    public void executeUpdate3() throws SQLException, IllegalAccessException {
        test(s -> s.executeUpdate("", new String[0]));
    }

    @Test(expected = SQLException.class)
    public void executeUpdate4() throws SQLException, IllegalAccessException {
        test(s -> s.executeUpdate("", Statement.NO_GENERATED_KEYS));
    }

    @Test(expected = SQLException.class)
    public void execute2() throws SQLException, IllegalAccessException {
        test(s -> s.execute(" ", new int[0]));
    }

    @Test(expected = SQLException.class)
    public void execute3() throws SQLException, IllegalAccessException {
        test(s -> s.execute(" ", new String[0]));
    }

    @Test(expected = SQLException.class)
    public void execute4() throws SQLException, IllegalAccessException {
        test(s -> s.execute("", Statement.NO_GENERATED_KEYS));
    }

    @Test
    public void getResultSetHoldability() throws SQLException, IllegalAccessException {
        testReturn(Statement::getResultSetHoldability);
    }

    @Test
    public void isClosed() throws SQLException, IllegalAccessException {
        testReturn(Statement::isClosed);
    }

    @Test
    public void setPoolable() throws SQLException, IllegalAccessException {
        test(s -> s.setPoolable(true));
    }

    @Test
    public void isPoolable() throws SQLException, IllegalAccessException {
        testReturn(Statement::isPoolable);
    }

    @Test
    public void closeOnCompletion() throws SQLException, IllegalAccessException {
        test(Statement::closeOnCompletion);
    }

    @Test
    public void isCloseOnCompletion() throws SQLException, IllegalAccessException {
        testReturn(Statement::isCloseOnCompletion);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getLargeUpdateCount1() throws SQLException, IllegalAccessException {
        testReturn(Statement::getLargeUpdateCount);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setLargeMaxRows1() throws SQLException, IllegalAccessException {
        test(s -> s.setLargeMaxRows(10L));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void executeLargeBatch1() throws SQLException, IllegalAccessException {
        testReturn(Statement::executeLargeBatch);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void executeLargeUpdate6() throws SQLException, IllegalAccessException {
        test(PreparedStatement::executeLargeUpdate);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void executeLargeUpdate7() throws SQLException, IllegalAccessException {
        test(s -> s.executeLargeUpdate(""));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void executeLargeUpdate8() throws SQLException, IllegalAccessException {
        test(s -> s.executeLargeUpdate(" ", Statement.NO_GENERATED_KEYS));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void executeLargeUpdate9() throws SQLException, IllegalAccessException {
        test(s -> s.executeLargeUpdate(" ", new int[0]));
    }

    @Test
    public void unwrap() throws SQLException, IllegalAccessException {
        test(s -> s.unwrap(PreparedStatement.class));
    }

    @Test
    public void isWrapperFor() throws SQLException, IllegalAccessException {
        testReturn(s -> s.isWrapperFor(PreparedStatement.class));
    }

    @Test
    public void equals1() throws SQLException, IllegalAccessException {
        PreparedStatement st = connection.prepareStatement("SELECT * FROM sqlite_master");
        testReturn(s -> s.equals(st));
        st.close();
    }
}
