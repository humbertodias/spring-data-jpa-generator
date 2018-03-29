package cn.x.codegen.db;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Field information
 * @author xslong
 * @time 2017/11/6 13:57
 */
@Getter
@Setter
public class ColumnMeta {

    // id auto_increment
    private String tableName;
    private String columnName;
    private Integer ordinalPosition; // 1-
    private String columnDefault;
    private String isNullable;// no yes
    private String dataType;
    private Integer characterMaximumLength;
    private Integer numericPrecision;
    private Integer numericScale;// Decimal point
    private String columnType;
    private String columnKey;
    private String extra;
    private String columnComment;
    private boolean isPrimaryKey;
    private boolean isAutoIncrement;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
