package ${pv("codegen.output.entity.package")};

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import ${pv("codegen.output.entity.baseclass")};
import java.io.Serializable;


/**
 * ${tableComment!""}
 * table :${tableName}
 * @author spring-data-jpa-generator
 * code generate time is ${.now}
 */
@Getter
@Setter
@Entity
@Table(
    name = "${tableName}"<#if indexs?has_content>,
    indexes = {
    <#list indexs as idx>
        @Index(name = "${idx.indexName}", columnList = "${idx.columnNames}"<#if idx.isNonUnique()>, unique = true</#if>),
    </#list>
    }
</#if>
)
public class ${nf("upperCamelCase",tableName)}
<#if (simpleClassName??)> extends ${nf("simpleClassName",pv("codegen.output.entity.baseclass"))} </#if> implements Serializable {
<#list columns as col>
    <#if !(("," + pv('codegen.excludeColumn') + ",") ? contains("," + col.columnName + ","))>

    <#if (col.columnComment??)>// ${col.columnComment}</#if><#if (col.columnDefault??)>
    // default: ${col.columnDefault}</#if><#if (col.isPrimaryKey())>
    @Id</#if><#if (col.isAutoIncrement())>
    @GeneratedValue(strategy = GenerationType.AUTO)</#if>
    @Column(name = "${col.columnName}"<#if col.isNullable()>, nullable = false</#if><#if (col.characterMaximumLength??)>, length = ${col
.characterMaximumLength?replace(',', '')}</#if><#if (col.numericScale?? && col.numericScale > 0)>, scale = ${col.numericScale}</#if>)
    private ${tf(col.dataType)} ${nf("lowerCamelCase",col.columnName)};
    </#if>
</#list>

}