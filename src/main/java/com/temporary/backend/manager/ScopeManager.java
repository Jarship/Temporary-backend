package com.temporary.backend.manager;

import com.temporary.backend.dao.BaseDAO;
import com.temporary.backend.dao.ScopeDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Scope;

import java.util.List;
public class ScopeManager extends BaseManager {
    private ScopeDAO dao;
    public ScopeManager() { this.dao = new ScopeDAO(); }
    public ScopeManager(BaseDAO dao) { this.dao = new ScopeDAO(dao); }
    public ScopeManager(BaseManager manager){ this.dao = new ScopeDAO(manager.getBaseDAO()); }

    @Override
    protected BaseDAO getBaseDAO() { return dao; }

    public Scope createScope(Scope newScope) throws ApplicationException {
        try {
            this.beginTransaction();
            int scopeId = dao.createScope(newScope);
            newScope.setScopeId(scopeId);
            if (newScope.getParentId() == null) {
                return newScope;
            } else {
                List<Integer> structureIds = dao.getStructureIdsOfScope(newScope.getParentId());
                for (Integer structureId : structureIds)
                    dao.addStructureScopeRelationship(newScope.getScopeId(), structureId);
                List<Integer> frontIds = dao.getFrontIdsOfScope(newScope.getParentId());
                for (Integer frontId : frontIds)
                    dao.addFrontScopeRelationship(frontId, newScope.getScopeId());
                List<Integer> assemblyIds = dao.getAssemblyIdsOfScope(newScope.getParentId());
                for (Integer assemblyId : assemblyIds)
                    dao.addScopeAssemblyRelationship(assemblyId, newScope.getScopeId());
                return newScope;
            }
        } catch (DatabaseException e) {
            this.rollbackTransaction();
            throw new ApplicationException(e);
        } finally {
            this.endTransaction();
        }
    }

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
