package cn.zswltech.mithras.capital.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.capital.enums.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.third.financialshare.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.financialshare.mapper.FinanceFlowRecordMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/6/5/16:37
 * @description
 */
@Component
@Slf4j
public class FinanceAutoWriteOffJob {

    @Resource
    private FinanceFlowRecordMapper financeFlowRecordMapper;

    @XxlJob(value = "financeFlowAutoWriteOffJob")
    public void financeFlowAutoWriteOffJob() {
        log.info("定时任务 financeFlowAutoWriteOffJob 开始--------->");
        //查询未核销完毕的流水
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordMapper.selectList(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .ne(FinanceFlowRecord::getWriteOffStatus, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name()));
        if (CollUtil.isNotEmpty(financeFlowRecords)) {
            log.info("本次核销的流水ID：【{}】", CharSequenceUtil.join(",", financeFlowRecords.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList())));
        }
        log.info("定时任务 financeFlowAutoWriteOffJob 结束<---------");
    }
}
