package cn.x.codegen.db;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Index
 */
@Getter
@Setter
public class IndexMeta {
    @NonNull
    private String indexName;
    private String columnNames;
    private boolean nonUnique;
    private String tableName;
    private int indexType;
}


enum IndexType{
    tableIndexStatistic,
    tableIndexClustered,
    tableIndexHashed,
    tableIndexOther
}
