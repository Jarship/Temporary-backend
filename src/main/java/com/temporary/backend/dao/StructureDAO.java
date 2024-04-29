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
public class StructureDAO extends BaseDAO {
    public StructureDAO() {}
    public StructureDAO(BaseDAO dao) { super(dao); }

    public class StructurePopulator implements Populator {
        private Structure structure = null;
        @Override
        public void nextResult(ResultSet rs) throws Exception {
            if (structure == null) {
                structure = new Structure(rs);
            }
            structure.getFronts().add(new Front(rs));
            structure.getScopes().add(new Scope(rs));
        }
        public Structure getStructure() { return structure; }
    }

    public class StructuresPopulator implements  Populator {
        private Map<Integer, Structure> structureMap = new HashMap<>();
        @Override
        public void nextResult(ResultSet rs) throws Exception {
            Structure structure;
            if (structureMap.containsKey(rs.getInt("structure_id"))) {
                structure = structureMap.get(rs.getInt("structure_id"));
            } else {
                structure = new Structure(rs);
                structureMap.put(structure.getStructureId(), structure);
            }
            structure.getFronts().add(new Front(rs));
            structure.getScopes().add(new Scope(rs));
        }
        public List<Structure> getStructures() {
            return new ArrayList<>(structureMap.values());
        }
    }

    public int createStructure(Structure structure) throws DatabaseException {
        String sql = "INSERT INTO structure(title) values(?)";
        return this.insertWithIntegerAutokey(sql, structure.getTitle());
    }

    public Structure getStructure(int structureId) throws DatabaseException {
        String sql = "SELECT st.structure_id, st.title, f.front_id, f.title, f.is_active, s.scope_id, s.title, s.population " +
                "FROM structure st " +
                "LEFT JOIN front_structure fs ON fs.structure_id = st.structure_id " +
                "LEFT JOIN front f ON f.front_id = fs.front_id " +
                "LEFT JOIN structure_scope ss ON  ss.structure_id = st.structure_id " +
                "LEFT JOIN scope s ON s.scope_id = ss.scope_id " +
                "WHERE st.structure_id = ?";
        StructurePopulator populator = new StructurePopulator();
        this.queryWithPopulator(sql, populator, structureId);
        return populator.getStructure();
    }

    public List<Structure> getStructures() throws DatabaseException {
        String sql = "SELECT st.structure_id, st.title, f.front_id, f.title, f.is_active, s.scope_id, s.title, s.population " +
                "FROM structure st " +
                "LEFT JOIN front_structure fs ON fs.structure_id = st.structure_id " +
                "LEFT JOIN front f ON f.front_id = fs.front_id " +
                "LEFT JOIN structure_scope ss ON  ss.structure_id = st.structure_id " +
                "LEFT JOIN scope s ON s.scope_id = ss.scope_id ";
        StructuresPopulator populator = new StructuresPopulator();
        this.queryWithPopulator(sql, populator);
        return populator.getStructures();
    }

    public boolean addStructureToFront(int structureId, int frontId) throws DatabaseException {
        String sql = "INSERT INTO front_structure (structure_id, front_id) values(?,?)";
        return this.insertWithIntegerAutokey(sql, structureId, frontId) > 0;
    }

    public boolean removeStructureFromFront(int structureId, int frontId) throws DatabaseException {
        String sql = "DELETE FROM front_structure WHERE structure_id = ? AND front_id = ?";
        return this.executeUpdate(sql, structureId, frontId) > 0;
    }
}
