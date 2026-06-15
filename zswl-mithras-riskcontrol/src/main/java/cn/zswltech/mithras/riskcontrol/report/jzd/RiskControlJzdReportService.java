package cn.zswltech.mithras.riskcontrol.report.jzd;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportAddREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportListREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportListRSP;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportModifyREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportRemoveREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportSubmitREQ;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlMarginPort;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.date.LocalDateTimeUtil.format;
import static cn.hutool.core.lang.Validator.isNull;
import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum.gyfss;
import static cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum.gyss;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportAssetsCategory.KYL;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportAssetsCategory.ZCL;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportBizType.ZSZL_SXLYW_BL_BL;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportBizType.ZSZL_SXLYW_RZZL_RZZL;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportCreateType.TIMELY;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportEconomyComposition.GYJJ;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportEconomyComposition.SYJJ;
import static cn.zswltech.mithras.riskcontrol.report.jzd.JzdReportStatus.NOT_REPORT;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.foundation.util.Util.mithrasLong2BigDecimal;
import static cn.zswltech.mithras.foundation.util.StringUtil.mysqlLimit;

/**
 * @author yibin
 */
@Slf4j
@Service
public class RiskControlJzdReportService extends ServiceImpl<RiskControlJzdReportMapper, RiskControlJzdReport> implements RiskControlJzdReportApplicationService, RiskControlJzdReportTimedCreateService {
    @Resource
    private RiskControlJzdReportExternalPort externalPort;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private OrgService orgService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private RiskControlMarginPort riskControlMarginPort;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;

    /**
     * 每月自动生成
     */
    @Transactional(rollbackFor = Exception.class)
    public void generateQuarterlyData(LocalDate dataMonth) {
        dataMonth = dataMonth.withDayOfMonth(1);
        int count = count(Wrappers.<RiskControlJzdReport>lambdaQuery()
                .eq(RiskControlJzdReport::getDataMonth, dataMonth)
                .eq(RiskControlJzdReport::getCreateType, TIMELY.name())
        );
        //已经存在记录，不再生成
        if (count > 0) {
            log.warn("已存在该月份的集中度报送生成记录，不再生成");
            return;
        }
        //开始生成
        List<ContractBaseInfo> contractAllList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.CLOSED.name(), ContractStatus.INVALID.name())
        );
        //按项目进行分组
        List<RiskControlJzdReport> result = new ArrayList<>();
        Map<Long, List<ContractBaseInfo>> projContractMap = contractAllList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        for (Map.Entry<Long, List<ContractBaseInfo>> entry : projContractMap.entrySet()) {
            RiskControlJzdReport jzdReport = new RiskControlJzdReport();
            Long reviewId = entry.getKey();
            try {
                List<ContractBaseInfo> contractList = entry.getValue();
                ProjReviewBaseInfo projReview = projReviewBaseInfoMapper.selectById(reviewId);
                String bizTyp = ProjectBizType.BL.name().equals(projReview.getBizType()) ? ZSZL_SXLYW_BL_BL.name() : ZSZL_SXLYW_RZZL_RZZL.name();
                String targetSubject = ProjectBizType.BL.name().equals(projReview.getBizType()) ? "应收账款" : "租赁物";
                Long bizAmountTotal = calcBizAmountTotal(contractList);
                if (bizAmountTotal == 0) {
                    continue;
                }
                Long bizAmountLeft = calcBizAmountLeft(contractList);
                Client client = clientMapper.selectById(projReview.getClientId());
                String enterpriseNature = null;
                CorpCommerceInfo clientCommerceInfo = corpCommerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, client.getId()));
                if (isNotNull(clientCommerceInfo)) {
                    enterpriseNature = clientCommerceInfo.getEnterpriseNature();
                }
                String economicComposition = equalsAny(enterpriseNature, gyfss.name(), gyss.name()) ? GYJJ.name() : SYJJ.name();
                String sponsorOrgName = orgService.selectByPrimaryKey(projReview.getBizDeptId()).getName();
                LocalDate bizStartDate = getBizStartDate(contractList);
                LocalDate bizEndDate = getBizEndDate(contractList);
                Long bzjValue = calcBzjValue(contractList);
                String guaranteeInfo = projReview.getGuaranteeInfo();
                String guaranteeName = null;
                if (isNotBlank(guaranteeInfo)) {
                    List<ClientInfo> clientInfoList = JSON.parseArray(guaranteeInfo, ClientInfo.class);
                    if (CollUtil.isNotEmpty(clientInfoList)) {
                        guaranteeName = clientInfoList.get(0).getClientName();
                    }
                }
                Integer overdueDays = calcOverdueDays(contractList);
                Long overdueValue = calcOverdueValue(contractList);
                JzdReportAssetsCategory category = getAssetsCategory(client);


                //
                jzdReport.setDataMonth(dataMonth);
                jzdReport.setReportStatus(NOT_REPORT.name());
                jzdReport.setBizType(bizTyp);
                jzdReport.setTargetSubject(targetSubject);
                jzdReport.setBizAmountTotal(bizAmountTotal / 10000);
                jzdReport.setBizAmountLeft(bizAmountLeft / 10000);
                jzdReport.setClientName(client.getClientName());
                jzdReport.setClientSameTrade("否");//默认都设置成否
                jzdReport.setEconomicComposition(economicComposition);
                jzdReport.setSponsorOrgName(sponsorOrgName);
                jzdReport.setBizStartDate(bizStartDate);
                jzdReport.setBizEndDate(bizEndDate);
                jzdReport.setEnsureValue(bzjValue / 10000);
                jzdReport.setGuaranteeName(guaranteeName);
                jzdReport.setOverdueDays(overdueDays);
                jzdReport.setOverdueValue(overdueValue / 10000);
                jzdReport.setAssetsCategory(category.name());
                jzdReport.setCreateType(TIMELY.name());
                jzdReport.setProjReviewId(reviewId);
//                jzdReport.setYjtjzValue(0L);
                result.add(jzdReport);
            } catch (Exception e) {
                log.error("计算项目评审[{}]的集中度数据失败", reviewId, e);
            }
        }
        this.saveBatch(result);
    }

    private JzdReportAssetsCategory getAssetsCategory(Client client) {
        Response<SystemConfigDO> rsp = systemConfigService.getConfig("jzd_report_special_config");
        if (Boolean.TRUE.equals(rsp.getSuccess())) {
            String configValue = rsp.getData().getConfigValue();
            if (StrUtil.isNotBlank(configValue)) {
                JSONObject jsonObject = JSONUtil.parseObj(configValue);
                JSONArray array = jsonObject.getJSONArray("suspiciousClientName");
                Set<Object> set = new HashSet<>(array);
                if (set.contains(client.getClientName())) {
                    return KYL;
                }
            }
        }
        return ZCL;
    }

    private Long calcOverdueValue(List<ContractBaseInfo> contractList) {
        //select datediff(NOW(),plan_collection_date) as overdueDays from collection_base_info where id = 1647;
        QueryWrapper<CollectionBaseInfo> wrapper = new QueryWrapper<>();
        wrapper.select(" IFNULL(SUM(plan_collection_amount-collection_amount),0) as collection_amount")
                .apply("collection_amount != plan_collection_amount AND plan_collection_date<NOW()")
                .in("contract_id", contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .eq("cash_flow_item", CashFlowItemEnum.RENT.name());
        Long overdueValue = collectionBaseInfoMapper.selectOne(wrapper).getCollectionAmount();
        if (isNull(overdueValue)) {
            overdueValue = 0L;
        }
        return overdueValue < 0 ? 0L : overdueValue;
    }

    private Integer calcOverdueDays(List<ContractBaseInfo> contractList) {
        //select datediff(NOW(),plan_collection_date) as overdueDays from collection_base_info where id = 1647;
        QueryWrapper<CollectionBaseInfo> wrapper = new QueryWrapper<>();
        wrapper.select(" IFNULL(SUM(datediff(NOW(),plan_collection_date)),0) as allRecordSort")
                .apply("collection_amount != plan_collection_amount")
                .in("contract_id", contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .eq("cash_flow_item", CashFlowItemEnum.RENT.name());
        Integer overdueDays = collectionBaseInfoMapper.selectOne(wrapper).getAllRecordSort();
        if (isNull(overdueDays)) {
            overdueDays = 0;
        }
        return overdueDays < 0 ? 0 : overdueDays;
    }

    private Long calcBzjValue(List<ContractBaseInfo> contractList) {
        return riskControlMarginPort.sumReportMarginAmount(contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
    }

    private LocalDate getBizEndDate(List<ContractBaseInfo> list) {
        QueryWrapper<CollectionBaseInfo> wrapper = new QueryWrapper<>();
        wrapper.select("MAX(plan_collection_date) planCollectionDate")
                .in("contract_id", list.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .eq("cash_flow_item", CashFlowItemEnum.RENT.name());

        CollectionBaseInfo one = collectionBaseInfoMapper.selectOne(wrapper);
        if (null != one) {
            return one.getPlanCollectionDate();
        }
        return null;
    }


    private LocalDate getBizStartDate(List<ContractBaseInfo> list) {
        PaymentActualDetail paymentActualDetail = paymentActualDetailMapper
                .selectOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getContractId, list.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .orderByAsc(PaymentActualDetail::getPaidInDate)
                        .last(mysqlLimit(0, 1))
                );
        if (isNotNull(paymentActualDetail)) {
            return paymentActualDetail.getPaidInDate();
        }
        return null;
    }

    private Long calcBizAmountLeft(List<ContractBaseInfo> list) {
        QueryWrapper<CollectionBaseInfo> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(principal - collection_principal),0) as principal")
                .in("contract_id", list.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .eq("cash_flow_item", CashFlowItemEnum.RENT.name());
        Long leftPrincipal = collectionBaseInfoMapper.selectOne(wrapper).getPrincipal();
        if (isNull(leftPrincipal)) {
            leftPrincipal = 0L;
        }
        return leftPrincipal;
    }

    private Long calcBizAmountTotal(List<ContractBaseInfo> list) {
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getContractId, list.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
        );
        return paymentActualDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long add(RiskControlJzdReport jzdReport) {
        this.save(jzdReport);
        return jzdReport.getId();
    }

    public void submit(JzdReportSubmitREQ req) {
        this.submit(req.getDataMonth());
    }

    public void addManually(JzdReportAddREQ req) {
        if (req.getDataMonth() != null) {
            req.setDataMonth(req.getDataMonth().withDayOfMonth(1));
        }
        RiskControlJzdReport riskControlJzdReport = BeanUtil.copyProperties(req, RiskControlJzdReport.class);
        riskControlJzdReport.setReportStatus(NOT_REPORT.name());
        this.add(riskControlJzdReport);
    }

    public void remove(JzdReportRemoveREQ req) {
        this.removeById(req.getId());
    }

    public void modify(JzdReportModifyREQ req) {
        if (req.getDataMonth() != null) {
            req.setDataMonth(req.getDataMonth().withDayOfMonth(1));
        }
        RiskControlJzdReport riskControlJzdReport = BeanUtil.copyProperties(req, RiskControlJzdReport.class);
        riskControlJzdReport.setReportStatus(NOT_REPORT.name());
        this.updateById(riskControlJzdReport);
    }

    public PageR<JzdReportListRSP> pageList(JzdReportListREQ req) {
        if (req.getDataMonth() != null) {
            req.setDataMonth(req.getDataMonth().withDayOfMonth(1));
        }

        Page<RiskControlJzdReport> data = this.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<RiskControlJzdReport>lambdaQuery()
                        .like(isNotBlank(req.getBizType()), RiskControlJzdReport::getBizType, req.getBizType())
                        .eq(isNotNull(req.getDataMonth()), RiskControlJzdReport::getDataMonth, req.getDataMonth())
                        .like(isNotBlank(req.getClientName()), RiskControlJzdReport::getClientName, req.getClientName())
                        .eq(isNotBlank(req.getCreateType()), RiskControlJzdReport::getCreateType, req.getCreateType())
        );

        List<JzdReportListRSP> list = BeanUtil.copyToList(data.getRecords(), JzdReportListRSP.class);
        PageR<JzdReportListRSP> pageR = PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize());
        boolean matched = list.stream().anyMatch(e -> NOT_REPORT.name().equals(e.getReportStatus()));
        pageR.getOthers().put("reportStatus", matched);
        return pageR;
    }

    @Transactional(rollbackFor = Exception.class)
    public void submit(LocalDate dataMonth) {
        List<RiskControlJzdReport> list = list(Wrappers.<RiskControlJzdReport>lambdaQuery()
                .eq(RiskControlJzdReport::getDataMonth, dataMonth.withDayOfMonth(1))
        );
        for (RiskControlJzdReport jzdReport : list) {
            err(isNull(jzdReport.getYjtjzValue()), String.format("客户[%s]的已计提减值未填写", jzdReport.getClientName()));
        }
        List<RiskControlJzdReportSubmitItem> dataList = new ArrayList<>();
        for (RiskControlJzdReport jzdReport : list) {
            RiskControlJzdReportSubmitItem data = new RiskControlJzdReportSubmitItem();
            data.setBizType(jzdReport.getBizType());
            data.setSubjectName(jzdReport.getTargetSubject());
            data.setBizTotalAmount(mithrasLong2BigDecimal(jzdReport.getBizAmountTotal()));
            data.setBizRestAmount(mithrasLong2BigDecimal(jzdReport.getBizAmountLeft()));
            data.setName(jzdReport.getClientName());
            data.setSameIndustryCustomer("是".equals(jzdReport.getClientSameTrade()));
            data.setEconomicComposition(JzdReportEconomyComposition.valueOf(jzdReport.getEconomicComposition()).code);
            data.setBizDept(jzdReport.getSponsorOrgName());
            data.setBizDateStart(jzdReport.getBizStartDate());
            data.setBizDateEnd(jzdReport.getBizEndDate());
            data.setEnsure(mithrasLong2BigDecimal(jzdReport.getEnsureValue()));
            data.setGuaranteeName(jzdReport.getGuaranteeName());
            data.setDecrease(mithrasLong2BigDecimal(jzdReport.getYjtjzValue()));
            data.setOverdueDay(jzdReport.getOverdueDays());
            data.setOverdueAmount(mithrasLong2BigDecimal(jzdReport.getOverdueValue()));
            data.setAssetsCategory((byte) JzdReportAssetsCategory.valueOf(jzdReport.getAssetsCategory()).code);
            dataList.add(data);
        }
        String error = externalPort.submitConcentration(format(dataMonth, "yyyy-MM"), dataList);
        err(isNotBlank(error), error);
    }
}
