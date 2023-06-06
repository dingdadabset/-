package org.example.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 元数据表附加信息(TableMetaInfoExtra)表实体类
 *
 * @author makejava
 * @since 2023-06-05 15:30:39
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("table_meta_info_extra")
@Builder
public class TableMetaInfoExtra  {
    //表id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    //表名
    private String tableName;
    //库名
    private String schemaName;
    //技术负责人   
    private String tecOwnerUserName;
    //业务负责人 
    private String busiOwnerUserName;
    //存储周期类型(FOREVER、NORMAL、UNSET)
    private String lifecycleType;
    //生命周期(天) 
    private Long lifecycleDays;
    //安全级别 (UNSET、PUBLIC、INSIDE、INSIDE_LIMIT、PROTECT)
    private String securityLevel;
    //数仓所在层级(ODSDWDDIMDWSADS) ( 来源: 附加)
    private String dwLevel;
    //创建时间 (自动生成)
    private Date createTime;
    //更新时间  (自动生成)
    private Date updateTime;
}

