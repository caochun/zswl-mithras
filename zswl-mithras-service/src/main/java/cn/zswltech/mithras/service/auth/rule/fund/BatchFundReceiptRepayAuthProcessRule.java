package cn.zswltech.mithras.service.auth.rule.fund;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayVersionService;
import cn.zswltech.mithras.service.util.FlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * 数据权限校验 判断是否在流程中
 * 资金收付款 批量操作
 * @author wangchuanhao
 * @date 2022/7/21 11:56 PM
 */
@Slf4j
@Component
public class BatchFundReceiptRepayAuthProcessRule {

    @Resource
    private FundReceiptRepayVersionService fundReceiptRepayVersionService;

    /**
     * 判断是否在流程中
     * @param mainId
     * @return
     */
    public void check(BusinessModuleEnum businessModule, Long mainId) {
        ProcessResp processResp = fundReceiptRepayVersionService.findBatchProcess(ListUtil.toList(mainId), Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        if (Objects.isNull(processResp)) {
            // 流程为空 放过
            return;
        }
        boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
        if (!isStartUserNode) {
            throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
        }
    }
}
