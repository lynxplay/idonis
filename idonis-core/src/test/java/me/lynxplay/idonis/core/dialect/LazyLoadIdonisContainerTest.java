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

package me.lynxplay.idonis.core.dialect;

import me.lynxplay.idonis.Idonis;
import me.lynxplay.idonis.core.IdonisCore;
import me.lynxplay.idonis.core.dialect.promise.parser.ValidStatementParser;
import me.lynxplay.idonis.core.util.ConnectionMock;
import me.lynxplay.idonis.dialect.SQLScriptNotFoundException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LazyLoadIdonisContainerTest {

    private Idonis idonis = new IdonisCore();
    private LazyLoadIdonisContainer container = new LazyLoadIdonisContainer(Path.of("src/test/resources/sql-scripts/sqlite"),
            idonis::simpleStringPath, new ValidStatementParser());

    @Test(expected = SQLScriptNotFoundException.class)
    public void testNotExistingFile() throws SQLException {
        this.container.using("updateScript.sql").prepare(null);
    }

    @Test
    public void testNotExistingFileState() {
        assertFalse(this.container.using("updateScript.sql").isPresent());
    }

    @Test
    public void testExistingFile() throws SQLException {
        this.container.using("insertTest.sql").prepare(new ConnectionMock());
    }

    @Test
    public void testExistingFileState() {
        assertTrue(this.container.using("insertTest.sql").isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readNonReadableFile() {
        this.container = new LazyLoadIdonisContainer(Path.of("src/test/resources/sql-scripts/sqlite"),
                idonis::simpleStringPath,
                p -> {
                    throw new IOException("This container is error injected and cannot read files");
                }, new ValidStatementParser());
        this.container.using("insertTest.sql");
    }
}
