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
public class Front extends BaseModel {
    private int frontId;
    private String title;
    private boolean isActive;
    private List<Scope> scopes = new ArrayList<>();
    private List<Structure> structures = new ArrayList<>();

    public Front () {}
    public Front(ResultSet rs) throws SQLException {
        super(rs);
    }
    public int getFrontId() {
        return frontId;
    }
    public void setFrontId(int frontId) {
        this.frontId = frontId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
    public List<Scope> getScopes() {
        return scopes;
    }
    public List<Structure> getStructures() {
        return structures;
    }
}
