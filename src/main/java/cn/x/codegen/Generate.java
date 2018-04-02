package cn.x.codegen;

import cn.x.codegen.db.AnalysisMetaDB;
import cn.x.codegen.db.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.util.List;

/**
 * Main
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Generate implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(Generate.class);

    public static void main(String ... args) {
        SpringApplication.run(Generate.class, args).close();
    }

    @Value("${codegen.catalog}")
    private String catalog;

    @Value("${codegen.tableSchema}")
    private String tableSchema;

    @Value("${codegen.tables}")
    private String tables;

    @Autowired
    private Connection connection;

    @Autowired
    private CodeGenerator codeGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        AnalysisMetaDB an = new AnalysisMetaDB(connection);
        List<TableMeta> allTables = an.allTable(catalog, tableSchema);
        allTables.stream()
                .filter( t -> tables.contains(t.getTableName()) || tables.isEmpty() )
                .forEach( this::generateCode );
    }

    public void generateCode(TableMeta tableMeta) {
        try {
            codeGenerator.process(tableMeta);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

