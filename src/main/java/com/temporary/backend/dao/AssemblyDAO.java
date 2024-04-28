package com.temporary.backend.dao;

import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Account;
import com.temporary.backend.model.Assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;

public class AssemblyDAO extends BaseDAO {

    public class AssemblyPopulator implements Populator {
        Assembly a = null;
        @Override
        public void nextResult(ResultSet rs) throws Exception {
            if (a == null) {
                a = new Assembly(rs);
            }
            String relationship = rs.getString("relationship");
            if (!rs.wasNull() && relationship.length() > 0) {
                if (relationship.equals("FOLLOW")) {
                    a.getFollowers().add(new Account(rs));
                }
            }

        }
        public Assembly getAssembly() { return a; }
    }

    public class AssembliesPopulator implements Populator {
        Map<Integer, Assembly> assemblyMap = new HashMap<>();
        @Override
        public void nextResult(ResultSet rs) throws Exception {
            Integer assemblyId = rs.getInt("assembly_id");
            Assembly assembly;
            if (assemblyMap.containsKey(assemblyId)) {
                assembly = assemblyMap.get(assemblyId);
            } else {
                assembly = new Assembly(rs);
                assemblyMap.put(assemblyId, assembly);
            }
            String relationship = rs.getString("relationship");
            if (!rs.wasNull() && relationship.length() > 0) {
                if (relationship.equals("FOLLOW")) {
                    assembly.getFollowers().add(new Account(rs));
                }
            }
        }
        public List<Assembly> getAssemblies() {
            return new ArrayList<>(assemblyMap.values());
        }
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
                "LEFT JOIN assembly_user au on a.assembly_id = au.assembly_id " +
                "LEFT JOIN account u on au.account_id = u.account_id " +
                "WHERE a.assembly_id=?"
                ;
        AssemblyPopulator populator = new AssemblyPopulator();
        this.queryWithPopulator(sql, populator, assemblyId);
        return populator.getAssembly();
    }

    public List<Assembly> getUserAssemblies(int accountId) throws DatabaseException {
        String sql = "SELECT a.assembly_id, a.name, a.hidden FROM assembly a " +
                "INNER JOIN assembly_user au on a.assembly_id = au.assembly_id " +
                "WHERE au.account_id = ? AND au.relationship != \"NONE\" AND au.relationship != \"IGNORE\"";
        return this.queryForList(sql, Assembly.class, accountId);
    }

    public List<Assembly> getPublicAssemblies() throws DatabaseException {
        String sql = "SELECT * from assembly WHERE hidden = 0";
        return this.queryForList(sql, Assembly.class);
    }
}
