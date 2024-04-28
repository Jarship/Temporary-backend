package com.temporary.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Assembly extends BaseModel {
    private int assemblyId;
    private String name;
    private boolean hidden;

    public Assembly() {}
    public Assembly(ResultSet rs) throws SQLException {
        super(rs);
    }

    public int getAssemblyId() { return assemblyId; }

    public void setAssemblyId(int assemblyId) { this.assemblyId = assemblyId; }
    public String getName() { return name; }
    public void setName(String name){ this.name = name; }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden){ this.hidden = hidden; }

}
