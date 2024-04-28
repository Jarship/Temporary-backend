package com.temporary.backend.dao;

import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Account;
import com.temporary.backend.model.Assembly;

import java.util.List;
import java.sql.ResultSet;

public class AssemblyDAO extends BaseDAO {

    public class AssemblyPopulator implements Populator{
        Assembly a = null;
        @Override
        public void nextResult(ResultSet rs) throws Exception {
            if (a == null) {
                a = new Assembly(rs);
            }
            String relationship = rs.getString("relationship");
            if (!rs.wasNull() && relationship.length() > 0) {
                if (relationship == "FOLLOW") {
                    a.getFollowers().add(new Account(rs));
                }
            }

        }
        public Assembly getAssembly() { return a; }
    }
    public AssemblyDAO() {}
    public AssemblyDAO(BaseDAO dao) { super(dao); }

    public int createAssembly(Assembly assembly) throws DatabaseException {
        String sql = "INSERT INTO assembly(name, hidden) values(?,?)";
        return this.insertWithIntegerAutokey(sql, assembly.getName(), assembly.isHidden() ? 1 : 0);
    }

    public boolean updateAssembly(Assembly assembly) throws DatabaseException {
        String sql = "UPDATE assembly SET name=?, hidden=? WHERE assembly_id=?";
        return this.executeUpdate(sql, assembly.getName(), assembly.isHidden() ? 1 : 0, assembly.getAssemblyId()) > 0;
    }

    public Assembly getAssembly(int assemblyId) throws DatabaseException {
        String sql = "SELECT a.assembly_id, a.name, a.hidden, au.relationship, u.account_id, u.email, u.account_enabled, u.account_type, u.username, u.phone " +
                "FROM assembly a " +
                "INNER JOIN assembly_user au on a.assembly_id = au.assembly_id " +
                "INNER JOIN account u on au.account_id = u.account_id " +
                "WHERE a.assembly_id=?"
                ;
        AssemblyPopulator populator = new AssemblyPopulator();
        this.queryWithPopulator(sql, populator, assemblyId);
        return populator.getAssembly();
    }

    public List<Assembly> getPublicAssemblies() throws DatabaseException {
        String sql = "SELECT * from assembly WHERE hidden = 0";
        return this.queryForList(sql, Assembly.class);
    }
}
