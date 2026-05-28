package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.capital.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.service.mapper.model.third.FinanceFlowRecord;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/10/28 17:15
 * @description
 */
@Slf4j
@Component
public class HotFixJob {

    @Resource
    private FinanceFlowRecordService financeFlowRecordService;

    @XxlJob("financeFlowRecordJob")
    public void financeFlowRecordJob() {
        log.info("financeFlowRecordJob start");
        List<FinanceFlowRecord> flowRecords = financeFlowRecordService.list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .eq(FinanceFlowRecord::getFinancingFlowType, BankFlowCenterTypeEnum.PROCESSING_CENTER.name()));
        if (CollUtil.isNotEmpty(flowRecords)) {
            financeFlowRecordService.move2NoHandle(flowRecords);
        }
        log.info("financeFlowRecordJob end");
    }
}
