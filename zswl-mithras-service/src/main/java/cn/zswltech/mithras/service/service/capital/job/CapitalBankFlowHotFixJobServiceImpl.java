package cn.zswltech.mithras.service.service.capital.job;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.capital.application.job.CapitalBankFlowHotFixJobService;
import cn.zswltech.mithras.service.enums.capital.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
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
