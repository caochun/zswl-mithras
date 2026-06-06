package cn.zswltech.mithras.service.service.ftp.job;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.ftp.oldftp.service.job.FtpIncomeJobService;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.service.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/8/10
 * @description
 */
@Slf4j
@Component
public class FtpIncomeJobServiceImpl implements FtpIncomeJobService {

    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FtpIncomeBaseInfoService ftpIncomeBaseInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ftpIncomeMaintenance(LocalDate targetDateTime) {
        try {
            //查询昨天核销数据
            List<FundReceiptFlowDetail> list = fundReceiptFlowDetailService.list(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                    .ge(FundReceiptFlowDetail::getUpdateTime, targetDateTime));
            if (ObjectUtil.isEmpty(list)) {
                log.info("ftpIncomeMaintenanceJob 无需处理数据");
            }
            Map<Long, LocalDate>  receiptRepayId2Date = list.stream().collect(Collectors.toMap(FundReceiptFlowDetail::getReceiptRepayId, FundReceiptFlowDetail::getCashFlowDate, (a, b) -> a.isAfter(b) ? a : b));
            List<FundReceiptRepayBaseInfo> receiptRepayBaseInfos = fundReceiptRepayBaseInfoService.listByIds(receiptRepayId2Date.keySet());
            if (ObjectUtil.isEmpty(receiptRepayBaseInfos)) {
                return;
            }
            receiptRepayBaseInfos.forEach(base -> {
                ftpIncomeBaseInfoService.createOrUpdate(base.getFinancingId(), base.getFinancingType(), receiptRepayId2Date.get(base.getId()));
            });
        } catch (Exception e){
            log.error("ftpIncomeMaintenanceJob has error", e);
        }
    }
}
