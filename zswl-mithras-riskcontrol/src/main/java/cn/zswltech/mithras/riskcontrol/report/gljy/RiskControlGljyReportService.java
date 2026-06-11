package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportAddREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportListREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportListRSP;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportModifyREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportRelatedClientREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportRemoveREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportSubmitREQ;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientService;
import cn.zswltech.mithras.payment.event.PaymentWriteOffEvent;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportLevel.IMPORTANT;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportLevel.NORMAL;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportStatus.NOT_REPORT;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportStatus.REPORTED;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;
import static cn.zswltech.mithras.foundation.util.Util.mithrasLong2BigDecimal;
import static java.util.Objects.requireNonNull;

/**
 * @author yibin
 */
@Slf4j
@Service
public class RiskControlGljyReportService extends ServiceImpl<RiskControlGljyReportMapper, RiskControlGljyReport> implements RiskControlGljyReportApplicationService {
    @Resource
    private RiskControlGljyReportExternalPort externalPort;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private RiskControlRelatedClientService relatedClientService;

    @XxlJob("syncRelatedClient")
    @Transactional(rollbackFor = Exception.class)
    public void syncRelatedClient() {
        try {
            List<RiskControlRelatedClientExternal> list = externalPort.fetchRelatedClients();
            Map<String, Long> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                            .in(Client::getUscCode, list.stream().map(RiskControlRelatedClientExternal::getCreditCode).collect(Collectors.toSet())))
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
                relatedClientService.remove(Wrappers.lambdaQuery());
                //插入所有的
                relatedClientService.saveBatch(insertList);
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

    public void addManually(GljyReportAddREQ req) {
        err(req.getLevel().equals(GljyReportLevel.IMPORTANT.name()) && isBlank(req.getImportantReason()),
                "重大原因不能为空");
        this.addManually(BeanUtil.copyProperties(req, RiskControlGljyReport.class));
    }

    public void remove(GljyReportRemoveREQ req) {
        RiskControlGljyReport report = this.getById(req.getId());
        if (isNotNull(report) && REPORTED.name().equals(report.getReportStatus())) {
            err("记录已报送，不能删除");
        }
        this.removeById(req.getId());
    }

    public void modify(GljyReportModifyREQ req) {
        err(req.getLevel().equals(GljyReportLevel.IMPORTANT.name()) && isBlank(req.getImportantReason()),
                "重大原因不能为空");
        if (req.getLevel().equals(GljyReportLevel.NORMAL.name())) {
            req.setImportantReason("");
        }
        RiskControlGljyReport report = this.getById(req.getId());
        if (isNotNull(report) && REPORTED.name().equals(report.getReportStatus())) {
            err("记录已报送，不能修改");
        }
        this.updateById(BeanUtil.copyProperties(req, RiskControlGljyReport.class));
    }

    public PageR<GljyReportListRSP> pageList(GljyReportListREQ req) {
        Page<RiskControlGljyReport> data = this.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<RiskControlGljyReport>lambdaQuery()
                        .like(isNotBlank(req.getLevel()), RiskControlGljyReport::getLevel, req.getLevel())
                        .ge(isNotNull(req.getAmountFrom()), RiskControlGljyReport::getAmount, req.getAmountFrom())
                        .le(isNotNull(req.getAmountTo()), RiskControlGljyReport::getAmount, req.getAmountTo())
                        .eq(isNotBlank(req.getReportStatus()), RiskControlGljyReport::getReportStatus, req.getReportStatus())
                        .like(isNotBlank(req.getTradePartyName()), RiskControlGljyReport::getTradePartyName, req.getTradePartyName())
                        .ge(isNotNull(req.getTradeDateFrom()), RiskControlGljyReport::getTradeDate, req.getTradeDateFrom())
                        .le(isNotNull(req.getTradeDateTo()), RiskControlGljyReport::getTradeDate, req.getTradeDateTo())
                        .orderByAsc(RiskControlGljyReport::getReportStatus)
                        .orderByDesc(RiskControlGljyReport::getTradeDate)
        );

        List<GljyReportListRSP> list = BeanUtil.copyToList(data.getRecords(), GljyReportListRSP.class);
        return PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize());
    }

    public void submit(GljyReportSubmitREQ req) {
        this.submit(req.getIdList());
    }

    public List<String> relatedClientList(GljyReportRelatedClientREQ req) {
        Page<RiskControlRelatedClient> page = relatedClientService.page(
                new Page<>(1, 50),
                Wrappers.<RiskControlRelatedClient>lambdaQuery()
                        .like(isNotBlank(req.getName()), RiskControlRelatedClient::getClientName, req.getName())
        );
        return page.getRecords().stream().map(RiskControlRelatedClient::getClientName)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void submit(List<Long> idList) {
        List<RiskControlGljyReport> reports = this.listByIds(idList);
        err(reports.stream().anyMatch(e -> REPORTED.name().equals(e.getReportStatus())), "已报送的记录的不能再次报送");
        //
        List<RiskControlRelationTradeSubmitItem> oneList = new ArrayList<>(reports.size());
        for (RiskControlGljyReport report : reports) {
            RiskControlRelationTradeSubmitItem one = new RiskControlRelationTradeSubmitItem();
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
        String error = externalPort.submitRelationTrades(oneList);
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
            ContractBaseInfo contract = getBean(ContractBaseInfoMapper.class).selectById(contractId);
            if (isNotNull(contract)) {
                //如果关联交易客户名单中未包含此笔付款的客户，则不用关注
                List<RiskControlRelatedClient> list = getBean(RiskControlRelatedClientService.class).list(Wrappers.<RiskControlRelatedClient>lambdaQuery()
                        .eq(RiskControlRelatedClient::getClientId, contract.getClientId()));
                if (list.size() > 0) {
                    ProjReviewBaseInfo review = getBean(ProjReviewBaseInfoMapper.class).selectById(contract.getProjReviewId());
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
