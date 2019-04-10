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

package me.lynxplay.idonis.dialect;

import java.sql.SQLException;

/**
 * This exception is thrown if the {@link StatementPromise} is empty but someone tries to execute it anyway
 */
public class SQLScriptNotFoundException extends SQLException {

    /**
     * Constructs a <code>SQLScriptNotFoundException</code> object with a given
     * <code>reason</code>. The  <code>SQLState</code>  is initialized to
     * <code>null</code> and the vendor code is initialized to 0.
     * <p>
     * The <code>cause</code> is not initialized, and may subsequently be initialized by a call to the {@link
     * Throwable#initCause(Throwable)} method.
     *
     * @param reason a description of the exception
     */
    public SQLScriptNotFoundException(String reason) {
        super(reason);
    }
}
