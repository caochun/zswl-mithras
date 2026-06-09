package cn.zswltech.mithras.service.flow.dynamicform.ftp;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;



@Component
public class SetFtpMeetingFileHandler implements DynamicFormHandler {
    private static final String NEW_FTP_GUIDANCE = "NEW_FTP_GUIDANCE";

    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, NEW_FTP_GUIDANCE);
        query.eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()));
        query.eq(MaterialsList::getMaterialsType, "MEETING_FILE");
        int count = materialsListMapper.selectCount(query);
        Assert.isTrue(count > 0, () -> MithrasException.newException("请先上传会议纪要"));
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
