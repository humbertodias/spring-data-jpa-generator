package cn.x.codegen.template;

import cn.x.codegen.utils.NameUtils;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Used directly in the template NameUtils.class Methods
 */
@Component
public class NameFunction implements TemplateMethodModelEx {

    @Value("${codegen.tablePrefix}")
    private String tablePrefix;

    @Override
    public Object exec(List arguments) {
        String funName = ((SimpleScalar) arguments.get(0)).getAsString();
        String param1 = ((SimpleScalar) arguments.get(1)).getAsString();
        if (StringUtils.isNoneBlank(tablePrefix)) {
            if (("upperCamelCase".equals(funName) || "lowerCamelCase".equals(funName)) && param1.startsWith(tablePrefix)) {
                param1 = param1.substring(tablePrefix.length());
            }
        }

        try {
            Method method = NameUtils.class.getDeclaredMethod(funName, String.class);
            return method.invoke(null, param1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
