package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.TableMetaInfo;
import org.example.entity.TableMetaInfoForQuery;
import org.example.entity.TableMetaInfoVO;

import java.util.List;

/**
 * 元数据表(TableMetaInfo)表服务接口
 *
 * @author makejava
 * @since 2023-06-05 14:56:36
 */
public interface TableMetaInfoService extends IService<TableMetaInfo> {
    public TableMetaInfo getTableMetaInfoFromHive(String databaseName, String tableName);

    public   void   initTableMeta(String databaseName);

    void addHdfsInfo(TableMetaInfo tableMetaInfo);

    List<TableMetaInfoVO> getTableMetaInfoList(TableMetaInfoForQuery tableMetaInfoForQuery);

    Integer getTableMetaInfoCount(TableMetaInfoForQuery tableMetaInfoForQuery);

    TableMetaInfo getTableMetaInfoAll(Long valueOf);
    public List<TableMetaInfo>  getTableMetaInfoList();
}

