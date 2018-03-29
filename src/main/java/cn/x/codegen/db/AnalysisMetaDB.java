package cn.x.codegen.db;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Analyze the data table structure
 * @author xslong
 * @time 2017/11/6 11:53
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
        ResultSet rs = metaData.getTables(catalog, tableSchema, tableNamePrefix, null);
        while(rs.next()) {
            TableMeta tm = new TableMeta();
            tm.setCatalog(rs.getString("TABLE_CAT"));
            tm.setTableSchema(rs.getString("TABLE_SCHEM"));
            tm.setTableName(rs.getString("TABLE_NAME"));
            tm.setTableComment(rs.getString("REMARKS"));
            list.add(tm);
        }
        return list;
    }

    public List<ColumnMeta> allColumn(TableMeta tableMeta) throws SQLException {
        List<ColumnMeta> list = new ArrayList<>();

        List<IndexMeta> primaryKeys = getPrimaryKeys(tableMeta);

        ResultSet rs = metaData.getColumns(tableMeta.getCatalog(), tableMeta.getTableSchema(), tableMeta.getTableName()  , "%");
        while(rs.next()){
            ColumnMeta cm = new ColumnMeta();
            cm.setTableName(rs.getString("TABLE_NAME"));
            cm.setColumnName(rs.getString("COLUMN_NAME"));
            if (rs.getObject("ORDINAL_POSITION") != null) {
                cm.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
            }
            cm.setColumnDefault(rs.getString("COLUMN_DEF"));
            cm.setIsNullable(rs.getString("IS_NULLABLE"));

            int jdbcType = rs.getInt("DATA_TYPE");
            String jdbcTypeName = JDBCType.valueOf(jdbcType).getName().toLowerCase();
            cm.setColumnType(jdbcTypeName);
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
//            cm.setColumnKey(rs.getString("COLUMN_KEY"));
            cm.setPrimaryKey( isPrimaryKey(cm, primaryKeys) );
            cm.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));
            cm.setColumnComment(rs.getString("REMARKS"));
            list.add(cm);
        }

        return list;
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


    public List<IndexMeta> allIndex(TableMeta tableMeta) throws SQLException {
        List<IndexMeta> list = new ArrayList<>();

        ResultSet rs = metaData.getIndexInfo(tableMeta.getCatalog(), tableMeta.getTableSchema(), tableMeta.getTableName(), false, false);

        while (rs.next()) {
            IndexMeta im = new IndexMeta();
            im.setIndexName(rs.getString("INDEX_NAME"));
            im.setColumnNames(rs.getString("COLUMN_NAME"));
            im.setNonUnique(rs.getInt("NON_UNIQUE"));
            im.setTableName(rs.getString("TABLE_NAME"));
            im.setIndexType(rs.getString("TYPE"));
            list.add(im);
        }

        return list;
    }

    public ColumnMeta pk(List<ColumnMeta> columnMetas) {
        for (ColumnMeta col : columnMetas) {
            if (col.isPrimaryKey()) {
                return col;
            }
        }
        return null;
    }

    public boolean isMySql() throws SQLException {
        return metaData.getDatabaseProductName().toLowerCase().startsWith("mysql");
    }

}
