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

package me.lynxplay.idonis.dialect.promise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * {@link StatementPromise} represents a statement which is stored in a string
 */
public interface StatementPromise {

    /**
     * Prepares the statement into a prepared statement on the given connection. If the {@link StatementPromise} is not
     * present, this call will throw a custom {@link SQLException}
     *
     * @param connection the connection instance
     *
     * @return the created prepared statement.
     *
     * @throws SQLException if the internal {@link Connection#prepareStatement(String)} call throws an exception or if
     * the {@link StatementPromise} is not presenta
     */
    PreparedStatement prepare(Connection connection) throws SQLException;

    /**
     * Returns if the promised statement is present
     *
     * @return the boolean
     */
    boolean isPresent();
}
