entity ${nf("upperCamelCase",tableName)} {
<#list columns as col>
    <#if !(("," + pv('codegen.excludeColumn') + ",") ? contains("," + col.columnName + ","))>
        ${nf("lowerCamelCase",col.columnName)} ${nf("jdlFormatFieldType", tf(col.dataType) )} <#if col.isNullable()>required</#if><#if col?has_next >,</#if>
    </#if>
</#list>
}