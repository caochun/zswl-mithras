package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.mithras.dto.flow.search.ReceiveTaskListRSP;

import java.util.List;

public interface AfterLeaseFlowTaskConvertPort {

    ReceiveTaskListRSP flowResp2ReceiveRSP(TaskResp resp, ProcessResp processResp);

    void receiveTaskListRSPFillName(List<ReceiveTaskListRSP> rspList);
}
