package com.angkorteam.pluggable.framework.test;

import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class HelloRowMapper<T> implements RowMapper<T> {

    public T instance;

    public HelloRowMapper(){

            TypeVariable<? extends Class<? extends HelloRowMapper>>[] sd = this.getClass().getTypeParameters();

        TypeVariable<?> pp = sd[0];
        Class pps = (Class) pp.getGenericDeclaration();

        System.out.println(sd);


    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return  null;
    }

    public static void main(String[] args){
        HelloRowMapper<String> pp = new HelloRowMapper<String>();

    }
}
