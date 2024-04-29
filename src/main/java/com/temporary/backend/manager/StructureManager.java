package com.temporary.backend.manager;

import com.temporary.backend.dao.BaseDAO;
import com.temporary.backend.dao.ScopeDAO;
import com.temporary.backend.dao.StructureDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Front;
import com.temporary.backend.model.Scope;
import com.temporary.backend.model.Structure;

import java.util.List;
public class StructureManager extends BaseManager {
    private StructureDAO dao;
    public StructureManager() { this.dao = new StructureDAO(); }
    public StructureManager(BaseDAO dao) { this.dao = new StructureDAO(dao); }
    public StructureManager(BaseManager manager) { this.dao = new StructureDAO(manager.getBaseDAO()); }

    @Override
    protected BaseDAO getBaseDAO() { return dao; }

    public Structure createStructure(Structure structure) throws ApplicationException {
        try {
            this.beginTransaction();
            int structureId = dao.createStructure(structure);
            structure.setStructureId(structureId);
            ScopeDAO scopeDao = new ScopeDAO(dao);
            for (Scope scope: structure.getScopes()) {
                scopeDao.addStructureScopeRelationship(scope.getScopeId(), structureId);
            }
            for (Front front: structure.getFronts()) {
                dao.addStructureToFront(structureId, front.getFrontId());
            }
            return structure;
        } catch (DatabaseException e) {
            this.rollbackTransaction();
            throw new ApplicationException(e);
        } finally {
            this.endTransaction();
        }
    }
}
