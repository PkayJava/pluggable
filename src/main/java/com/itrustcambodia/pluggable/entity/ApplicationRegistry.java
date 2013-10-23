package com.itrustcambodia.pluggable.entity;

import java.io.Serializable;
import java.util.Date;

import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.Entity;
import com.itrustcambodia.pluggable.database.annotation.GeneratedValue;
import com.itrustcambodia.pluggable.database.annotation.GenerationType;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Table;

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

}
