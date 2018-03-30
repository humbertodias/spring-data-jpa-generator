package cn.x.codegen;

import cn.x.codegen.db.AnalysisMetaDB;
import cn.x.codegen.db.ColumnMeta;
import cn.x.codegen.db.TableMeta;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main
 * @author xslong
 * @time 2017/11/6 10:45
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"cn.x.codegen", "cn.xxxxxx"})
public class CodegenApplication implements CommandLineRunner {


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


    public static void main(String ... args) {
        SpringApplication.run(CodegenApplication.class, args);
    }

    @Override
    public void run(String... args) throws SQLException {
        if(enabled) {
            generateCode();
        }
    }


    public void generateCode() throws SQLException {
        final Set<String> tableSet = new HashSet<>();
        if (StringUtils.isNoneBlank(tables)) {
            tableSet.addAll(Arrays.asList(tables.split(",")));
        }
        AnalysisMetaDB an = new AnalysisMetaDB(connection);
        List<TableMeta> tables = an.allTable(tableSchema, null);
        tables.stream()
                .filter( t -> tableSet.isEmpty() || tableSet.contains(t.getTableName()))
                .forEach(t -> {
                    try {
                        if (t.hasPrimaryKey()) {
                            codeGenerator.process(t);
                        } else {
                            System.err.format("Skipping table %s because doesn't have a primary key\n", t.getTableName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        codeGenerator.afterProcess();
    }


}