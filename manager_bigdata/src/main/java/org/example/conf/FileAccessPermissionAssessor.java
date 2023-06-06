package org.example.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
 


@Component("FILE_ACCESS_PERMISSION")
public class FileAccessPermissionAssessor extends Assessor {

    @Override
    protected void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam) throws Exception {

        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        String tableFsOwner = assessParam.getTableMetaInfo().getTableFsOwner();

        //提取建议权限参数
        JSONObject paramsJsonObj = JSON.parseObject(metricParamsJson);
        String dirPermission = paramsJsonObj.getString("dir_permission");
        String filePermission = paramsJsonObj.getString("file_permission");

        String tableFsPath = assessParam.getTableMetaInfo().getTableFsPath();


        boolean isBeyondPermission = beyondPermission(tableFsPath, tableFsOwner, dirPermission, filePermission,governanceAssessDetail);
        if(isBeyondPermission){
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            governanceAssessDetail.setAssessProblem("超过权限建议权限");
        }


    }


    //创建客户端，调用递归方法遍历目录文件，判断是否权限越级
    public  boolean  beyondPermission(String path,String fsUser,String dirPermission,String filePermission,GovernanceAssessDetail governanceAssessDetail) throws Exception{
        //初始化文件服务器客户端
        FileSystem fs = FileSystem.get(new URI(path), new Configuration(), fsUser);
        boolean  isBeyondPermission=false;
        if(fs.exists(new Path(path))){
            FileStatus[] fileStatuses = fs.listStatus(new Path(path));
            isBeyondPermission=beyondPermissionPathRec( fileStatuses,  fs,  dirPermission,  filePermission,governanceAssessDetail);
        }
        return isBeyondPermission;

    }

    // 递归方法遍历目录文件，判断是否权限越级
    public  boolean  beyondPermissionPathRec(FileStatus[] curPathFileStatuses,  FileSystem fs,String dirPermission,String filePermission,GovernanceAssessDetail governanceAssessDetail) throws Exception{

        for (FileStatus curPathFileStatus : curPathFileStatuses) {

            if(curPathFileStatus.isDirectory()){
                if(beyondPermissionFileOrDir(    curPathFileStatus.getPermission(),  dirPermission) ){
                    //governanceAssessDetail.addLog("目录:"+curPathFileStatus.getPath()+ "权限:"+curPathFileStatus.getPath()+"超过建议权限:"+dirPermission);
                    return true;

                }
                FileStatus[] fileStatuses = fs.listStatus(curPathFileStatus.getPath());
                if(beyondPermissionPathRec(fileStatuses,   fs,dirPermission,filePermission,governanceAssessDetail)){
                    return true;
                }
            }else {
                if( beyondPermissionFileOrDir(curPathFileStatus.getPermission(), filePermission)){
                    //governanceAssessDetail.addLog("文件:"+curPathFileStatus.getPath()+ "权限:"+curPathFileStatus.getPath()+",超过建议权限:"+dirPermission);
                    return true;
                }
            }

        }
        return false;
    }


    //比较权限是否越级
    private boolean beyondPermissionFileOrDir(  FsPermission fsPermission,String suggestPermission){
        char[] suggestPermissionChars = suggestPermission.toCharArray();
        int userOrdinalSug = Integer.valueOf(suggestPermissionChars[0]+ "");
        int groupOrdinalSug = Integer.valueOf(suggestPermissionChars[1]+ "");
        int otherOrdinalSug = Integer.valueOf(suggestPermissionChars[2]+ "");
        if( fsPermission.getUserAction().ordinal() >userOrdinalSug){
            return true;
        }else if( fsPermission.getGroupAction().ordinal() >groupOrdinalSug){
            return true;
        }else if( fsPermission.getOtherAction().ordinal() >otherOrdinalSug){
            return true;
        }
        return false;
    }
}
