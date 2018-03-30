package ${pv("codegen.output.repository.package")};

import ${pv("codegen.output.entity.package")}.${nf("upperCamelCase",tableName)};
//import cn.xxxx.repository.IRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import javax.transaction.Transactional;

/**
 * ${tableComment!""}
 * table :${tableName}
 * @author spring-data-jpa-generator
 * code generate time is ${.now}
 */
@RepositoryRestResource
public interface ${nf("upperCamelCase",tableName)}Repository extends JpaRepository<${nf("upperCamelCase",tableName)}, ${tf(pk.dataType)}> {
}

