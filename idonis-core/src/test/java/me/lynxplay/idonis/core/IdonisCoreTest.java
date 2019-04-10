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

package me.lynxplay.idonis.core;

import me.lynxplay.idonis.Idonis;
import me.lynxplay.idonis.IdonisContainer;
import me.lynxplay.idonis.dialect.SQLDialect;
import me.lynxplay.idonis.dialect.StatementKey;
import org.junit.Test;

import java.nio.file.Path;
import java.util.ServiceLoader;

import static org.junit.Assert.*;

public class IdonisCoreTest {

    private IdonisCore core = new IdonisCore();

    @Test
    public void testKeyGeneration() {
        StatementKey key = this.core.simpleStringPath(Path.of("test"));
        assertNotNull(key);
    }

    @Test
    public void testContainerGeneration() {
        Path root = Path.of("");
        for (SQLDialect dialect : SQLDialect.values()) {
            IdonisContainer container = core.forDialect(root, dialect);
            assertEquals(dialect.resolve(root), container.path());
        }
    }

    @Test
    public void findService() {
        assertTrue("The idonis service was not present", ServiceLoader.load(Idonis.class).findFirst().isPresent());
    }
}
