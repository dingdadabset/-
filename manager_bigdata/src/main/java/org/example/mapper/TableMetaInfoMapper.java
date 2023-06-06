package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.example.entity.TableMetaInfo;
import org.example.entity.TableMetaInfoVO;

import java.util.List;

/**
 * 元数据表(TableMetaInfo)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-05 14:56:35
 */
public interface TableMetaInfoMapper extends BaseMapper<TableMetaInfo> {
    @Select("${SQL}}")
    List<TableMetaInfoVO> getTableMetaInfoList(@Param("SQL") String sql);
    @Select("${SQL}")
    Integer getTableMetaInfoCount(@Param("SQL") String sql);

    @Select(" select   te.id te_id,  te.create_time as   te_create_time,    tm.*,te.*  from table_meta_info tm join table_meta_info_extra  te on tm.table_name =te.table_name and tm.schema_name=te.schema_name\n" +
            "     where   assess_date = (select  max(tm1.assess_date) from table_meta_info  tm1 group by tm1.table_name,tm1.schema_name having tm.schema_name=tm1.schema_name and tm.table_name=tm1.table_name)\n")
    @ResultMap("tableMetaInfoMap")
    public List<TableMetaInfo> getTableMetaInfoListLastDt();

}

