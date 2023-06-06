package org.example.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.hadoop.hive.metastore.api.Decimal;

/**
 * 治理考评结果明细(GovernanceAssessDetail)表实体类
 *
 * @author makejava
 * @since 2023-06-05 16:29:35
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("governance_assess_detail")
public class GovernanceAssessDetail  {
    //id
    private Long id;
    //考评日期
    private String assessDate;
    //表名
    private String tableName;
    //库名
    private String schemaName;
    //指标项id
    private String metricId;
    //指标项名称
    private String metricName;
    //治理类型
    private String governanceType;
    //技术负责人
    private String tecOwner;
    //考评得分
    private BigDecimal assessScore;
    //考评问题项
    private String assessProblem;
    //考评备注
    private String assessComment;
    //考评是否异常
    private String isAssessException;
    //异常信息
    private String assessExceptionMsg;
    //治理处理路径
    private String governanceUrl;
    //创建日期
    private Date createTime;
}

