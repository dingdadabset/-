package org.example.spark_sql.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    public String name;
    public Long age;

    public User() {
    }

    public User(Long age, String name) {
        this.age = age;
        this.name = name;
    }

}
