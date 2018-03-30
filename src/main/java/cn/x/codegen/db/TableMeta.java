package cn.x.codegen.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Table information
 * @author xslong
 * @time 2017/11/6 11:53
 */
@Getter
@Setter
@ToString
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

}
