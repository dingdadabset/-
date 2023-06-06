package org.example.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 治理考评类别权重表(GovernanceType)表实体类
 *
 * @author makejava
 * @since 2023-06-05 16:29:11
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("governance_type")
public class GovernanceType  {
    //id
    private Long id;
    //治理项类型编码
    private String typeCode;
    //治理项类型描述
    private String typeDesc;
    //治理类型权重
    private Double typeWeight;
}

