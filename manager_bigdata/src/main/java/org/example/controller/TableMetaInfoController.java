package org.example.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.TableMetaInfo;
import org.example.entity.TableMetaInfoExtra;
import org.example.entity.TableMetaInfoForQuery;
import org.example.entity.TableMetaInfoVO;
import org.example.service.TableMetaInfoExtraService;
import org.example.service.TableMetaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 元数据表(TableMetaInfo)表控制层
 *
 * @author makejava
 * @since 2023-06-05 14:56:35
 */
@RestController
@RequestMapping("tableMetaInfo")
public class TableMetaInfoController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private TableMetaInfoService tableMetaInfoService;
    @Resource
    private TableMetaInfoExtraService tableMetaInfoExtraService;
    @PostMapping("/init-tables/{database}")
    @CrossOrigin
    public void initTable( @PathVariable("database") String  databaseName){
        tableMetaInfoService.initTableMeta(databaseName );
    }

    @PostMapping("/table/{tableName}")
    public void testMeta(@PathVariable("tableName") String tableName){
        TableMetaInfo tableMetaInfo = tableMetaInfoService.getTableMetaInfoFromHive("gmall", tableName);
        tableMetaInfoService.addHdfsInfo(tableMetaInfo);

    }
    @GetMapping("/table-list")
    @CrossOrigin
    public Map tableList(TableMetaInfoForQuery tableMetaInfoForQuery  ){

        //查询列表
        List<TableMetaInfoVO> tableMetaInfoList = tableMetaInfoService.getTableMetaInfoList(tableMetaInfoForQuery);
        //查询总数
        tableMetaInfoForQuery.setPageSize(Integer.MAX_VALUE);
        Integer count = tableMetaInfoService.getTableMetaInfoCount(tableMetaInfoForQuery);
        //封装结果
        Map resultMap=new HashMap();
        resultMap.put("list",tableMetaInfoList);
        resultMap.put("total",count);
        return resultMap;
    }
    @GetMapping("/table/{tableMetaInfoId}")
    @CrossOrigin
    public TableMetaInfo tableDetail(@PathVariable("tableMetaInfoId") String tableMetaInfoId   ){
        TableMetaInfo tableMetaInfo = tableMetaInfoService.getTableMetaInfoAll(Long.valueOf(tableMetaInfoId) );
        return tableMetaInfo;
    }

    @PostMapping("/tableExtra")
    @CrossOrigin
    public String saveTableMetaInfoExtra(@RequestBody TableMetaInfoExtra tableMetaInfoExtra){
        tableMetaInfoExtraService.saveTableMetaInfoExtra (tableMetaInfoExtra );
        return "success";
    }




    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tableMetaInfo 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<TableMetaInfo> page, TableMetaInfo tableMetaInfo) {
        return success(this.tableMetaInfoService.page(page, new QueryWrapper<>(tableMetaInfo)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.tableMetaInfoService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tableMetaInfo 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody TableMetaInfo tableMetaInfo) {
        return success(this.tableMetaInfoService.save(tableMetaInfo));
    }

    /**
     * 修改数据
     *
     * @param tableMetaInfo 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody TableMetaInfo tableMetaInfo) {
        return success(this.tableMetaInfoService.updateById(tableMetaInfo));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.tableMetaInfoService.removeByIds(idList));
    }
}

