package com.temporary.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.checker.units.qual.A;

import java.sql.ResultSet;
import java.sql.SQLException;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scope extends BaseModel {
    private int scopeId;
    private Integer parentId;
    private Scope parent;
    private String title;
    private long population;
    public Scope() {}

    public Scope(ResultSet rs) throws SQLException {
        super(rs);
    }

    public int getScopeId() { return scopeId; }
    public void setScopeId(int scopeId) { this.scopeId = scopeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public long getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public Scope getParent() { return parent; }
    public void setParent(Scope parent) { this.parent = parent; }
}
