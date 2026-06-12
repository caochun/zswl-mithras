package cn.zswltech.mithras.application.orchestration.job.capital;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.capital.enums.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.capital.job.service.FinanceAutoWriteOffJobService;
import cn.zswltech.mithras.third.financialshare.persistence.mapper.FinanceFlowRecordMapper;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowRecord;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FinanceAutoWriteOffJobServiceImpl implements FinanceAutoWriteOffJobService {

    @Resource
    private FinanceFlowRecordMapper financeFlowRecordMapper;

    @Override
    public void financeFlowAutoWriteOff() {
        log.info("定时任务 financeFlowAutoWriteOffJob 开始--------->");
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordMapper.selectList(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .ne(FinanceFlowRecord::getWriteOffStatus, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name()));
        if (CollUtil.isNotEmpty(financeFlowRecords)) {
            log.info("本次核销的流水ID：【{}】", CharSequenceUtil.join(",", financeFlowRecords.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList())));
        }
        log.info("定时任务 financeFlowAutoWriteOffJob 结束<---------");
    }
}
