package cn.zswltech.mithras.fund.application.receiptrepay.port;

import cn.zswltech.flow.core.domain.resp.ProcessResp;

import java.util.List;

public interface FundReceiptRepayProcessQueryPort {

    ProcessResp findRelatedProcess(Long fundReceiptRepayId);

    ProcessResp findBatchProcess(List<Long> batchIdList, List<Integer> statusList);
}
