package org.example.entity;

import lombok.Data;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;

import java.util.List;
import java.util.Map;

@Data
public class AssessParam {


      GovernanceMetric governanceMetric;

      TableMetaInfo tableMetaInfo;

      String assessDate;

      Map<String,TableMetaInfo> tableMetaInfoMap;



}
