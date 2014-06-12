package com.angkorteam.pluggable.framework.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "tbl_job")
public class Job implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -485206283510789022L;

    @Id
    @Column(name = ID, columnDefinition = "VARCHAR(255)")
    private String id;
    public static final String ID = "job_id";

    @Column(name = DESCRIPTION, columnDefinition = "VARCHAR(255)")
    private String description;
    public static final String DESCRIPTION = "description";

    @Column(name = CRON, columnDefinition = "VARCHAR(255)")
    private String cron;
    public static final String CRON = "cron";

    @Column(name = NEW_CRON, columnDefinition = "VARCHAR(255)")
    private String newCron;
    public static final String NEW_CRON = "new_cron";

    @Column(name = STATUS, columnDefinition = "VARCHAR(10)")
    private String status;
    public static final String STATUS = "status";

    @Column(name = DISABLE, columnDefinition = "BOOLEAN")
    private boolean disable = false;
    public static final String DISABLE = "disable";

    @Column(name = PAUSE, columnDefinition = "BOOLEAN")
    private boolean pause = false;
    public static final String PAUSE = "pause";

    @Column(name = LAST_PROCESS, columnDefinition = "TIMESTAMP")
    private Date lastProcess;
    public static final String LAST_PROCESS = "last_process";

    @Column(name = LAST_ERROR, columnDefinition = "TEXT")
    private String lastError;
    public static final String LAST_ERROR = "last_error";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public Date getLastProcess() {
        return lastProcess;
    }

    public String getNewCron() {
        return newCron;
    }

    public void setNewCron(String newCron) {
        this.newCron = newCron;
    }

    public void setLastProcess(Date lastProcess) {
        this.lastProcess = lastProcess;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public static abstract class Status {
        public static final String BUSY = "BUSY";
        public static final String IDLE = "IDLE";
    }

}
