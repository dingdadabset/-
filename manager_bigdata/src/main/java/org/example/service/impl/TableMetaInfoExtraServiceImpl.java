package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.conf.MetaConst;
import org.example.entity.TableMetaInfo;
import org.example.entity.TableMetaInfoForQuery;
import org.example.entity.TableMetaInfoVO;
import org.example.mapper.TableMetaInfoExtraMapper;
import org.example.entity.TableMetaInfoExtra;
import org.example.service.TableMetaInfoExtraService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 元数据表附加信息(TableMetaInfoExtra)表服务实现类
 *
 * @author makejava
 * @since 2023-06-05 15:30:39
 */
@Service("tableMetaInfoExtraService")
public class TableMetaInfoExtraServiceImpl extends ServiceImpl<TableMetaInfoExtraMapper, TableMetaInfoExtra> implements TableMetaInfoExtraService {
    /**
     * 根据元数据表列表生成
     * @param tableMetaInfoExtra
     */
    public  void genExtraListByMetaList(List<TableMetaInfo> tableMetaInfoList ){

        List<TableMetaInfoExtra> tableMetaInfoExtraList=new ArrayList<>(tableMetaInfoList.size());

        for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
            //如果有则不必生成
            TableMetaInfoExtra tableMetaInfoExtra =  getOne(new QueryWrapper<TableMetaInfoExtra>().eq("schema_name", tableMetaInfo.getSchemaName()).eq("table_name", tableMetaInfo.getTableName()));
            if(tableMetaInfoExtra==null){
                tableMetaInfoExtra= TableMetaInfoExtra.builder()
                .tableName(tableMetaInfo.getTableName())
                .schemaName(tableMetaInfo.getSchemaName())
                .dwLevel(getInitDwLevelByTableName(tableMetaInfo.getTableName()))
                .lifecycleType(MetaConst.LIFECYCLE_TYPE_UNSET)
                .securityLevel(MetaConst.SECURITY_LEVEL_UNSET)
                .createTime(new Date()).build();
                tableMetaInfoExtraList.add(tableMetaInfoExtra);
            }
        }
        saveBatch(tableMetaInfoExtraList);
    }

    @Override
    public void saveTableMetaInfoExtra(TableMetaInfoExtra tableMetaInfoExtra) {
        tableMetaInfoExtra.setUpdateTime(new Date());
        QueryWrapper<TableMetaInfoExtra> updateQueryWrapper = new QueryWrapper<TableMetaInfoExtra>().eq("schema_name", tableMetaInfoExtra.getSchemaName())
                .eq("table_name", tableMetaInfoExtra.getTableName());
        update(tableMetaInfoExtra,updateQueryWrapper);

    }


    private String getInitDwLevelByTableName(String tableName){
        if(tableName.startsWith("ods")){
            return "ODS";
        } else if (tableName.startsWith("dwd")) {
            return "DWD";
        }else if (tableName.startsWith("dim")) {
            return "DIM";
        }else if (tableName.startsWith("dws")) {
            return "DWS";
        }else if (tableName.startsWith("ads")) {
            return "ADS";
        }else if (tableName.startsWith("dm")) {
            return "DM";
        }else  {
            return "OTHER";
        }
    }

}

