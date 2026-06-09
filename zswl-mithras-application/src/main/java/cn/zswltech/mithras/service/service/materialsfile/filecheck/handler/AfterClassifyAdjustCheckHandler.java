package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * 租后调整
 * @author: jackerhe
 * @date: 2023/8/30 5:29 下午
 **/
@Component
public class AfterClassifyAdjustCheckHandler extends FileModuleCheck {

    @Resource
    private AfterLeaseAdjustInfoService adjustInfoService;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.ADJUST.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        // 资产管理岗可发起
        AfterLeaseAdjustInfo baseInfo = adjustInfoService.getById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 确定是否为发起人
        if (Objects.equals(AccountUtil.getLoginInfo().getId(), baseInfo.getProjSponsorUserId())) {
            // 只上传尽调报告
            if(!AfterLeaseAdjustMaterialsEnum.DUE_DILIGENCE_REPORT.name().equals(materialsType)){
                throw new MithrasException("发起人只允许上传业务申请资料");
            }
        } else {
            throw new AuthCheckException("详情页只允许发起人上传，其他岗位请在审批流中上传相应文件报告");
        }
    }


    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        checkJob();
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds){
       checkJob();
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
    }

    private void checkJob(){
    }
}
