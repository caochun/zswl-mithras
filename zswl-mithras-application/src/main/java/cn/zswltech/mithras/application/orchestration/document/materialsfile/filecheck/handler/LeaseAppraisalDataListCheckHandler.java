package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.leaseholdproperty.enums.LeaseFileTypeEnums;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Component
public class LeaseAppraisalDataListCheckHandler extends FileModuleCheck {

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.LEASE_APPRAISAL_DATA_LIST.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        checkFlowFile(mainId);
        checkAuth(materialsType);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materials = getMaterials(fileId);
        if(ObjectUtil.isEmpty(materials)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkFlowFile(materials.getMainId());
        checkAuth(materials.getMaterialsType());
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        if(ObjectUtil.isEmpty(fileIds)){
            return;
        }
        List<MaterialsList> byIds = materialsListService.getByIds(fileIds);
        byIds.forEach( materials -> {
            if(ObjectUtil.isEmpty(materials)){
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            if(!AccountUtil.getLoginInfo().getId().equals(materials.getCreateBy())){
                throw new MithrasException("非文件上传人，不可删除");
            }
        });
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
    }

    private void checkFlowFile( Long mainId) {
//        BusinessModuleEnum businessModule = "LEASE_TEXT";
//        ProcessPageReq req = new ProcessPageReq();
//        req.setBusinessKey(String.valueOf(mainId));
//        req.setPageIndex(1);
//        req.setPageSize(1);
//        req.setModelKeyList(businessModule.getModelKeyList());
//        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
//        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
//                .stream().findFirst().orElse(null);
//        if (Objects.isNull(processResp)) {
//            throw new AuthCheckException("流程已结束，不允许上传文件");
//        }
    }

    private void checkAuth(String materialsType) {
        //判断文件类型
//        if (StrUtil.equalsAny(materialsType, LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_OWNERSHIP.name(), LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_DUPLICATE_CHECK.name() , LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_OTHER.name())) {
//            if (!sysUserService.currentUserIsSpecificJob(JobEnum.operationManagement.name())) {
//                throw new AuthCheckException("非运营经理,不可上传");
//            }
//        } else if (StrUtil.equalsAny(materialsType, LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_OWNERSHIP.name(), LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_DUPLICATE_CHECK.name(), LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_OTHER.name())) {
//            if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
//                throw new AuthCheckException("非项目经理,不可上传");
//            }
//        }
    }
}
