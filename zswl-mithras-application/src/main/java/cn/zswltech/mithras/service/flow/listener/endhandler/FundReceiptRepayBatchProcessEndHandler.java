package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayVersionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 资金收付款 批量流程结束
 *
 * @author wangchuanhao
 * @date 2023/2/20 4:38 PM
 */
@Component
public class FundReceiptRepayBatchProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private FundReceiptRepayVersionService fundReceiptRepayVersionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.BatchFundReceiptRepayFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        fundReceiptRepayVersionService.batchProcessEnd(endContext.getModelKey(), Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
    }
}
