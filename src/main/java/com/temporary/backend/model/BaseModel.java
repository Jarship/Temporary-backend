package com.temporary.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@JsonIgnoreProperties({"columnMap"})
public abstract class BaseModel {
    protected transient Logger logger = Logger.getLogger(BaseModel.class.getSimpleName());

    protected Map<String,Integer> columnMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private static List<Field> getFields(Class c) {
        List<Field> fields = new ArrayList<>();

        while (c != null) {
            Field[] declared = c.getDeclaredFields();
            Collections.addAll(fields, declared);
            c = c.getSuperclass();
        }
        return fields;
    }

    /**
     * Returns a list of column names suitable for a SQL query, assuming that the standard field name to column
     * name translation applies. Transient fields and static fields are ignored.
     *
     * @param prefix The desired table name to prefix the column names, o rnull if none.
     */
    public static String getColumnNames(@SuppressWarnings("rawtypes") Class c, String prefix) {
        StringBuilder st = new StringBuilder();
        if (prefix != null)
            prefix = prefix + ".";

        List<Field> fields = getFields(c);

        for (Field field: fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers))
                continue;

            String columnName = (prefix != null ? prefix : "") + fieldNameToColumnName(field.getName());

            if (st.length() > 0)
                st.append(",");

            st.append(columnName);
        }
        return st.toString();
    }

    protected BaseModel() {}

    protected BaseModel(ResultSet rs) throws SQLException {
        this(rs, "");
    }

    /**
     * This constructor provides a reflection-driven mapping between the columns of a ResultSet
     * and the properties of the object. Each property of the object is set if a matching column
     * can be found in the ResultSet, using the standard name mapping (location_id -> locationId).
     *
     * Properties that are transient or static are ignored, as are any columns in the ResultSet
     * that do not match a property name.
     */
    @SuppressWarnings("rawtypes")
    protected BaseModel(ResultSet rs, String useOnlyWithPrefix) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int count = md.getColumnCount();
        columnMap.clear();
        for (int i = 1; i <= count; i++) {
            String name = md.getColumnName(i);
            if (!columnMap.containsKey(name) && name.startsWith(useOnlyWithPrefix))
                columnMap.put(name.replaceFirst(useOnlyWithPrefix, ""), i);
            String label = md.getColumnLabel(i);
            if (!columnMap.containsKey(label) && label.startsWith(useOnlyWithPrefix))
                columnMap.put(label.replaceFirst(useOnlyWithPrefix, ""), i);
        }

        List<Field> fields = getFields(this.getClass());

        for (Field field: fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers))
                continue;

            field.setAccessible(true);

            String columnName = fieldNameToColumnName(field.getName());
            Integer columnIndex = columnMap.get(columnName);
            if (columnIndex != null) {
                Class type = field.getType();
                String typename = type.getSimpleName();

                try {
                    if (typename.equals("int")) {
                        field.setInt(this, rs.getInt(columnIndex));
                    } else if (typename.equals("long")) {
                        field.setLong(this, rs.getLong(columnIndex));
                    } else if (typename.equals("double")) {
                        field.setDouble(this, rs.getDouble(columnIndex));
                    } else if (typename.equals("float")) {
                        field.setFloat(this, rs.getFloat(columnIndex));
                    } else if (typename.equals("boolean")) {
                        field.setBoolean(this, rs.getBoolean(columnIndex));
                    } else if (typename.equals("String")) {
                        String value = rs.getString(columnIndex);
                        field.set(this, value);
                    } else if (typename.equals("Integer")) {
                        int value = rs.getInt(columnIndex);
                        if (rs.wasNull()) {
                            field.set(this, null);
                        } else {
                            field.set(this, value);
                        }
                    } else if (typename.equals("Double")) {
                        double value = rs.getDouble(columnIndex);
                        if (rs.wasNull()) {
                            field.set(this, null);
                        } else {
                            field.set(this, value);
                        }
                    } else if (typename.equals("Boolean")) {
                        int value = rs.getInt(columnIndex);
                        if (rs.wasNull()) {
                            field.set(this, null);
                        } else {
                            field.set(this, value != 0 ? Boolean.TRUE : Boolean.FALSE);
                        }
                    } else if (typename.equals("Date")) {
                        Date date = rs.getTimestamp(columnIndex);
                        if (rs.wasNull()) {
                            field.set(this, null);
                        } else {
                            field.set(this, date);
                        }
                    } else if (typename.equals("Timestamp")) {
                        Timestamp date = rs.getTimestamp(columnIndex);
                        if (rs.wasNull()) {
                            field.set(this, null);
                        } else {
                            field.set(this, date);
                        }
                    } else if (typename.equals("BigDecimal")) {
                        BigDecimal value = rs.getBigDecimal(columnIndex);
                        if (rs.wasNull()) {
                            field.set(this, null);
                        } else {
                            field.set(this, value);
                        }
                    } else {
                        if (field.getType().isEnum()) {
                            String value = rs.getString(columnIndex);
                            if (!rs.wasNull()) {
                                field.set(this, Enum.valueOf((Class<Enum>) field.getType(), rs.getString(columnIndex)));
                            }
                        }
                    }
                } catch (Exception e ) {
                    logger.log(Level.WARNING, e.getLocalizedMessage(), e);
                }
            }
        }
    }

    // routeCode -> route_code
    // companyID -> company_id
    private static String fieldNameToColumnName(String fieldName) {
        StringBuilder columnName = new StringBuilder();
        boolean wasUpper = false;
        for(int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!wasUpper)
                    columnName.append("_");
                columnName.append(Character.toLowerCase(c));
                wasUpper = true;
            } else {
                columnName.append(c);
                wasUpper = false;
            }
        }
        return columnName.toString();
    }
}
