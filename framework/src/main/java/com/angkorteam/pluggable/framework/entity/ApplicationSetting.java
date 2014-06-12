package com.angkorteam.pluggable.framework.entity;

import java.io.Serializable;


import javax.persistence.*;

/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_application_setting")
public class ApplicationSetting implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2125345268932276245L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "application_setting_id";

    @Unique
    @Column(name = NAME, columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;
    public static final String NAME = "name";

    @Column(name = VALUE, columnDefinition = "VARCHAR(255)")
    private String value;
    public static final String VALUE = "value";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
