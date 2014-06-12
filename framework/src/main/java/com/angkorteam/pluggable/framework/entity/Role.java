package com.angkorteam.pluggable.framework.entity;

import java.io.Serializable;

import javax.persistence.*;

/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_role")
public class Role implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1750419786618415063L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "role_id";

    @Unique
    @Column(name = NAME, columnDefinition = "VARCHAR(255)")
    private String name;
    public static final String NAME = "name";

    @Column(name = DESCRIPTION, columnDefinition = "VARCHAR(255)")
    private String description;
    public static final String DESCRIPTION = "description";

    @Column(name = DISABLE, columnDefinition = "BOOLEAN")
    private boolean disable;
    public static final String DISABLE = "disable";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
