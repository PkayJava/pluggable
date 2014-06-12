package com.angkorteam.pluggable.framework.entity;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_plugin_setting")
public class PluginSetting implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2125345268932276245L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "plugin_setting_id";

    @Column(name = IDENTITY, columnDefinition = "VARCHAR(255)")
    private String identity;
    public static final String IDENTITY = "identity";

    @Column(name = NAME, columnDefinition = "VARCHAR(255)")
    private String name;
    public static final String NAME = "name";

    @Column(name = VALUE, columnDefinition = "TEXT")
    private String value;
    public static final String VALUE = "value";

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

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
