package com.temporary.backend.dao;

import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Day;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseDAO {
    public static final String JDBC_CONTEXT = "java:/comp/env";
    public static final String JDBC_NAME = "jdbc/temporary";

    protected Logger logger = Logger.getLogger(BaseDAO.class.getSimpleName());

    private BaseDAO dao = null;
    private DataSource dataSource;
    private Connection sharedConnection = null;
    private int transactionCount = 0;
    private boolean rollBack = false;

    protected interface Populator {
        void nextResult(ResultSet rs) throws Exception;
    }

    protected BaseDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected BaseDAO(String jdbcName) throws RuntimeException {
        initJDBC(jdbcName);
    }

    private void initJDBC(String jdbcName) throws RuntimeException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup(JDBC_CONTEXT);
            dataSource = (DataSource) envContext.lookup(jdbcName);
        } catch(NamingException e) {
            throw new RuntimeException(e);
        }
    }

    protected BaseDAO() throws RuntimeException {
        this(JDBC_NAME);
    }

    protected BaseDAO(BaseDAO dao) {
        if (dao!= null)
            this.dao = dao;
        else
            initJDBC(JDBC_NAME);
    }

    public DataSource getDataSource() {
        return dao != null ? dao.getDataSource() : this.dataSource;
    }

    public boolean inTransaction() {
        if (dao != null)
            return dao.inTransaction();
        else
            return transactionCount > 0;
    }

    public boolean isRollBack() {
        return dao != null ? dao.isRollBack() : rollBack;
    }

    private DatabaseException convertException(SQLException e) {
        return new DatabaseException(e);
    }

    public void beginTransaction() throws DatabaseException {
        if (dao != null) {
            dao.beginTransaction();
        } else {
            transactionCount++;
            if (transactionCount > 1) return;
            try {
                sharedConnection = dataSource.getConnection();
                sharedConnection.setAutoCommit(false);
                rollBack = false;
            } catch(SQLException e) {
                throw this.convertException(e);
            }
        }
    }

    public void endTransaction() {
        if (dao != null) {
            dao.endTransaction();
        } else {
            if (transactionCount == 0) return;
            transactionCount--;
            if (transactionCount > 0) return;

            try {
                if (rollBack)
                    sharedConnection.rollback();
                else
                    sharedConnection.commit();
                sharedConnection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            } finally {
                try {
                    sharedConnection.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    sharedConnection = null;
                }
            }
        }
    }

    public void rollbackTransaction() {
        if (dao != null) {
            dao.rollbackTransaction();
        } else {
            if (transactionCount > 0)
                rollBack = true;
        }
    }

    protected Connection getConnection() throws DatabaseException {
        if (dao != null) {
            return dao.getConnection();
        }

        if (this.inTransaction()) {
            return sharedConnection;
        }
        try {
            Connection c = dataSource.getConnection();
            if (!c.getAutoCommit())
                c.setAutoCommit(true);
            return c;
        } catch(SQLException e) {
            throw this.convertException(e);
        }
    }

    protected void applyParametersToStatement(PreparedStatement st, Object... parameters) throws DatabaseException {
        if (parameters != null) {
            int index = 1;
            try {
                for (Object parameter: parameters) {
                    if (parameter instanceof Day) {
                        Day day = (Day) parameter;
                        st.setObject(index++, day.toString());
                    } else {
                        st.setObject(index++, parameter);
                    }
                }
            } catch(SQLException e) {
                this.rollbackTransaction();
                throw this.convertException(e);
            }
        }
    }

    protected void queryWithPopulator(String sql, Populator populator, Object... parameters) throws DatabaseException {
        Connection c = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            c = this.getConnection();
            st = c.prepareStatement(sql);
            this.applyParametersToStatement(st, parameters);

            rs = st.executeQuery();
            while (rs.next()) {
                populator.nextResult(rs);
            }
        } catch (SQLException e) {
            this.rollbackTransaction();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw this.convertException(e);
        } catch (Exception e) {
            this.rollbackTransaction();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            try {
                if (st != null && !st.isClosed()) {
                    st.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            if (!this.inTransaction()) {
                try {
                    if (c != null && !c.isClosed()) {
                        c.close();
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    protected <T> List<T> queryForList(String sql, Class<T> classOfT, Object... parameters) throws DatabaseException {
        final List<T> result = new ArrayList<>();

        try {
            final Constructor<T> constructor = classOfT.getConstructor(ResultSet.class);

            this.queryWithPopulator(sql, new Populator() {
                @Override
                public void nextResult(ResultSet rs) throws Exception {
                    T row = constructor.newInstance(rs);
                    result.add(row);
                }
            }, parameters);

            return result;
        } catch (NoSuchMethodException e) {
            this.rollbackTransaction();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    protected <T> T queryForObject(String sql, Class<T> classOfT, Object... parameters) throws DatabaseException {
        List<T> result = this.queryForList(sql, classOfT, parameters);
        if (result.size() == 0) {
            return null;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        this.rollbackTransaction();
        throw new DatabaseException(new SQLIntegrityConstraintViolationException("queryForObject returned " + result.size() + " results: " + sql));
    }

    protected List<String> queryForStrings(String sql, Object...parameters) throws DatabaseException {
        final List<String> result = new ArrayList<>();

        this.queryWithPopulator(sql, new Populator() {
            @Override
            public void nextResult(ResultSet rs) throws Exception {
                result.add(rs.getString(1));
            }
        }, parameters);

        return result;
    }

    protected String queryForString(String sql, Object... parameters) throws DatabaseException {
        List<String> result = this.queryForStrings(sql, parameters);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    protected int queryForInt(String sql, Object...parameters) throws DatabaseException {
        List<String> result = this.queryForStrings(sql, parameters);
        if (result.size() > 0) {
            try {
                return Integer.parseInt(result.get(0));
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    protected List<Integer> queryForInts(String sql, Object... parameters) throws DatabaseException {
        List<Integer> ints = new ArrayList<>();
        List<String> results = this.queryForStrings(sql, parameters);
        for (String result: results) {
            try {
                ints.add(Integer.parseInt(result));
            } catch (NumberFormatException ignored) {}
        }
        return ints;
    }

    protected Double queryForDouble(String sql, Object... parameters) throws DatabaseException {
        List<String> result = this.queryForStrings(sql, parameters);
        if (result.size() > 0) {
            try {
                if (result.get(0) == null) return 0.0;
                return Double.parseDouble(result.get(0));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }

    protected BigDecimal queryForDecimal(String sql, Object...parameters) throws DatabaseException {
        String result = this.queryForString(sql, parameters);
        if (result != null) {
            try {
                return new BigDecimal(result);
            } catch(Exception e) {
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.ZERO;
        }
    }

    protected boolean queryForBool(String sql, Object...parameters) throws DatabaseException {
        final boolean[] result = new boolean[1];
        this.queryWithPopulator(sql, new Populator() {
            @Override
            public void nextResult(ResultSet rs) throws Exception {
                result[0] = rs.getBoolean(1);
            }
        }, parameters);
        return result[0];
    }

    protected List<Map<String, Object>> queryForMaps(String sql, Object... parameters) throws DatabaseException {
        final List<Map<String, Object>> result = new ArrayList<>();

        this.queryWithPopulator(sql, new Populator() {
            @Override
            public void nextResult(ResultSet rs) throws Exception {
                Map<String, Object> map = new HashMap<>();
                int columns = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columns; i++) {
                    String name = rs.getMetaData().getColumnName(i);
                    Object value = rs.getObject(i);
                    map.put(name, value);
                }
                result.add(map);
            }
        }, parameters);
        return result;
    }

    protected List<Object> insertWithAutokey(String sql, Object... parameters) throws DatabaseException {
        List<Object> keys = new ArrayList<>();
        Connection c = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            c = this.getConnection();
            st = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.applyParametersToStatement(st, parameters);

            st.executeUpdate();
            rs = st.getGeneratedKeys();
            while (rs.next()) {
                keys.add(rs.getObject(1));
            }
        } catch (SQLException e) {
            this.rollbackTransaction();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw this.convertException(e);
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            try {
                if (st != null && !st.isClosed()) st.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            if (!this.inTransaction())
                try {
                    if (c != null && !c.isClosed()) c.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
        }
        return keys;
    }

    protected int insertWithIntegerAutokey(String sql, Object...parameters) throws DatabaseException {
        List<Object> result = this.insertWithAutokey(sql, parameters);
        if (result.size() > 0) {
            Object value = result.get(0);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else
                return -1;
        } else
            return 0;
    }

    protected int executeUpdate(String sql, Object...parameters) throws DatabaseException {
        Connection c = null;
        PreparedStatement st = null;

        try {
            c = this.getConnection();
            st = c.prepareStatement(sql);
            this.applyParametersToStatement(st, parameters);
            return st.executeUpdate();
        } catch (SQLException e ) {
            this.rollbackTransaction();
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw this.convertException(e);
        }  finally {
            try { if (st != null && !st.isClosed()) st.close(); } catch (SQLException e) { logger.log(Level.SEVERE, e.getMessage(), e); }
            if (!this.inTransaction())
                try { if (c != null && !c.isClosed()) c.close(); } catch (SQLException e) { logger.log(Level.SEVERE, e.getMessage(), e); }
        }
    }

    // SQL Helpers

    protected String valuesClause(int numColumns) {
        StringBuilder sql = new StringBuilder("VALUES(");
        for (int i = 0; i < numColumns; i++) {
            if (i > 0) sql.append(",");
            sql.append("?");
        }
        sql.append(")");
        return sql.toString();
    }

    protected static class IntegerListPopulator implements Populator {
        private List<Integer> integerList;
        private String columnName;

        public IntegerListPopulator(List<Integer> integerList, String columnName) {
            this.integerList = integerList;
            this.columnName = columnName;
        }

        @Override
        public void nextResult(ResultSet rs) throws Exception {
            this.integerList.add(rs.getInt(columnName));
        }
    }

    protected String enumsToSQLSet(Set<? extends Enum<?>> values) {
        StringBuilder result = new StringBuilder();
        if (values != null) {
            for (Enum<?> e : values) {
                if (result.length() > 0) result.append(",");
                result.append(e.name());
            }
        }
        return result.toString();
    }

    public static <T extends Enum<T>> Set<T> sqlSetToEnums(Class<T> enumType, String value) {
        Set<T> result = new HashSet<>();

        if (value != null && value.length() > 0) {
            String[] components = value.split(",");
            for (String c: components) {
                if (c.length() > 0) {
                    result.add(Enum.valueOf(enumType, c));
                }
            }
        }
        return result;
    }
 }
