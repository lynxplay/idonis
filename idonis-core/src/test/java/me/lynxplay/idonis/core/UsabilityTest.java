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
import org.junit.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ServiceLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsabilityTest {

    @Test
    public void testFunctionality() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        connection.prepareStatement("CREATE TABLE test (id INTEGER, name VARCHAR(16));").executeUpdate();

        Idonis idonis = ServiceLoader.load(Idonis.class).findFirst().orElseThrow();
        IdonisContainer container = idonis.forDialect(Path.of("src/test/resources/sql-scripts"), SQLDialect.SQLITE);

        try (PreparedStatement s = container.using("insertData.sql").prepare(connection)) {
            s.setString(1, "LynxPlay");
            s.setInt(2, 0);
            s.executeUpdate();
        }

        try (PreparedStatement s = connection.prepareStatement("SELECT * FROM test")) {
            ResultSet resultSet = s.executeQuery();
            assertTrue(resultSet.next());

            String name = resultSet.getString("name");
            int id = resultSet.getInt("id");

            assertEquals("LynxPlay", name);
            assertEquals(0, id);
        }

        connection.close();
    }

}
