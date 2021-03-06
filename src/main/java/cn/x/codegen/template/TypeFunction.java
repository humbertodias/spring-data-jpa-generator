package cn.x.codegen.template;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * SQL type in the template to java type
 */
@Component
@ConfigurationProperties("codegen")
@Configuration
public class TypeFunction implements TemplateMethodModelEx {

    private Map<String, String> columnTypeMapping;

    @Override
    public Object exec(List arguments) {
        String sqlType = ((SimpleScalar) arguments.get(0)).getAsString();
        String javaType = columnTypeMapping.get(sqlType);
        if (javaType == null) {
            throw new RuntimeException("No binding type : " + sqlType);
        }
        return javaType;
    }

    public Map<String, String> getColumnTypeMapping() {
        return columnTypeMapping;
    }

    public void setColumnTypeMapping(Map<String, String> columnTypeMapping) {
        this.columnTypeMapping = columnTypeMapping;
    }
}
