package com.angkorteam.pluggable.framework.test;

import javax.persistence.*;

/**
 * Created by socheat on 12/06/14.
 */
@Entity
@Table(name = "tbl_hello1")
public class Hello1 {

    @Id
    private Long id;

    @ManyToOne
    private Hello hello;
}
