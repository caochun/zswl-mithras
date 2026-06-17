package cn.zswltech.mithras.application.orchestration.job.capital;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.capital.application.port.ManualWriteOffReleaseBankFlowJobPort;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.capital.FinanceFlowRecordService;
import cn.zswltech.mithras.third.financialshare.persistence.mapper.FinanceFlowTabMainInfoMapper;
import cn.zswltech.mithras.third.financialshare.persistence.mapper.FinanceFlowTabRecordMapper;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowTabMainInfo;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowTabRecord;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowTabMainInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/10/12 10:45
 * @description
 */
@Slf4j
@Component
public class ManualWriteOffReleaseBankFlowJobPortAdapter implements ManualWriteOffReleaseBankFlowJobPort {

    @Resource
    private FinanceFlowTabMainInfoService mainInfoService;
    @Resource
    private FinanceFlowTabMainInfoMapper mainInfoMapper;
    @Resource
    private FinanceFlowTabRecordMapper flowTabRecordMapper;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;

    @Override
    public void releaseBankFlow() {
        List<FinanceFlowTabMainInfo> mainInfos = mainInfoMapper.findWithLogicDelete(null);
        if (mainInfos.isEmpty()) {
            log.info("无未释放的账单");
            return;
        }
        List<FinanceFlowTabMainInfo> tabMainInfos = mainInfos.stream().filter(mainInfo -> {
            String batchNumber = mainInfo.getBatchNumber();
            LocalDateTime localDate = LocalDateTimeUtil.parse(batchNumber, DatePattern.PURE_DATETIME_MS_PATTERN);
            return localDate.isBefore(LocalDateTime.now().minusMinutes(15));
        }).collect(Collectors.toList());
        List<FinanceFlowTabMainInfo> effectInfos = mainInfos.stream().filter(mainInfo -> {
            String batchNumber = mainInfo.getBatchNumber();
            LocalDateTime localDate = LocalDateTimeUtil.parse(batchNumber, DatePattern.PURE_DATETIME_MS_PATTERN);
            return !localDate.isBefore(LocalDateTime.now().minusMinutes(15));
        }).collect(Collectors.toList());
        List<Long> financeFlowIds = new LinkedList<>();
        if (CollUtil.isNotEmpty(effectInfos)) {
            financeFlowIds = flowTabRecordMapper.selectList(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                            .in(FinanceFlowTabRecord::getMainId, effectInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList())))
                    .stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList());
        }
        List<Long> needReleaseFlowIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(tabMainInfos)) {
            List<FinanceFlowTabRecord> recordList = flowTabRecordMapper.findWithLogicDelete(tabMainInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList()));
            // 更新tab状态
            mainInfoService.lambdaUpdate()
                    .set(FinanceFlowTabMainInfo::getIsExpired, YesOrNoNumberEnum.YES.getCode())
                    .set(FinanceFlowTabMainInfo::getDeleted, YesOrNoNumberEnum.YES.getCode())
                    .in(FinanceFlowTabMainInfo::getId, mainInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList()))
                    .notIn(CollUtil.isNotEmpty(effectInfos), FinanceFlowTabMainInfo::getId, effectInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList()))
                    .update();
            needReleaseFlowIds.addAll(recordList.stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList()));
        }

        // 作为兜底，将超过20分钟未解锁的流水释放
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordService.list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .eq(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.NO.getCode())
                .lt(FinanceFlowRecord::getModifytime, LocalDateTime.now().minusMinutes(20)));
        if (CollUtil.isNotEmpty(financeFlowRecords)) {
            needReleaseFlowIds.addAll(financeFlowRecords.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(needReleaseFlowIds)) {
            financeFlowRecordService.lambdaUpdate()
                    .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                    .in(FinanceFlowRecord::getId, needReleaseFlowIds)
                    .notIn(CollUtil.isNotEmpty(financeFlowIds), FinanceFlowRecord::getId, financeFlowIds)
                    .update();
            log.info("银行流水释放成功， id列表={}", needReleaseFlowIds);
        }
    }
}
