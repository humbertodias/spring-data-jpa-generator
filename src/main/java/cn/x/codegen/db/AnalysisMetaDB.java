package cn.x.codegen.db;

import cn.x.codegen.utils.SQLUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<TableMeta> allTable(String tableSchema) throws SQLException {
        return allTable(tableSchema, null);
    }

    /**
     * Read table list
     * @return
     */
    public List<TableMeta> allTable(String tableSchema, String prefix) throws SQLException {
        List<TableMeta> list = new ArrayList<>();
        String tableNamePrefix = prefix != null ? prefix + "%" : "%";
        ResultSet rs = metaData.getTables(null, tableSchema, tableNamePrefix, new String []{"TABLES"});
        while(rs.next()) {
            TableMeta tm = new TableMeta();
            tm.setTableSchema(rs.getString("TABLE_SCHEM"));
            tm.setTableName(rs.getString("TABLE_NAME"));
            tm.setTableComment(rs.getString("REMARKS"));
            list.add(tm);
        }
        return list;
    }

    public List<ColumnMeta> allColumn(String tableSchema, String tableName) throws SQLException {
        List<ColumnMeta> list = new ArrayList<>();

//        Statement st = connection.prepareStatement("select * from " + tableName + " where 0=1");
//        ResultSet rs = connection.getColumns(null, tableSchema,  tableName, "%");
        ResultSet rs = metaData.getColumns(null, tableSchema,  tableName, "%");

//        ResultSet rs = metaData.getColumns(null, tableSchema,  tableName, "%");

        while(rs.next()){
            ColumnMeta cm = new ColumnMeta();
            cm.setTableName(rs.getString("TABLE_NAME"));
            cm.setColumnName(rs.getString("COLUMN_NAME"));
            if (rs.getObject("ORDINAL_POSITION") != null) {
                cm.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
            }
            cm.setColumnDefault(rs.getString("COLUMN_DEF"));
            cm.setIsNullable(rs.getString("IS_NULLABLE"));
            cm.setDataType(rs.getString("DATA_TYPE"));

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
            if(rs.getString("COLUMN_NAME").equalsIgnoreCase("ID")) {
                cm.setColumnKey("PRI");
            }

            List<ColumnMeta> primaryKeys = getPrimaryKeys(tableSchema, tableName);
            System.out.println(primaryKeys);

            if(rs.getString("DATA_TYPE") != null){
                int jdbcType = rs.getInt("DATA_TYPE");
                cm.setColumnType(JDBCType.valueOf(jdbcType).getName());
            }
            if("YES".equals(rs.getString("IS_AUTOINCREMENT"))) {
                cm.setExtra("auto_increment");
            }
            cm.setColumnComment(rs.getString("REMARKS"));
            list.add(cm);
        }

        return list;
    }


    public List<ColumnMeta> getPrimaryKeys(String tableSchema, String tableName) throws SQLException {
        List<ColumnMeta> list = new ArrayList<>();

        ResultSet rs = metaData.getPrimaryKeys(null,tableSchema, tableName);

        while (rs.next()) {
            ColumnMeta cm = new ColumnMeta();
            cm.setTableName(rs.getString("TABLE_NAME"));
            cm.setColumnName(rs.getString("COLUMN_NAME"));
            if (rs.getObject("ORDINAL_POSITION") != null) {
                cm.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
            }
            cm.setColumnDefault(rs.getString("COLUMN_DEF"));
            cm.setIsNullable(rs.getString("IS_NULLABLE"));
            cm.setDataType(rs.getString("DATA_TYPE"));
            list.add(cm);
        }

        return list;
    }


    public List<IndexMeta> allIndex(String tableSchema, String tableName) throws SQLException {
        List<IndexMeta> list = new ArrayList<>();

        ResultSet rs = metaData.getIndexInfo(null, tableSchema, tableName, false, false);

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

/*
        public List<IndexMeta> allIndex(String tableSchema, String tableName) {
        String sql = "select INDEX_NAME,COLUMN_NAME,NON_UNIQUE,TABLE_NAME,INDEX_TYPE\n" +
                "from information_schema.STATISTICS\n" +
                "where TABLE_SCHEMA = ? \n" +
                "and INDEX_NAME <> 'PRIMARY'\n" +
                "and TABLE_NAME = ? \n" +
                "order by SEQ_IN_INDEX";
        String [] parameters = {tableSchema, tableName};
        List<IndexMeta> list = new ArrayList<>();
        SQLUtils.execute(connection, sql, new SQLUtils.ResultSetLoop() {
            @Override
            public void each(int count, ResultSet rs) throws SQLException {
                IndexMeta im = new IndexMeta();
                im.setIndexName(rs.getString("INDEX_NAME"));
                im.setColumnNames(rs.getString("COLUMN_NAME"));
                im.setNonUnique(rs.getInt("NON_UNIQUE"));
                im.setTableName(rs.getString("TABLE_NAME"));
                im.setIndexType(rs.getString("INDEX_TYPE"));
                list.add(im);
            }
        }, parameters);
        Map<String, List<IndexMeta>> newMap = list.stream().collect(Collectors.groupingBy(IndexMeta::getIndexName, Collectors.toList()));
        List<IndexMeta> result = new ArrayList<>();
        newMap.forEach((key, metas) -> {
            if (metas.size() != 1) {
                String columnNames = StringUtils.join(metas.stream().map(IndexMeta::getColumnNames).collect(Collectors.toList()), ",");
                metas.get(0).setColumnNames(columnNames);
            }
            result.add(metas.get(0));
        });
        return result;
    }
*/

    public ColumnMeta pk(List<ColumnMeta> columnMetas) {
        for (ColumnMeta col : columnMetas) {
            if ("PRI".equals(col.getColumnKey())) {
                return col;
            }
        }
        return null;
    }

}
