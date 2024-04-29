package com.temporary.backend.dao;

import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Front;
import com.temporary.backend.model.Scope;
import com.temporary.backend.model.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;
public class FrontDAO extends BaseDAO {
    public FrontDAO() {}
    public FrontDAO(BaseDAO dao) { super(dao); }

    public class FrontPopulator implements Populator {
        private Front front = null;
        @Override
        public void nextResult(ResultSet rs) throws Exception {
            if (front == null) {
                front = new Front(rs);
            }
            front.getScopes().add(new Scope(rs));
            front.getStructures().add(new Structure(rs));
        }
        public Front getFront() {
            return front;
        }
    }

    public class FrontsPopulator implements Populator {
        private Map<Integer, Front> frontMap = new HashMap();

        @Override
        public void nextResult(ResultSet rs) throws Exception {
            int frontId = rs.getInt("front_id");
            Front front;
            if (frontMap.containsKey(frontId))
            {
                front = frontMap.get(frontId);
            } else {
                front = new Front(rs);
                frontMap.put(frontId, front);
            }
            front.getScopes().add(new Scope(rs));
            front.getStructures().add(new Structure(rs));
        }

        public List<Front> getFronts() {
            return new ArrayList<>(frontMap.values());
        }
    }

    public int createFront(Front front) throws DatabaseException {
        String sql = "INSERT INTO front(title, is_active) values(?,?)";
        return this.insertWithIntegerAutokey(sql, front.getTitle(), front.isActive() ? 1 : 0);
    }

    public boolean endFront (int frontId) throws DatabaseException {
        String sql = "UPDATE front SET is_active=0 WHERE front_id = ?";
        return this.executeUpdate(sql, frontId) > 0;
    }

    public Front getFront(int frontId) throws DatabaseException {
        String sql = "SELECT f.front_id, f.title, f.is_active, s.scope_id, s.title, s.population, st.structure_id, st.title " +
                "FROM front f " +
                "LEFT JOIN front_scope fs ON f.front_id = fs.front_id " +
                "LEFT JOIN scope s ON fs.scope_id = s.scope_id " +
                "LEFT JOIN front_structure fst ON f.front_id = fst.front_id " +
                "LEFT JOIN structure st ON fst.structure_id = st.structure_id " +
                " WHERE f.front_id = ?";
        FrontPopulator populator = new FrontPopulator();
        this.queryWithPopulator(sql, populator, frontId);
        return populator.getFront();
    }

    public List<Front> getActiveFronts() throws DatabaseException {
        String sql = "SELECT f.front_id, f.title, f.is_active, s.scope_id, s.title, s.population, st.structure_id, st.title " +
                "FROM front f " +
                "LEFT JOIN front_scope fs ON f.front_id = fs.front_id " +
                "LEFT JOIN scope s ON fs.scope_id = s.scope_id " +
                "LEFT JOIN front_structure fst ON f.front_id = fst.front_id " +
                "LEFT JOIN structure st ON fst.structure_id = st.structure_id " +
                " WHERE f.is_active = 1";
        FrontsPopulator populator = new FrontsPopulator();
        this.queryWithPopulator(sql, populator);
        return populator.getFronts();
    }

}
