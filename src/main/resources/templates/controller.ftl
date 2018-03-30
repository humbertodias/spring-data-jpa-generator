package ${pv("codegen.output.controller.package")};

import ${pv("codegen.output.entity.package")}.${nf("upperCamelCase",tableName)};
import ${pv("codegen.output.repository.package")}.${nf("upperCamelCase",tableName)}Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

/**
 * ${tableComment}
 * table :${tableName}
 * @author spring-data-jpa-generator
 * Code generate time is ${.now}
 */
@RestController
@RequestMapping("/${tableName}")
public class ${nf("upperCamelCase",tableName)}Controller {

    @Autowired
    private ${nf("upperCamelCase",tableName)}Repository ${nf("lowerCamelCase",tableName)}Repository;

    @GetMapping
    public List<${nf("upperCamelCase",tableName)}> getAll() {
        return ${nf("lowerCamelCase",tableName)}Repository.findAll();
    }

}
