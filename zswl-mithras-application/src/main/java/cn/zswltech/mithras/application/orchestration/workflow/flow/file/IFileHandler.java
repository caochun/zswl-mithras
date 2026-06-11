package cn.zswltech.mithras.application.orchestration.workflow.flow.file;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;

import java.util.List;

/**
 * 流程中 动态表单 处理文件
 *
 * @author wangchuanhao
 * @date 2022/11/23 11:14 AM
 */
public interface IFileHandler {

    /**
     * 上传文件校验
     *
     * @param processResp
     */
    default void uploadCheck(ProcessResp processResp, String materialsType, String taskId) {

    }

    /**
     * 删除文件校验
     *
     * @param processResp
     */
    default void removeCheck(ProcessResp processResp, MaterialsList materialsList, String taskId) {

    }

    /**
     * 上传后处理
     *
     * @param processResp
     */
    default void afterUploadHook(ProcessResp processResp, String taskId) {

    }

    /**
     * 删除后处理
     *
     * @param processResp
     */
    default void afterRemoveHook(ProcessResp processResp, String taskId) {

    }

    /**
     * 获取文件
     *
     * @param processResp
     * @param materialsTypeList
     * @return
     */
    List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList);

    /**
     * 每个模块自己一个处理器
     *
     * @return
     */
    BusinessModuleEnum businessModule();

    /**
     * 转办
     *
     * @param materialsType
     * @return
     */
    String convertMaterialsType(String materialsType);

}
