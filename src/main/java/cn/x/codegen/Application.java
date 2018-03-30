package cn.x.codegen;

import cn.x.codegen.db.AnalysisMetaDB;
import cn.x.codegen.db.TableMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Main
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"cn.x.codegen", "generated"})
public class Application implements CommandLineRunner {


    public static void main(String ... args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${codegen.enabled}")
    private Boolean enabled;

    @Value("${codegen.tableSchema}")
    private String tableSchema;

    @Value("${codegen.tables}")
    private String tables;

    @Autowired
    private Connection connection;

    @Autowired
    private CodeGenerator codeGenerator;

    @Override
    public void run(String... args) throws SQLException {
        if(enabled) {
            AnalysisMetaDB an = new AnalysisMetaDB(connection);
            List<TableMeta> allTables = an.allTable(tableSchema, null);
            allTables.stream()
                    .filter( t -> tables.contains(t.getTableName()) || tables.isEmpty() )
                    .forEach(  this::generateCode );
            codeGenerator.afterProcess();
        }
    }

    public void generateCode(TableMeta tableMeta) {
        try {
            codeGenerator.process(tableMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}