package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.metric.emit.MetricEmitter;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListREQ;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListRSP;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelationTradeBody;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelationTradeReqBody;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportCategoryOne;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportCategoryTwo;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportImportantReason;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportLevel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReport;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReportMapper;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.third.financial.impl.events.PaymentWriteOffEvent;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportLevel.IMPORTANT;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportLevel.NORMAL;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportStatus.NOT_REPORT;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportStatus.REPORTED;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static cn.zswltech.mithras.service.others.Util.mithrasLong2BigDecimal;
import static java.util.Objects.requireNonNull;

/**
 * @author yibin
 */
@Slf4j
@Service
public class RiskControlGljyReportService extends ServiceImpl<RiskControlGljyReportMapper, RiskControlGljyReport> {
    @Resource
    private MetricEmitter metricEmitter;

    @XxlJob("syncRelatedClient")
    @Transactional(rollbackFor = Exception.class)
    public void syncRelatedClient() {
        try {
            List<RelatedClientListRSP> list = metricEmitter.fetchRelatedClientList(new RelatedClientListREQ());
            Map<String, Long> clientMap = getBean(ClientService.class).list(Wrappers.<Client>lambdaQuery()
                            .in(Client::getUscCode, list.stream().map(RelatedClientListRSP::getCreditCode).collect(Collectors.toSet())))
                    .stream().filter(e -> StrUtil.isNotBlank(e.getUscCode())).collect(Collectors.toMap(Client::getUscCode, Client::getId));
            List<RiskControlRelatedClient> insertList = list.stream()
                    //暂时只关注企业的；自然人的名单中并没有客户的身份证号
                    .filter(e -> equal(1, e.getPartyType()) && clientMap.containsKey(e.getCreditCode()))
                    .map(e -> {
                        RiskControlRelatedClient one = new RiskControlRelatedClient();
                        one.setUscd(e.getCreditCode());
                        one.setClientName(e.getName());
                        //party type; 1企业 2自然人
                        one.setRelatedPartyType(String.valueOf(e.getPartyType()));
                        one.setDescription(e.getRelationDesc());
                        one.setClientId(clientMap.get(e.getCreditCode()));
                        return one;
                    }).collect(Collectors.toList());
            //全量替换，先删除，再插入
            if (isNotEmpty(insertList)) {
                //删除已有的关联方
                getBean(RiskControlRelatedClientService.class).remove(Wrappers.lambdaQuery());
                //插入所有的
                getBean(RiskControlRelatedClientService.class).saveBatch(insertList);
            }
        } catch (Exception e) {
            log.error("同步关联交易关联方名单失败", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addManually(RiskControlGljyReport report) {
        report.setReportStatus(NOT_REPORT.name());
     /*   List<RiskControlGljyReport> list = this.list(Wrappers.<RiskControlGljyReport>lambdaQuery()
                .eq(RiskControlGljyReport::getTradePartyName, report.getTradePartyName())
                .eq(RiskControlGljyReport::getTradeDate, report.getTradeDate())
                .eq(RiskControlGljyReport::getAmount, report.getAmount())
        );
        err(CollUtil.isNotEmpty(list), "已存在相同的记录");*/
        this.save(report);
    }

    @Transactional(rollbackFor = Exception.class)
    public void submit(List<Long> idList) {
        List<RiskControlGljyReport> reports = this.listByIds(idList);
        err(reports.stream().anyMatch(e -> REPORTED.name().equals(e.getReportStatus())), "已报送的记录的不能再次报送");
        //
        RelationTradeReqBody reqBody = new RelationTradeReqBody();
        List<RelationTradeBody> oneList = new ArrayList<>(reports.size());
        for (RiskControlGljyReport report : reports) {
            RelationTradeBody one = new RelationTradeBody();
            one.setRisk(report.getRisk());
            one.setAmount(mithrasLong2BigDecimal(report.getAmount()));
            one.setLevel(GljyReportLevel.valueOf(report.getLevel()).code);
            one.setOpinion(report.getOpinion());
            one.setPurpose(report.getPurpose());
            one.setDescription(report.getDescription());
            if (one.getLevel().equals(IMPORTANT.code)) {
                one.setImportantReason(GljyReportImportantReason.valueOf(report.getImportantReason()).code);
            }
            one.setTradeDate(report.getTradeDate());
            one.setSubjectPartyName(report.getSubjectPartyName());
            one.setTradeCategoryParentName(GljyReportCategoryOne.valueOf(report.getTradeCategoryParentName()).display);
            one.setTradeCategoryName(GljyReportCategoryTwo.valueOf(report.getTradeCategoryName()).display);
            one.setTradePartyAssets(mithrasLong2BigDecimal(report.getTradePartyAssets()));
            one.setTradePartyName(report.getTradePartyName());
            oneList.add(one);
        }
        reqBody.setRelatedTrades(oneList);
        String error = metricEmitter.emitRelationTrade(reqBody);
        err(isNotBlank(error), error);
        List<RiskControlGljyReport> updateList = reports.stream()
                .map(e -> new RiskControlGljyReport().setId(e.getId()).setReportStatus(REPORTED.name()))
                .collect(Collectors.toList());
        this.updateBatchById(updateList);
    }

    @Slf4j
    @Component
    public static class PaymentEndListener implements ApplicationListener<PaymentWriteOffEvent> {
        @Override
        @Transactional(rollbackFor = Exception.class)
        public void onApplicationEvent(PaymentWriteOffEvent event) {
            log.info("关联交易记录报送-监听付款核销结束； payment actual detail:{}", toJsonStr(event.getPaymentActualDetail()));
            PaymentActualDetail detail = event.getPaymentActualDetail();
            Long contractId = detail.getContractId();
            ContractBaseInfo contract = getBean(ContractBaseInfoService.class).getById(contractId);
            if (isNotNull(contract)) {
                //如果关联交易客户名单中未包含此笔付款的客户，则不用关注
                List<RiskControlRelatedClient> list = getBean(RiskControlRelatedClientService.class).list(Wrappers.<RiskControlRelatedClient>lambdaQuery()
                        .eq(RiskControlRelatedClient::getClientId, contract.getClientId()));
                if (list.size() > 0) {
                    ProjReviewBaseInfo review = getBean(ProjReviewBaseInfoService.class).getById(contract.getProjReviewId());
                    //
                    RiskControlGljyReport report = new RiskControlGljyReport();
                    report.setSubjectPartyName("浙江浙商融资租赁有限公司");
                    report.setTradePartyName(list.get(0).getClientName());
                    report.setTradeCategoryParentName(GljyReportCategoryOne.TRZL.name());
                    report.setTradeCategoryName(GljyReportCategoryTwo.RZZL.name());
                    report.setAmount(mithrasLong2BigDecimal(detail.getPaidInAmount()).longValue());//转换万元单位
                    report.setTradeDate(LocalDate.now());
//                        report.setTradePartyAssets()
                    report.setPurpose(isNull(review.getBizType()) ? "" : requireNonNull(ProjectBizType.of(review.getBizType())).display + "业务");
                    report.setLevel(detail.getPaidInAmount() > 500_000_000_0000L ? IMPORTANT.name() : NORMAL.name());
                    if (IMPORTANT.name().equals(report.getLevel())) {
                        report.setImportantReason(GljyReportImportantReason.one.name());
                    }
                    report.setDescription(report.getPurpose());
                    report.setOpinion("无影响");
                    report.setRisk("无意见");
                    report.setReportStatus(NOT_REPORT.name());
                    getBean(RiskControlGljyReportService.class).save(report);
                }
            }
        }
    }

}
