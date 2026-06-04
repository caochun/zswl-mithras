package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.LeaseTextFileEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Component
public class LeaseItemCheckHandler extends FileModuleCheck {

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.LEASE_TEXT.name();
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
            checkFlowFile(materials.getMainId());
            checkAuth(materials.getMaterialsType());
        });
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
    }

    private void checkFlowFile( Long mainId) {
        BusinessModuleEnum businessModule = BusinessModuleEnum.LEASE_TEXT;
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(mainId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(businessModule.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            throw new AuthCheckException("流程已结束，不允许上传文件");
        }
    }

    private void checkAuth(String materialsType) {
        //判断文件类型
        if(LeaseTextFileEnum.LEASE_ITEM.name().equals(materialsType)){
            if (!sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name(), JobEnum.operationManagement.name(), JobEnum.projmanager.name(), JobEnum.legalmanager.name())) {
                throw new AuthCheckException("非项目经理、运营和法务,不可上传");
            }
        }
    }
}
