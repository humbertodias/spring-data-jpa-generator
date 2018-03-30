package cn.x.codegen.template;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.context.EnvironmentAware;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Can be read in the template application.properties
 */
@Component
public class PropertiesFunction implements TemplateMethodModelEx ,EnvironmentAware {

    @Override
    public Object exec(List arguments) {
        SimpleScalar name = (SimpleScalar)arguments.get(0);
        return getValue((name).getAsString());
    }

    private org.springframework.core.env.Environment springEnvironment;

    public String getValue(String name) {
        return springEnvironment.getProperty(name);
    }

    @Override
    public void setEnvironment(org.springframework.core.env.Environment environment) {
        this.springEnvironment = environment;
    }
}
