package org.example.spark_sql.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Buffer implements Serializable {
    private Long sum;
    private Long count;

    public Buffer() {
    }

    public Buffer(Long sum, Long count) {
        this.sum = sum;
        this.count = count;
    }
}
