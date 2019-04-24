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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ValidStatementWrapper implements PreparedStatement {

    private PreparedStatement preparedStatement;
    private Map<Integer, List<Integer>> fakeIndicesMap;

    /**
     * Creates a new valid statement wrapper
     *
     * @param preparedStatement the inner statement
     * @param fakeIndicesMap the fake indices map
     */
    public ValidStatementWrapper(PreparedStatement preparedStatement, Map<Integer, List<Integer>> fakeIndicesMap) {
        this.preparedStatement = preparedStatement;
        this.fakeIndicesMap = fakeIndicesMap;
    }

    /**
     * Runs the given index based on the fake index map
     *
     * @param index the fake index
     * @param consumer the consumer for each actual index
     */
    private void of(int index, StatementIndexConsumer consumer) throws SQLException {
        List<Integer> list = fakeIndicesMap.get(index);
        if (list != null) {
            for (Integer i : list) {
                consumer.accept(i);
            }
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return preparedStatement.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        return preparedStatement.executeUpdate();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        of(parameterIndex, p -> preparedStatement.setNull(p, sqlType));
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBoolean(i, x));
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setByte(i, x));
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setShort(i, x));
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setInt(i, x));
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setLong(i, x));
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setFloat(i, x));
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setDouble(i, x));
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBigDecimal(i, x));
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setString(i, x));
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBytes(i, x));
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setDate(i, x));
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setTime(i, x));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setTimestamp(i, x));
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setAsciiStream(i, x, length));
    }

    @Override
    @Deprecated(since = "1.2")
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setUnicodeStream(i, x, length));
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBinaryStream(i, x, length));
    }

    @Override
    public void clearParameters() throws SQLException {
        preparedStatement.clearParameters();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setObject(i, x, targetSqlType));
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setObject(i, x));
    }

    @Override
    public boolean execute() throws SQLException {
        return preparedStatement.execute();
    }

    @Override
    public void addBatch() throws SQLException {
        preparedStatement.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setCharacterStream(i, reader, length));
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setRef(i, x));
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBlob(i, x));
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setClob(i, x));
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setArray(i, x));
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return preparedStatement.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setDate(i, x, cal));
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setTime(i, x, cal));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setTimestamp(i, x, cal));
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setNull(i, sqlType, typeName));
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setURL(i, x));
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return preparedStatement.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setRowId(i, x));
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setNString(i, value));
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setNCharacterStream(parameterIndex, value, length));
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setNClob(i, value));
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setClob(i, reader, length));
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBlob(i, inputStream, length));
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setNClob(i, reader, length));
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setSQLXML(i, xmlObject));
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setObject(i, x, targetSqlType, scaleOrLength));
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setAsciiStream(i, x, length));
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBinaryStream(i, x, length));
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setCharacterStream(i, reader, length));
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setAsciiStream(i, x));
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBinaryStream(i, x));
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setCharacterStream(i, reader));
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setNCharacterStream(i, value));
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setClob(i, reader));
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setBlob(i, inputStream));
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setNClob(i, reader));
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setObject(i, x, targetSqlType, scaleOrLength));
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        of(parameterIndex, i -> preparedStatement.setObject(i, x, targetSqlType));
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        return preparedStatement.executeLargeUpdate();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return preparedStatement.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return preparedStatement.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        preparedStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return preparedStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        preparedStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return preparedStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        preparedStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        preparedStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return preparedStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        preparedStatement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        preparedStatement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return preparedStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        preparedStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        preparedStatement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return preparedStatement.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return preparedStatement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return preparedStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return preparedStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        preparedStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return preparedStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        preparedStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return preparedStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return preparedStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return preparedStatement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        preparedStatement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        preparedStatement.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return preparedStatement.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return preparedStatement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return preparedStatement.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return preparedStatement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return preparedStatement.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return preparedStatement.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return preparedStatement.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return preparedStatement.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return preparedStatement.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return preparedStatement.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return preparedStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return preparedStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        preparedStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return preparedStatement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        preparedStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return preparedStatement.isCloseOnCompletion();
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        return preparedStatement.getLargeUpdateCount();
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        preparedStatement.setLargeMaxRows(max);
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        return preparedStatement.getLargeMaxRows();
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        return preparedStatement.executeLargeBatch();
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return preparedStatement.executeLargeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return preparedStatement.executeLargeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return preparedStatement.executeLargeUpdate(sql, columnIndexes);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return preparedStatement.executeLargeUpdate(sql, columnNames);
    }

    @Override
    public String enquoteLiteral(String val) throws SQLException {
        return preparedStatement.enquoteLiteral(val);
    }

    @Override
    public String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException {
        return preparedStatement.enquoteIdentifier(identifier, alwaysQuote);
    }

    @Override
    public boolean isSimpleIdentifier(String identifier) throws SQLException {
        return preparedStatement.isSimpleIdentifier(identifier);
    }

    @Override
    public String enquoteNCharLiteral(String val) throws SQLException {
        return preparedStatement.enquoteNCharLiteral(val);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return preparedStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return preparedStatement.isWrapperFor(iface);
    }

    @Override
    public boolean equals(Object obj) {
        return this.preparedStatement.equals(obj);
    }

    /**
     * Returns the wrapped statement
     *
     * @return the statement
     */
    public PreparedStatement getWrapped() {
        return preparedStatement;
    }
}
