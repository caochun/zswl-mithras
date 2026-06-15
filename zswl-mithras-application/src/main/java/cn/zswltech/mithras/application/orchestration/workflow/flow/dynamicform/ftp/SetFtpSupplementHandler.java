package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.ftp;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.document.materialsfile.MaterialsListQueryService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;



@Component
public class SetFtpSupplementHandler implements DynamicFormHandler {
    private static final String FTP_QUARTERLY_GUIDANCE = "FTP_QUARTERLY_GUIDANCE";
    private static final String FTP_MONTHLY_GUIDANCE = "FTP_MONTHLY_GUIDANCE";

    @Resource
    private MaterialsListQueryService materialsListQueryService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        String businessModuleName = Optional.ofNullable(ProcessModelTypeEnum.getByName(taskResp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        if (Objects.equals(FTP_QUARTERLY_GUIDANCE, businessModuleName)) {
            Assert.isTrue(materialsListQueryService.exists(FTP_QUARTERLY_GUIDANCE, "SUPPLEMENT", Long.valueOf(taskResp.getBusinessKey())), () -> MithrasException.newException("请先上传补充资料"));
        } else if (Objects.equals("NEW_FTP_GUIDANCE", businessModuleName)) {
            Assert.isTrue(materialsListQueryService.exists(FTP_MONTHLY_GUIDANCE, "SUPPLEMENT", Long.valueOf(taskResp.getBusinessKey())), () -> MithrasException.newException("请先上传补充资料"));
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.ftp_setSupplement;
    }
}
