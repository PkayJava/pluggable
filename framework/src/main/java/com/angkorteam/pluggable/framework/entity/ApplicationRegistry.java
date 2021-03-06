package com.angkorteam.pluggable.framework.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author Socheat KHAUV
 */
@Entity
@Table(name = "tbl_application_registry")
public class ApplicationRegistry implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2125345268932276245L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, columnDefinition = "INT")
    private Long id;
    public static final String ID = "application_registry_id";

    @Column(name = VERSION, columnDefinition = "DECIMAL(10,5)")
    private double version;
    public static final String VERSION = "version";

    @Column(name = UPGRADE_DATE, columnDefinition = "TIMESTAMP")
    private Date upgradeDate;
    public static final String UPGRADE_DATE = "upgrade_date";

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public Date getUpgradeDate() {
        return upgradeDate;
    }

    public void setUpgradeDate(Date upgradeDate) {
        this.upgradeDate = upgradeDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
