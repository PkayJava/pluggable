package com.angkorteam.pluggable.framework.entity;

import java.io.Serializable;

import com.angkorteam.pluggable.framework.database.annotation.Column;
import com.angkorteam.pluggable.framework.database.annotation.Entity;
import com.angkorteam.pluggable.framework.database.annotation.GeneratedValue;
import com.angkorteam.pluggable.framework.database.annotation.GenerationType;
import com.angkorteam.pluggable.framework.database.annotation.Id;
import com.angkorteam.pluggable.framework.database.annotation.Table;
import com.angkorteam.pluggable.framework.database.annotation.Unique;

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

}
