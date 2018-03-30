package cn.x.codegen;

import cn.x.codegen.db.TableMeta;
import cn.x.codegen.utils.NameUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;

/**
 * Generate code
 * @author xslong
 * @time 2017/11/6 16:58
 */
@Component
public class CodeGenerator implements InitializingBean {

    private final static String CHARSET = "UTF-8";

    @Autowired
    private Configuration freemarkerConfiguration;

    @Value("${codegen.module}")
    private String module;

    @Value("${codegen.tablePrefix}")
    private String tablePrefix;

    @Value("${codegen.output.entity.overwrite}")
    private boolean entityOverwrite;
    @Value("${codegen.output.entity.path}")
    private String entityPath;
    @Value("${codegen.output.entity.package}")
    private String entityPackage;


    @Value("${codegen.output.repository.overwrite}")
    private boolean repositoryOverwrite;
    @Value("${codegen.output.repository.path}")
    private String repositoryPath;
    @Value("${codegen.output.repository.package}")
    private String repositoryPackage;

    @Value("${codegen.output.controller.overwrite}")
    private boolean controllerOverwrite;
    @Value("${codegen.output.controller.path}")
    private String controllerPath;
    @Value("${codegen.output.controller.package}")
    private String controllerPackage;


    @Value("${codegen.output.configuration.overwrite}")
    private boolean configurationOverwrite;
    @Value("${codegen.output.configuration.path}")
    private String configurationPath;
    @Value("${codegen.output.configuration.package}")
    private String configurationPackage;


    @Override
    public void afterPropertiesSet() {
        checkOutputDir(entityPath, entityPackage);
        checkOutputDir(repositoryPath, repositoryPackage);
//        checkOutputDir(servicePath, servicePackage);
//        checkOutputDir(serviceimplPath, serviceimplPackage);
        checkOutputDir(controllerPath, controllerPackage);

    }

    public void afterProcess() {

        try {
            Template configurationTpl = freemarkerConfiguration.getTemplate("configuration.ftl", CHARSET);
            String configurationCode = FreeMarkerTemplateUtils.processTemplateIntoString(configurationTpl, this);
            String configurationCodePath = checkOutputDir(configurationPath, configurationPackage) + File.separator + "AppConfiguration.java";
            writeFile(configurationCodePath, configurationCode, controllerOverwrite);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String checkOutputDir(String path, String pack) {
        String fullPath = path + File.separator + StringUtils.join(pack.split("\\."), File.separator);
        File output = new File(fullPath);
        if (!output.exists()) {
            output.mkdirs();
        }
        return fullPath;
    }


    public void process(TableMeta tableMeta) throws Exception {

        if (!tableMeta.hasPrimaryKey()) {
            System.err.format("Skipping table %s because doesn't have a primary key\n", tableMeta.getTableName());
        }else {

            String tableName = tableMeta.getTableName();
            if (StringUtils.isNoneBlank(tablePrefix) && tableName.startsWith(tablePrefix)) {
                tableName = tableName.substring(tablePrefix.length());
            }
            String entityName = NameUtils.upperCamelCase(tableName);

            Template entityTpl = freemarkerConfiguration.getTemplate("entity.ftl", CHARSET);
            String entityCode = FreeMarkerTemplateUtils.processTemplateIntoString(entityTpl, tableMeta);
            String entityCodePath = checkOutputDir(entityPath, entityPackage) + File.separator + entityName + ".java";
            writeFile(entityCodePath, entityCode, entityOverwrite);

            Template repositoryTpl = freemarkerConfiguration.getTemplate("repository.ftl", CHARSET);
            String repositoryCode = FreeMarkerTemplateUtils.processTemplateIntoString(repositoryTpl, tableMeta);
            String repositoryCodePath = checkOutputDir(repositoryPath, repositoryPackage) + File.separator + entityName + "Repository.java";
            writeFile(repositoryCodePath, repositoryCode, repositoryOverwrite);

            Template controllerTpl = freemarkerConfiguration.getTemplate("controller.ftl", CHARSET);
            String controllerCode = FreeMarkerTemplateUtils.processTemplateIntoString(controllerTpl, tableMeta);
            String controllerCodePath = checkOutputDir(controllerPath, controllerPackage) + File.separator + entityName + "Controller.java";
            writeFile(controllerCodePath, controllerCode, controllerOverwrite);

        }
    }

    private void writeFile(String path, String code, boolean overwrite) throws IOException {
        File file = new File(path);
        if (!file.exists() || overwrite) {
            Files.write(file.toPath(), Collections.singleton(code), Charset.forName(CHARSET));
        }
    }


}
