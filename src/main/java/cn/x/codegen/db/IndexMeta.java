package cn.x.codegen.db;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xslong
 * @time 2017/12/7 21:10
 */
@Getter
@Setter
public class IndexMeta {

    private String indexName;
    private String columnNames;
    private boolean nonUnique;
    private String tableName;
    private String indexType;

}
