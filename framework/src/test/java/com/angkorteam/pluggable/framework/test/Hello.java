package com.angkorteam.pluggable.framework.test;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.Target;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by socheat on 12/06/14.
 */
@Entity
@Table(name = "tbl_hello" ,indexes = {@Index(columnList = "people")})
public class Hello implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hello_id",columnDefinition = "INT")
    private Long id;

    @Column(name = "people")
    private String people;

    @Column(name = "people1")
    private double people1;

    public Hello() {
    }

    public static void main(String[] args) {
        Configuration hello =new Configuration();

        Properties properties = new Properties();
        properties.setProperty(Environment.DIALECT, MySQL5Dialect.class.getName());
        hello.setProperties(properties);
        hello.addAnnotatedClass(Hello.class);
        hello.addAnnotatedClass(Hello1.class);
        SchemaExport schemaExport = new SchemaExport(hello);
        schemaExport.setDelimiter(";");
//        schemaExport.setOutputFile(String.format("%s_%s.%s ", new Object[] {"ddl", dialect.name().toLowerCase(), "sql" }));
         boolean consolePrint = true;
         boolean exportInDatabase = false;
//         schemaExport.create(consolePrint, exportInDatabase);
        schemaExport.create(Target.SCRIPT);

    }
}
