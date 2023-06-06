package org.example.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 考评指标参数表(GovernanceMetric)表实体类
 *
 * @author makejava
 * @since 2023-06-05 16:29:24
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("governance_metric")
public class GovernanceMetric  {
    //id
    private Long id;
    //指标名称
    private String metricName;
    //指标编码
    private String metricCode;
    //指标描述
    private String metricDesc;
    //治理类型
    private String governanceType;
    //指标参数
    private String metricParamsJson;
    //治理连接
    private String governanceUrl;
    //是否启用
    private String isDisabled;
}

