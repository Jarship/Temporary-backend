package com.temporary.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.checker.units.qual.A;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Structure extends BaseModel {
    private int structureId;
    private String title;
    private List<Scope> scopes = new ArrayList<>();
    private List<Front> fronts = new ArrayList<>();

    public Structure() {}
    public Structure(ResultSet rs ) throws SQLException {
        super(rs);
    }

    public int getStructureId() { return structureId; }
    public void setStructureId(int structureId) { this.structureId = structureId; }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Scope> getScopes() { return scopes; }

    public List<Front> getFronts() { return fronts; }
}
