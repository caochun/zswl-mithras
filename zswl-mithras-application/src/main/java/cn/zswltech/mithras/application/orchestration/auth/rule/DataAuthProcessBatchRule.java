package cn.zswltech.mithras.application.orchestration.auth.rule;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessBatchGuard;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限校验 判断是否在流程中
 *
 * @author wangchuanhao
 * @date 2022/7/21 11:56 PM
 */
@Slf4j
@Component
public class DataAuthProcessBatchRule implements DataAuthProcessBatchGuard {

    @Resource
    private FlowTaskApiService flowTaskApiService;

    /**
     * 判断是否在流程中
     * @param mainIds
     * @return
     */
    @Override
    public void check(DataAuthBusinessModule businessModule, Collection<Long> mainIds) {
        if (CollectionUtils.isEmpty(mainIds)) {
            return;
        }
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKeyList(mainIds.stream().map(String::valueOf).collect(Collectors.toList()));
        req.setPageIndex(1);
        req.setModelKeyList(businessModule.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        List<ProcessResp> processRespList = flowTaskApiService.queryProcess(req).getContents();
        // 把在发起人节点中的流程移除
        processRespList.removeIf(p -> FlowUtil.isStartUserNode(p));
        if (processRespList.size() > 0) {
            throw new AuthCheckException("有数据处于流程中，且流程不在发起人节点，不允许修改数据");
        }
    }
}
