package cn.x.codegen.db;

import lombok.Getter;
import lombok.Setter;

/**
 * Index
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
