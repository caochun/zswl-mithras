package cn.zswltech.mithras.creditreport.flow.dynamicform;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.creditreport.mapper.CreditReportBaseInfoMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;


@Component
public class CreditSearchFormHandler implements DynamicFormHandler {

    @Resource
    private CreditReportBaseInfoMapper creditReportBaseInfoMapper;


    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.credit_search_handle_file;
    }
}
