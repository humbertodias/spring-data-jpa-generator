package cn.x.codegen.db;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Table information
 * @author xslong
 * @time 2017/11/6 11:53
 */
@Getter
@Setter
public class TableMeta {

    private String catalog;
    private String tableSchema;
    private String tableName;
    private String tableComment;

    private List<ColumnMeta> columns = new ArrayList<>();
    private List<IndexMeta> indexs = new ArrayList<>();

    private ColumnMeta pk;

    public boolean hasPrimaryKey(){
        return pk != null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
