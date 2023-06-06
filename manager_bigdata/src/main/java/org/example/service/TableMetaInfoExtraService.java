package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.TableMetaInfo;
import org.example.entity.TableMetaInfoExtra;

import java.util.List;

/**
 * 元数据表附加信息(TableMetaInfoExtra)表服务接口
 *
 * @author makejava
 * @since 2023-06-05 15:30:39
 */
public interface TableMetaInfoExtraService extends IService<TableMetaInfoExtra> {
    public  void genExtraListByMetaList(List<TableMetaInfo> tableMetaInfoList );

    void saveTableMetaInfoExtra(TableMetaInfoExtra tableMetaInfoExtra);
}

