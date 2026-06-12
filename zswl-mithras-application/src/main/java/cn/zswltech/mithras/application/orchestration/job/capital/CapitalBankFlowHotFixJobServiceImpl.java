package cn.zswltech.mithras.application.orchestration.job.capital;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.capital.job.port.CapitalBankFlowHotFixJobService;
import cn.zswltech.mithras.capital.enums.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.application.orchestration.capital.FinanceFlowRecordService;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowRecord;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
public class CapitalBankFlowHotFixJobServiceImpl implements CapitalBankFlowHotFixJobService {

    @Resource
    private FinanceFlowRecordService financeFlowRecordService;

    @Override
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
