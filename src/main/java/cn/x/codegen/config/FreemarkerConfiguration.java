package cn.x.codegen.config;

import cn.x.codegen.template.NameFunction;
import cn.x.codegen.template.PropertiesFunction;
import cn.x.codegen.template.TypeFunction;
import freemarker.template.Configuration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FreemarkerConfiguration implements InitializingBean {

    @Autowired
    private Configuration configuration;

    @Autowired
    private PropertiesFunction propertiesFunction;
    @Autowired
    private NameFunction nameFunction;
    @Autowired
    private TypeFunction typeFunction;

    @Override
    public void afterPropertiesSet() throws Exception {
        //Configurar funciones personalizadas de plantilla
        configuration.setSharedVariable("pv",propertiesFunction);
        configuration.setSharedVariable("nf",nameFunction);
        configuration.setSharedVariable("tf",typeFunction);
    }

}
