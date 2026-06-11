package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseFlowTaskConvertPort;
import cn.zswltech.mithras.dto.flow.search.ReceiveTaskListRSP;
import cn.zswltech.mithras.workflow.flow.convert.FlowTaskConvert;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AfterLeaseFlowTaskConvertAdapter implements AfterLeaseFlowTaskConvertPort {

    @Resource
    private FlowTaskConvert flowTaskConvert;

    @Override
    public ReceiveTaskListRSP flowResp2ReceiveRSP(TaskResp resp, ProcessResp processResp) {
        return flowTaskConvert.flowResp2ReceiveRSP(resp, processResp);
    }

    @Override
    public void receiveTaskListRSPFillName(List<ReceiveTaskListRSP> rspList) {
        flowTaskConvert.receiveTaskListRSPFillName(rspList);
    }
}
