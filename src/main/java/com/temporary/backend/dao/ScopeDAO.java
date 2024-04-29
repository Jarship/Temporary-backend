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
}
