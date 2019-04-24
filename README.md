# idonis

[![Build](https://travis-ci.org/LynxPlay101/idonis.svg?branch=master)](https://travis-ci.org/LynxPlay101/idonis)

A really small SQL script caching framework to store complex SQL statements outside of java code.

Install
---

As of right now, idonis was not accepted onto maven central and therefore you will have to manually install the project 
into your local maven repository. You can install the project using:

```bash
curl -sL https://raw.githubusercontent.com/LynxPlay101/idonis/master/scripts/downloadLatest.sh | bash
```

Usage
---

After adding idonis to your dependencies using:

```xml
<dependencies>
    <dependency>
        <groupId>me.lynxplay</groupId>
        <artifactId>idonis</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

you can now access the entry point using the `java.util.ServiceLoader`. Note that this dependency will contain the
actual implementation as it has to be provided in the runtime. If you have multiple projects using idonis, it is suggested
to provide the `idonis` jar to the runtime manually and only depend on a provided `idonis-api`.

You can request the IdonisContainer using: 

```java
class Showcase {
    public static void main(String[] args){
        Idonis i = ServiceLoader.load(Idonis.class).findFirst().orElseThrow();
        IdonisContainer c = i.forDialect(Path.of("your" , "resource" , "path") , SQLDialect.SQLITE);
    }
}
```

Using this container, you can now easily request a query stored in it using:

```java
IdonisContainer container = ...
try(PreparedStatement s = container.using("mySqlScript.sql").prepare(mySqlConnection)) {
    s.setString(1 , "name");
    ...
} catch(SQLException e) {
    ...
}
```

As SQL scripts may not preserve placeholder order between languages, `idonis` also provides a simple way to abstract 
templates as well. When defining an SQL script, you should replace all `?` templates with idonis variables. 
For example, this UPSERT SQLite3 script:
```sql
/*
@id
@first_name
@last_name
*/
INSERT INTO table (id, first_name, last_name)
    VALUES (@id, @first_name, @last_name)
    ON CONFLICT(id) DO UPDATE SET first_name=@first_name,
                                  last_name=@last_name;
```
