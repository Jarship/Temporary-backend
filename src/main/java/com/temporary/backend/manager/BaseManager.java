package com.temporary.backend.manager;

import com.temporary.backend.dao.BaseDAO;
import com.temporary.backend.exception.DatabaseException;

import java.util.logging.Logger;

public abstract class BaseManager {

    protected Logger log = Logger.getLogger(BaseManager.class.getSimpleName());
    protected abstract BaseDAO getBaseDAO();

    protected void beginTransaction() throws DatabaseException {
        BaseDAO dao = getBaseDAO();
        if (dao != null) dao.beginTransaction();
    }

    protected void rollbackTransaction() {
        BaseDAO dao = getBaseDAO();
        if (dao != null) dao.rollbackTransaction();
    }

    protected void endTransaction() {
        BaseDAO dao = getBaseDAO();
        if (dao != null) dao.endTransaction();
    }
}
