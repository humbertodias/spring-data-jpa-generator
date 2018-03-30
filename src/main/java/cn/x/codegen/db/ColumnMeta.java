package cn.x.codegen.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Field information
 * @author xslong
 * @time 2017/11/6 13:57
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ColumnMeta {

    // id auto_increment
    private String tableName;
    private String columnName;
    private Integer ordinalPosition; // 1-
    private String columnDefault;
    private String dataType;
    private Integer characterMaximumLength;
    private Integer numericPrecision;
    private Integer numericScale;// Decimal point
    private String columnType;
    private String columnKey;
    private String extra;
    private String columnComment;
    private boolean isNullable;// no yes
    private boolean isPrimaryKey;
    private boolean isAutoIncrement;
}
