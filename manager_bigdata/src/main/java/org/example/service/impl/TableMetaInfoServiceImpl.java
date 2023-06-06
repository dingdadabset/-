package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.RetryingMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.example.entity.TableMetaInfoExtra;
import org.example.entity.TableMetaInfoForQuery;
import org.example.entity.TableMetaInfoVO;
import org.example.mapper.TableMetaInfoExtraMapper;
import org.example.mapper.TableMetaInfoMapper;
import org.example.entity.TableMetaInfo;
import org.example.service.TableMetaInfoExtraService;
import org.example.service.TableMetaInfoService;
import org.example.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 元数据表(TableMetaInfo)表服务实现类
 *
 * @author makejava
 * @since 2023-06-05 14:56:36
 */
@Service("tableMetaInfoService")
public class TableMetaInfoServiceImpl extends ServiceImpl<TableMetaInfoMapper, TableMetaInfo> implements TableMetaInfoService {
    IMetaStoreClient hiveClient = getHiveClient();

    // 初始化 hive 客户端
    private IMetaStoreClient getHiveClient() {
        HiveConf hiveConf = new HiveConf();

        hiveConf.addResource(Thread.currentThread().getContextClassLoader().
                getResourceAsStream("hive-site.xml"));

        IMetaStoreClient client = null;
        try {
            client = RetryingMetaStoreClient.getProxy(hiveConf, true);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        return client;
    }

    public List<String> getTableNameList(String databaseName) {
        List<String> allTables = null;
        try {
            allTables = hiveClient.getAllTables(databaseName);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
        return allTables;
    }


    @Override
    public TableMetaInfo getTableMetaInfoFromHive(String databaseName, String tableName) {
        TableMetaInfo tableMetaInfo = new TableMetaInfo();
        try {
            Table table = hiveClient.getTable(databaseName, tableName);
            tableMetaInfo.setTableName(tableName);
            tableMetaInfo.setSchemaName(databaseName);
            //列信息 ： 内容弹性变化大 所以放在一个json里
            List<FieldSchema> fieldSchemaList = table.getSd().getCols();
            PropertyPreFilters.MySimplePropertyPreFilter filter = new PropertyPreFilters().addFilter("comment", "name", "type");
            String colNameJson = JSON.toJSONString(fieldSchemaList, filter);
            tableMetaInfo.setColNameJson(colNameJson);
            //表类型  external_table  \ managed_table
            tableMetaInfo.setTableType(table.getTableType());
            //获得表备注
            Map<String, String> parameters = table.getParameters();//获取表参数（备注、建表时间）
            tableMetaInfo.setTableComment(parameters.get("comment"));

            //表目录所有者
            tableMetaInfo.setTableFsOwner(table.getOwner());

            //表存储路径
            tableMetaInfo.setTableFsPath(table.getSd().getLocation());

            //表输入格式
            tableMetaInfo.setTableInputFormat(table.getSd().getInputFormat());
            //表输出格式
            tableMetaInfo.setTableOutputFormat(table.getSd().getInputFormat());

            //序列化信息
            tableMetaInfo.setTableRowFormatSerde(table.getSd().getSerdeInfo().getSerializationLib());

            //表创建时间
            Date tableCreateDate = new Date(table.getCreateTime() * 1000L);
            tableMetaInfo.setTableCreateTime(tableCreateDate.toString());

            //表分区字段
            List<FieldSchema> partitionKeys = table.getPartitionKeys();
            String partitionColJson = JSON.toJSONString(partitionKeys, filter);
            tableMetaInfo.setPartitionColNameJson(partitionColJson);


            //表分桶
            tableMetaInfo.setTableBucketNum(table.getSd().getNumBuckets() + 0L);
            if (tableMetaInfo.getTableBucketNum() > 0) {
                List<String> bucketCols = table.getSd().getBucketCols();
                tableMetaInfo.setTableBucketColsJson(JSON.toJSONString(bucketCols));
                tableMetaInfo.setTableSortColsJson(JSON.toJSONString(table.getSd().getSortCols()));
            }

            // 其他表辅助信息
            tableMetaInfo.setTableParametersJson(JSON.toJSONString(table.getParameters()));
        } catch (TException e) {
            throw new RuntimeException(e);
        }

        return tableMetaInfo;
    }

    @Resource
    private TableMetaInfoExtraServiceImpl tableMetaInfoExtraMapper;

    @Override
    public void initTableMeta(String databaseName) {
        List<String> tableNameList = getTableNameList(databaseName);
        List<TableMetaInfo> tableMetaInfoList = new ArrayList<>(tableNameList.size());


        Date curDateTime = new Date();
        String curDt = DateFormatUtils.format(curDateTime, "yyyy-MM-dd");
        //2  循环每张表提取元数据信息
        for (String tableName : tableNameList) {
            //2.1 查询每张表的hive元数据信息
            TableMetaInfo tableMetaInfo = getTableMetaInfoFromHive(databaseName, tableName);
            //2.2 补充每张表的hdfs文件信息信息
            addHdfsInfo(tableMetaInfo);
            tableMetaInfo.setCreateTime(curDateTime);
            tableMetaInfo.setAssessDate(curDt);
            tableMetaInfoList.add(tableMetaInfo);
        }
        //3  写入数据库
        remove(new QueryWrapper<TableMetaInfo>().eq("assess_date", curDt));
        saveOrUpdateBatch(tableMetaInfoList);
        tableMetaInfoExtraMapper.genExtraListByMetaList(tableMetaInfoList);
    }

    @Override
    public void addHdfsInfo(TableMetaInfo tableMetaInfo) {
        String tableFsPath = tableMetaInfo.getTableFsPath();
        String tableFsOwner = tableMetaInfo.getTableFsOwner();

        try {
            FileSystem fs = FileSystem.get(new URI(tableFsPath), new Configuration(), tableFsOwner);

            boolean exists = fs.exists(new Path(tableFsPath));

            if (exists) {
                //求文件大小：  求出所有文件的列表，然后把所有文件的大小汇总相加
                // FileStatus 是一个文件的元数据信息
                List<FileStatus> fileStatusList = new ArrayList<>();
                FileStatus[] fileStatuses = fs.listStatus(new Path(tableFsPath));
                System.out.println("tableMetaInfo.getTableName() = " + tableMetaInfo.getTableName());
                addFileInfo(fileStatuses, tableMetaInfo, fs);

                tableMetaInfo.setFsCapcitySize(fs.getStatus().getCapacity());
                tableMetaInfo.setFsUsedSize(fs.getStatus().getUsed());
                tableMetaInfo.setFsRemainSize(fs.getStatus().getRemaining());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<TableMetaInfoVO> getTableMetaInfoList(TableMetaInfoForQuery tableMetaInfoForQuery) {
        Integer pageNo = tableMetaInfoForQuery.getPageNo();
        Integer pageSize = tableMetaInfoForQuery.getPageSize();
        Integer limitFrom=(pageNo-1)*pageSize;
        StringBuilder sqlBd=new StringBuilder();
        sqlBd.append(" select  tm.id ,tm.table_name,tm.schema_name,table_comment,table_size,table_total_size,tec_owner_user_name,busi_owner_user_name, table_last_access_time,table_last_modify_time");
        sqlBd.append(" from table_meta_info tm  join table_meta_info_extra te on tm.table_name=te.table_name and tm.schema_name=te.schema_name");
        sqlBd.append(" where 1=1 ");
        if(tableMetaInfoForQuery.getSchemaName() != null&&tableMetaInfoForQuery.getSchemaName().length()>0){
            sqlBd.append(" and schema_name like '%"+ SqlUtil.filterUnsafeSql( tableMetaInfoForQuery.getSchemaName())+"'");
        }
        if(tableMetaInfoForQuery.getTableName() != null&&tableMetaInfoForQuery.getTableName().length()>0){
            sqlBd.append(" and table_name like '%"+ SqlUtil.filterUnsafeSql( tableMetaInfoForQuery.getTableName())+"'");
        }
        if(tableMetaInfoForQuery.getDwLevel() != null&&tableMetaInfoForQuery.getDwLevel().length()>0){
            sqlBd.append(" and dw_level like '%"+SqlUtil.filterUnsafeSql( tableMetaInfoForQuery.getDwLevel())+"'");
        }
        sqlBd.append(" and assess_date = (select  max(tm1.assess_date) from table_meta_info  tm1 group by tm1.table_name,tm1.schema_name having tm.schema_name=tm1.schema_name and tm.table_name=tm1.table_name)  ");
        sqlBd.append(" limit  "+ limitFrom+","+pageSize);

        List<TableMetaInfoVO> tableMetaInfoList = baseMapper.getTableMetaInfoList(sqlBd.toString());

        return tableMetaInfoList;

    }

    @Override
    public Integer getTableMetaInfoCount(TableMetaInfoForQuery tableMetaInfoForQuery) {
        Integer pageNo = tableMetaInfoForQuery.getPageNo();
        Integer pageSize = tableMetaInfoForQuery.getPageSize();
        Integer limitFrom=(pageNo-1)*pageSize;
        StringBuilder sqlBd=new StringBuilder();
        sqlBd.append(" select count(*)");
        sqlBd.append(" from table_meta_info tm  join table_meta_info_extra te on tm.table_name=te.table_name and tm.schema_name=te.schema_name");
        sqlBd.append(" where 1=1 ");
        //组合条件
        if(tableMetaInfoForQuery.getSchemaName() != null&&tableMetaInfoForQuery.getSchemaName().length()>0){
            sqlBd.append(" and schema_name like '%"+SqlUtil.filterUnsafeSql( tableMetaInfoForQuery.getSchemaName())+"'");
        }
        if(tableMetaInfoForQuery.getTableName() != null&&tableMetaInfoForQuery.getTableName().length()>0){
            sqlBd.append(" and table_name like '%"+SqlUtil.filterUnsafeSql(tableMetaInfoForQuery.getTableName())+"'");
        }
        if(tableMetaInfoForQuery.getDwLevel() != null&&tableMetaInfoForQuery.getDwLevel().length()>0){
            sqlBd.append(" and dw_level like '%"+SqlUtil.filterUnsafeSql(tableMetaInfoForQuery.getDwLevel())+"'");
        }
        sqlBd.append(" and assess_date = (select  max(tm1.assess_date) from table_meta_info  tm1 group by tm1.table_name,tm1.schema_name having tm.schema_name=tm1.schema_name and tm.table_name=tm1.table_name)  ");
        sqlBd.append(" limit  "+ limitFrom+","+pageSize);

        Integer total = baseMapper.getTableMetaInfoCount(sqlBd.toString());

        return total;

    }

    @Override
    public TableMetaInfo getTableMetaInfoAll(Long valueOf) {
        TableMetaInfo tableMetaInfo = getById(valueOf);
        //通过库名和表名查询辅助信息
        TableMetaInfoExtra tableMetaInfoExtra = tableMetaInfoExtraMapper.getOne(new QueryWrapper<TableMetaInfoExtra>().eq("schema_name", tableMetaInfo.getSchemaName()).eq("table_name", tableMetaInfo.getTableName()));
        if(tableMetaInfoExtra==null){
            tableMetaInfoExtra=new TableMetaInfoExtra();
        }
        tableMetaInfo.setTableMetaInfoExtra(tableMetaInfoExtra);
        return tableMetaInfo;


    }

    @Override
    public List<TableMetaInfo> getTableMetaInfoList() {

        List<TableMetaInfo> tableMetaInfoList = baseMapper.getTableMetaInfoListLastDt();
        return tableMetaInfoList;

    }

    private void addFileInfo(FileStatus[] curPathFileStatuses, TableMetaInfo tableMetaInfo, FileSystem fs) {
        try {
            for (FileStatus curPathFileStatus : curPathFileStatuses) {
                if (curPathFileStatus.isDirectory()) {
                    FileStatus[] fileStatuses = fs.listStatus(curPathFileStatus.getPath());
                    // 因为可能会有多级目录所以要利用递归完成
                    addFileInfo(fileStatuses, tableMetaInfo, fs);
                } else {
                    //把文件大小加到表大小
                    long fileSize = curPathFileStatus.getLen();
                    Long tableSize = tableMetaInfo.getTableSize();
                    tableMetaInfo.setTableSize(tableSize + fileSize);
                    Long tableTotalSize = tableMetaInfo.getTableTotalSize();
                    long fileTotalSize = curPathFileStatus.getLen() * curPathFileStatus.getReplication();
                    tableMetaInfo.setTableTotalSize(tableTotalSize + fileTotalSize);
                    //最后访问时间
                    if (tableMetaInfo.getTableLastAccessTime() != null) {
                        if (tableMetaInfo.getTableLastAccessTime().getTime() < curPathFileStatus.getAccessTime()) {
                            tableMetaInfo.setTableLastAccessTime(new Date(curPathFileStatus.getAccessTime()));
                        }
                    } else {
                        tableMetaInfo.setTableLastAccessTime(new Date(curPathFileStatus.getAccessTime()));
                    }
                    //最后修改时间
                    if (tableMetaInfo.getTableLastModifyTime() != null) {
                        if (tableMetaInfo.getTableLastModifyTime().getTime() < curPathFileStatus.getModificationTime()) {
                            tableMetaInfo.setTableLastModifyTime(new Date(curPathFileStatus.getModificationTime()));
                        }
                    } else {
                        tableMetaInfo.setTableLastModifyTime(new Date(curPathFileStatus.getModificationTime()));
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}

