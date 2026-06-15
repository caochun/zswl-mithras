package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.ftp;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.document.materialsfile.MaterialsListQueryService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;



@Component
public class SetFtpMeetingFileHandler implements DynamicFormHandler {
    private static final String NEW_FTP_GUIDANCE = "NEW_FTP_GUIDANCE";

    @Resource
    private MaterialsListQueryService materialsListQueryService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Assert.isTrue(materialsListQueryService.exists(NEW_FTP_GUIDANCE, "MEETING_FILE", Long.valueOf(taskResp.getBusinessKey())), () -> MithrasException.newException("请先上传会议纪要"));
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.ftp_setMeetingFile;
    }

}
