package cn.zswltech.mithras.service.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.service.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
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
public class FtpIncomeJob {

    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FtpIncomeBaseInfoService ftpIncomeBaseInfoService;

    @XxlJob("ftpIncomeMaintenanceJob")
    @Transactional(rollbackFor = Exception.class)
    public void ftpIncomeMaintenanceJob() {
        try {
            LocalDate targetDateTime;
            String jobParam = XxlJobHelper.getJobParam();
            if (StrUtil.isNotBlank(jobParam)) {
                targetDateTime = LocalDateTimeUtil.parse(jobParam, DatePattern.NORM_DATE_PATTERN).toLocalDate();
            } else {
                targetDateTime = LocalDate.now().minusDays(1);
            }
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
