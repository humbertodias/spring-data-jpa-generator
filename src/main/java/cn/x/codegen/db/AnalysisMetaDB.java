package cn.x.codegen.db;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Analyze the data table structure
 */
public class AnalysisMetaDB {

    private Connection connection;
    private final DatabaseMetaData metaData;

    public AnalysisMetaDB(Connection connection) throws SQLException {
        this.connection = connection;
        this.metaData = connection.getMetaData();
    }

    public List<TableMeta> allTable(String catalog, String tableSchema) throws SQLException {
        return allTable(catalog, tableSchema, null);
    }

    /**
     * Read table list
     * @return
     */
    public List<TableMeta> allTable(String catalog, String tableSchema, String prefix) throws SQLException {
        List<TableMeta> list = new ArrayList<>();
        String tableNamePrefix = prefix != null ? prefix + "%" : "%";
        String [] tables = {"TABLE"};
        ResultSet rs = metaData.getTables(catalog, tableSchema, tableNamePrefix, tables);
        while(rs.next()) {
            TableMeta tm = new TableMeta();
            tm.setCatalog(rs.getString("TABLE_CAT"));
            tm.setTableSchema(rs.getString("TABLE_SCHEM"));
            tm.setTableName(rs.getString("TABLE_NAME"));
            tm.setTableComment(rs.getString("REMARKS"));
            list.add(tm);

            allColumn(tm);
            allIndex(tm);
        }
        return list;
    }

    /**
     * https://docs.oracle.com/javase/9/docs/api/java/sql/DatabaseMetaData.html#getColumns-java.lang.String-java.lang.String-java.lang.String-java.lang.String-
     * @param tableMeta
     * @throws SQLException
     */
    public void allColumn(TableMeta tableMeta) throws SQLException {
        List<IndexMeta> primaryKeys = getPrimaryKeys(tableMeta);

        ResultSet rs = metaData.getColumns(tableMeta.getCatalog(), tableMeta.getTableSchema(), tableMeta.getTableName()  , "%");
        while(rs.next()){
            ColumnMeta cm = new ColumnMeta();
            cm.setTableName(rs.getString("TABLE_NAME"));
            cm.setColumnName(rs.getString("COLUMN_NAME"));
            cm.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
            cm.setColumnDefault(rs.getString("COLUMN_DEF"));

            int jdbcType = rs.getInt("DATA_TYPE");
            String jdbcTypeName = JDBCType.valueOf(jdbcType).getName().toLowerCase();
            cm.setDataType(jdbcTypeName);

            if (rs.getObject("COLUMN_SIZE") != null) {
                Long character_maximum_length = rs.getLong("COLUMN_SIZE");
                cm.setCharacterMaximumLength(character_maximum_length.intValue());
            }
            if (rs.getObject("DECIMAL_DIGITS") != null) {
                cm.setNumericPrecision(rs.getInt("DECIMAL_DIGITS"));
            }
//            if (rs.getObject("NUMERIC_SCALE") != null) {
//                cm.setNumericScale(rs.getInt("NUMERIC_SCALE"));
//            }
            boolean isPK = isPrimaryKey(cm, primaryKeys);
            cm.setPrimaryKey( isPK );
            if(isPK){
                tableMeta.setPk(cm);
            }

            cm.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));
            cm.setNullable("YES".equals(rs.getString("IS_NULLABLE")));
            cm.setColumnComment(rs.getString("REMARKS"));

            tableMeta.getColumns().add(cm);

        }
    }

    private boolean isPrimaryKey(ColumnMeta columnMeta, List<IndexMeta> primaryKeys){
        return primaryKeys.stream().filter(idx -> idx.getColumnNames().equalsIgnoreCase(columnMeta.getColumnName()) ).count() > 0 ;
    }

    public List<IndexMeta> getPrimaryKeys(TableMeta tableMeta) throws SQLException {
        List<IndexMeta> list = new ArrayList<>();

        ResultSet rs = metaData.getPrimaryKeys(tableMeta.getCatalog(),tableMeta.getTableSchema(), tableMeta.getTableName());
        while (rs.next()) {
            IndexMeta im = new IndexMeta();
            im.setIndexName(rs.getString("PK_NAME"));
            im.setColumnNames(rs.getString("COLUMN_NAME"));
            //im.setNonUnique(rs.getInt("NON_UNIQUE"));
            im.setTableName(rs.getString("TABLE_NAME"));
            //im.setIndexType(rs.getString("TYPE"));
            list.add(im);
        }

        return list;
    }

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html#getIndexInfo(java.lang.String,%20java.lang.String,%20java.lang.String,%20boolean,%20boolean)
     * @param tableMeta
     * @throws SQLException
     */
    public void allIndex(TableMeta tableMeta) throws SQLException {
        ResultSet rs = metaData.getIndexInfo(tableMeta.getCatalog(), tableMeta.getTableSchema(), tableMeta.getTableName(), false, false);

        while (rs.next()) {
            IndexMeta im = new IndexMeta();
            int indexType = rs.getInt("TYPE");
            if(IndexType.tableIndexStatistic.ordinal() == indexType
            || IndexType.tableIndexOther.ordinal() == indexType){
                continue;
            }
            im.setIndexType(indexType);
            im.setIndexName(rs.getString("INDEX_NAME"));
            im.setColumnNames(rs.getString("COLUMN_NAME"));
            im.setNonUnique(rs.getBoolean("NON_UNIQUE"));
            im.setTableName(rs.getString("TABLE_NAME"));

            tableMeta.getIndexs().add(im);
        }
    }

}
