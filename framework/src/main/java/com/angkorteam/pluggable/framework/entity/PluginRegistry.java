package com.angkorteam.pluggable.framework.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_plugin_registry")
public class PluginRegistry implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6506550583124979815L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "plugin_registry_id";

    @Column(name = NAME, columnDefinition = "VARCHAR(255)")
    private String name;
    public static final String NAME = "name";

    @Column(name = VERSION, columnDefinition = "DECIMAL(10,5)")
    private double version;
    public static final String VERSION = "version";

    @Column(name = IDENTITY, columnDefinition = "VARCHAR(255)")
    private String identity;
    public static final String IDENTITY = "identity";

    @Column(name = UPGRADE_DATE, columnDefinition = "TIMESTAMP")
    private Date upgradeDate;
    public static final String UPGRADE_DATE = "upgrade_date";

    @Column(name = ACTIVATED, columnDefinition = "BOOLEAN")
    private boolean activated;
    public static final String ACTIVATED = "activated";

    @Column(name = PRESENTED, columnDefinition = "BOOLEAN")
    private boolean presented;
    public static final String PRESENTED = "presented";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Date getUpgradeDate() {
        return upgradeDate;
    }

    public void setUpgradeDate(Date upgradeDate) {
        this.upgradeDate = upgradeDate;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isPresented() {
        return presented;
    }

    public void setPresented(boolean presented) {
        this.presented = presented;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
