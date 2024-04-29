package com.temporary.backend.manager;

import com.temporary.backend.dao.BaseDAO;
import com.temporary.backend.dao.ScopeDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.model.Scope;

import java.util.List;
public class ScopeManager extends BaseManager {
    private ScopeDAO dao;
    public ScopeManager() { this.dao = new ScopeDAO(); }
    public ScopeManager(BaseDAO dao) { this.dao = new ScopeDAO(dao); }
    public ScopeManager(BaseManager manager){ this.dao = new ScopeDAO(manager.getBaseDAO()); }

    @Override
    protected BaseDAO getBaseDAO() { return dao; }

    public Scope getScopeWithParents(int scopeId) throws ApplicationException {
        Scope baseScope = dao.getScope(scopeId);
        Scope pointer = baseScope;
        while (pointer.getParentId() != null) {
            Scope parent = dao.getScope(pointer.getScopeId());
            pointer.setParent(parent);
            pointer = parent;
        }
        return baseScope;
    }

}
