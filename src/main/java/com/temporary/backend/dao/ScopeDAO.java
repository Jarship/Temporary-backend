package com.temporary.backend.dao;

import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Scope;

import java.util.List;

public class ScopeDAO extends BaseDAO {
    public ScopeDAO() {}
    public ScopeDAO(BaseDAO baseDAO) { super(baseDAO); }

    public int createScope(Scope scope) throws DatabaseException {
        String sql = "INSERT INTO scope(title, population, parent_id) values(?,?, ?)";
        return insertWithIntegerAutokey(sql, scope.getTitle(), scope.getPopulation(), scope.getParentId());
    }
    public boolean updateScope(Scope scope) throws DatabaseException {
        String sql = "UPDATE scope SET population=?, title=? WHERE scope_id=?";
        return this.executeUpdate(sql, scope.getPopulation(), scope.getTitle(), scope.getScopeId()) > 0;
    }
    public Scope getScope(int scopeId) throws DatabaseException {
        String sql = "SELECT scope_id, title, population, parent_id FROM scope WHERE scope_id = ?";
        return queryForObject(sql, Scope.class, scopeId);
    }

    public List<Scope> getScopesOfParent(int parentId) throws DatabaseException {
        String sql = "SELECT scope_id, title, population, parent_id FROM scope WHERE parent_id = ?";
        return queryForList(sql, Scope.class, parentId);
    }

    public List<Scope> getScopesWithoutParent() throws DatabaseException {
        String sql = "SELECT scope_id, title, population, parent_id FROM scope WHERE parent_id IS NULL";
        return queryForList(sql, Scope.class);
    }

    public void addStructureScopeRelationship(int scopeId, int structureId) throws DatabaseException {
        String sql = "INSERT INTO structure_scope(scope_id, structure_id) values(?,?)";
        this.insertWithIntegerAutokey(sql, scopeId, structureId);
    }

    public List<Integer> getStructureIdsOfScope(int scopeId) throws DatabaseException {
        String sql = "SELECT structure_id FROM structure_scope WHERE scope_id = ?";
        return queryForList(sql, Integer.class, scopeId);
    }

    public void addFrontScopeRelationship(int frontId, int scopeId) throws DatabaseException {
        String sql = "INSERT INTO front_structure(front_id, scope_id) values(?,?)";
        this.insertWithIntegerAutokey(sql, frontId, scopeId);
    }

    public List<Integer> getFrontIdsOfScope(int scopeId) throws DatabaseException {
        String sql = "SELECT front_id FROM front_scope WHERE scope_id = ?";
        return queryForList(sql, Integer.class, scopeId);
    }

    public void addScopeAssemblyRelationship(int assemblyId, int scopeId) throws DatabaseException {
        String sql = "INSERT INTO scope_assembly(assembly_id, scope_id) values(?,?)";
        this.insertWithIntegerAutokey(sql, assemblyId, scopeId);
    }

    public List<Integer> getAssemblyIdsOfScope(int scopeId) throws DatabaseException {
        String sql = "SELECT assembly_id FROM scope_assembly WHERE scope_id = ?";
        return queryForList(sql, Integer.class, scopeId);
    }
}
