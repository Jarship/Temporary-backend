package com.temporary.backend.dao;

import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Assembly;

import java.util.List;

public class AssemblyDAO extends BaseDAO {
    public AssemblyDAO() {}
    public AssemblyDAO(BaseDAO dao) { super(dao); }

    public int createAssembly(Assembly assembly) throws DatabaseException {
        String sql = "INSERT INTO assembly(name, hidden) values(?,?)";
        return this.insertWithIntegerAutokey(sql, assembly.getAssemblyName(), assembly.isHidden() ? 1 : 0);
    }

    public boolean updateAssembly(Assembly assembly) throws DatabaseException {
        String sql = "UPDATE assembly SET name=?, hidden=? WHERE assembly_id=?";
        return this.executeUpdate(sql, assembly.getAssemblyName(), assembly.isHidden() ? 1 : 0, assembly.getAssemblyId()) > 0;
    }

    public Assembly getAssembly(int assemblyId) throws DatabaseException {
        String sql = "SELECT * from assembly WHERE assembly_id=?";
        return this.queryForObject(sql, Assembly.class, assemblyId);
    }

    public List<Assembly> getPublicAssemblies() throws DatabaseException {
        String sql = "SELECT * from assembly WHERE hidden = 0";
        return this.queryForList(sql, Assembly.class);
    }
}
