package com.temporary.backend.manager;

import com.temporary.backend.dao.BaseDAO;
import com.temporary.backend.dao.FrontDAO;
import com.temporary.backend.dao.ScopeDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Front;
import com.temporary.backend.model.Scope;
public class FrontManager extends BaseManager {
    private FrontDAO dao;
    public FrontManager() { this.dao = new FrontDAO(); }
    public FrontManager(BaseDAO dao){ this.dao = new FrontDAO(dao); }
    public FrontManager(BaseManager manager) { this.dao = new FrontDAO(manager.getBaseDAO()); }

    @Override
    protected BaseDAO getBaseDAO() { return dao; }

    public Front createFront(Front front) throws ApplicationException {
        try {
            this.beginTransaction();
            int frontId = dao.createFront(front);
            front.setFrontId(frontId);
            ScopeDAO scopeDao = new ScopeDAO(dao);
            for (Scope scope: front.getScopes()) {
                scopeDao.addFrontScopeRelationship(frontId, scope.getScopeId());
            }
            return front;
        } catch(DatabaseException e) {
            this.rollbackTransaction();
            throw new ApplicationException(e);
        } finally {
            this.endTransaction();
        }
    }
}
