package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.payment.event.PaymentWriteOffEvent;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportCategoryOne;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportCategoryTwo;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportImportantReason;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportLevel;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportStatus;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReport;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReportService;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.foundation.util.Util.mithrasLong2BigDecimal;
import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class RiskControlGljyPaymentWriteOffListener implements ApplicationListener<PaymentWriteOffEvent> {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private RiskControlRelatedClientService relatedClientService;
    @Resource
    private RiskControlGljyReportService gljyReportService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onApplicationEvent(PaymentWriteOffEvent event) {
        log.info("关联交易记录报送-监听付款核销结束； payment actual detail:{}", toJsonStr(event.getPaymentActualDetail()));
        PaymentActualDetail detail = event.getPaymentActualDetail();
        ContractBaseInfo contract = contractBaseInfoMapper.selectById(detail.getContractId());
        if (isNotNull(contract)) {
            List<RiskControlRelatedClient> list = relatedClientService.list(Wrappers.<RiskControlRelatedClient>lambdaQuery()
                    .eq(RiskControlRelatedClient::getClientId, contract.getClientId()));
            if (list.size() > 0) {
                ProjReviewBaseInfo review = projReviewBaseInfoMapper.selectById(contract.getProjReviewId());
                RiskControlGljyReport report = new RiskControlGljyReport();
                report.setSubjectPartyName("浙江浙商融资租赁有限公司");
                report.setTradePartyName(list.get(0).getClientName());
                report.setTradeCategoryParentName(GljyReportCategoryOne.TRZL.name());
                report.setTradeCategoryName(GljyReportCategoryTwo.RZZL.name());
                report.setAmount(mithrasLong2BigDecimal(detail.getPaidInAmount()).longValue());
                report.setTradeDate(LocalDate.now());
                report.setPurpose(isNull(review.getBizType()) ? "" : requireNonNull(ProjectBizType.of(review.getBizType())).display + "业务");
                report.setLevel(detail.getPaidInAmount() > 500_000_000_0000L ? GljyReportLevel.IMPORTANT.name() : GljyReportLevel.NORMAL.name());
                if (GljyReportLevel.IMPORTANT.name().equals(report.getLevel())) {
                    report.setImportantReason(GljyReportImportantReason.one.name());
                }
                report.setDescription(report.getPurpose());
                report.setOpinion("无影响");
                report.setRisk("无意见");
                report.setReportStatus(GljyReportStatus.NOT_REPORT.name());
                gljyReportService.save(report);
            }
        }
    }
}
