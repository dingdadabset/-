package org.example.spark_sql.test;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
public class BufferString implements Serializable {
    private Long totalCount;
    private HashMap<String,Long> map;

    public BufferString(Long totalCount, HashMap<String, Long> map) {
        this.totalCount = totalCount;
        this.map = map;
    }
}
