package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.customer.externaldata.environmentpenalty.model.EnvironmentPenalty;
import cn.zswltech.mithras.customer.externaldata.tianyancha.model.*;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.model.ZhongdengInfo;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListREQ;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.customer.application.client.CorpSubjectItemApplicationService;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.customer.enums.SubjectItemDisplayDimension;
import cn.zswltech.mithras.customer.enums.SubjectItemType;
import cn.zswltech.mithras.customer.enums.SubjectReportType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.*;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectType;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractProjReviewReportRender;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.bo.ProjReviewRenderBO;
import cn.zswltech.mithras.foundation.bo.ProjectBizTypeBO;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewCashFlowPlanService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.Includes;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dingqi
 * @date 2022/8/3
 * @description 项目评审模块-尽调报告（租赁）渲染
 */
@Slf4j
@Component
public class ProjReviewJDReportZLRender extends AbstractProjReviewReportRender<ProjReviewRenderBO> {
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Resource
    private ProjReviewLeasePriceMapper projReviewLeasePriceMapper;
    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;
    @Resource
    private CorpSubjectItemApplicationService corpSubjectItemController;

    /**
     * 渲染尽调报告
     *
     * @param outputStream       尽调报告需要写入的输出流
     * @param projReviewRenderBO 项目评审基本信息&是否需要拼接多文档
     */
    @Override
    public String render(OutputStream outputStream, ProjReviewRenderBO projReviewRenderBO) throws Exception {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewRenderBO.getProjReviewBaseInfo();
        StopWatch sw = new StopWatch();
        sw.start("查询数据");
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("项目评审信息不存在");
        }
        ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceMapper.selectOne(
                Wrappers.<ProjReviewLeasePrice>lambdaQuery()
                        .eq(ProjReviewLeasePrice::getProjectId, projReviewBaseInfo.getId())
                        .orderByDesc(ProjReviewLeasePrice::getId)
        );
        if (Objects.isNull(projReviewLeasePrice)) {
            throw new MithrasException("报价方案不存在");
        }
        // 多线程查询关联数据
        JSONArray lesseeJsonArray = JSONUtil.parseArray(projReviewBaseInfo.getLesseeInfo());
        JSONArray guaranteeJsonArray = JSONUtil.parseArray(projReviewBaseInfo.getGuaranteeInfo());
        List<Long> lesseeIdList = this.transformIdList(lesseeJsonArray);
        List<Long> guaranteeIdList = this.transformIdList(guaranteeJsonArray);
        // 客户相关信息一次性查询，所有类型的客户id并入同一个集合中
        List<Long> clientIdList = new LinkedList<>();
        clientIdList.addAll(lesseeIdList);
        clientIdList.addAll(guaranteeIdList);
        // 查询客户基本信息
        CompletableFuture<Map<Long, Client>> clientMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getClientMap(clientIdList), threadPool);
        // 查询客户地址信息
        CompletableFuture<Map<Long, List<CorpAddressInfoLib>>> corpAddressMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getCorpAddressMap(clientIdList), threadPool);
        // 查询客户工商信息
        CompletableFuture<Map<Long, CorpCommerceInfoLib>> corpCommerceMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getCorpCommerceMap(clientIdList), threadPool);
        // 查询客户股东信息
        CompletableFuture<Map<Long, List<CorpShareholderInfoLib>>> corpShareholderMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getCorpShareholderMap(clientIdList), threadPool);
        // 查询客户关联企业
        CompletableFuture<Map<Long, List<CorpRelatedEnterpriseLib>>> clientMapcorpRelatedEnterpriseMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getCorpRelatedEnterpriseMap(clientIdList), threadPool);
        // 查询客户法律诉讼数量
        CompletableFuture<Map<Long, Integer>> tycLawSuitMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycLawSuitCountMap(clientIdList), threadPool);
        // 查询客户动产质押
        CompletableFuture<Map<Long, List<TycMortgageInfo>>> tycMortgageMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycMortgageMap(clientIdList), threadPool);
        // 查询客户股权出质
        CompletableFuture<Map<Long, List<TycEquityInfo>>> tycEquityMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycEquityMap(clientIdList), threadPool);
        // 查询客户行政处罚
        CompletableFuture<Map<Long, List<TycPunishmentInfo>>> tycPunishmentMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.geTycPunishmentMap(clientIdList), threadPool);
        // 查询客户环境处罚
        CompletableFuture<Map<Long, List<EnvironmentPenalty>>> environmentPenaltyMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getEnvironmentPenaltyMap(clientIdList), threadPool);
        // 查询客户经营异常
        CompletableFuture<Map<Long, List<TycAbnormal>>> tycAbnormalMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycAbnormalMap(clientIdList), threadPool);
        // 查询客户司法协助
        CompletableFuture<Map<Long, List<TycJudicial>>> tycJudicialMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycJudicialMap(clientIdList), threadPool);
        // 查询客户被限制消费令
        CompletableFuture<Map<Long, List<TycConsumptionRestriction>>> tycConsumptionRestrictionMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycConsumptionRestrictionMap(clientIdList), threadPool);
        // 查询客户被执行人
        CompletableFuture<Map<Long, List<TycZhixingInfo>>> tycZhixingInfoMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycZhixingInfoMap(clientIdList), threadPool);
        // 查询客户失信人
        CompletableFuture<Map<Long, List<TycDishonest>>> tycDishonestMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getTycDishonestMap(clientIdList), threadPool);
        // 查询中登网信息
        CompletableFuture<Map<Long, List<ZhongdengInfo>>> zhongdengInfoMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getZhongdengInfoMap(clientIdList), threadPool);
        // 查询承租人现金流计划
        CompletableFuture<List<ProjReviewCashFlowPlan>> cashFlowPlanListCF = CompletableFuture.supplyAsync(() -> projReviewCashFlowPlanService.listByProjReviewId(projReviewBaseInfo.getId(), null), threadPool);
        // 查询部门信息
        CompletableFuture<Map<Long, OrgDO>> orgMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getOrgMap(Collections.singletonList(projReviewBaseInfo.getBizDeptId())), threadPool);
        // 项目主办、项目协办、部门负责人、分管领导
        Long sponsorId = projReviewBaseInfo.getProjSponsorUserId();
        Collection<Long> cosponsorIdList = JSONUtil.toList(projReviewBaseInfo.getProjCosponsorUserIds(), Long.class);
        Long orgLeaderId = projReviewBaseInfo.getBizDeptLeaderId();
        Long divisionLeaderId = projReviewBaseInfo.getBizDivisionLeaderId();
        List<Long> allIds = new LinkedList<>();
        allIds.add(sponsorId);
        allIds.add(orgLeaderId);
        allIds.add(divisionLeaderId);
        if (!CollectionUtils.isEmpty(cosponsorIdList)) {
            allIds.addAll(cosponsorIdList);
        }
        // 批量查询用户信息
        CompletableFuture<Map<Long, UserDO>> userMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getUserMap(allIds), threadPool);
        // 等待获取结果数据
        Map<Long, Client> clientMap = clientMapCF.get();
        Map<Long, List<CorpAddressInfoLib>> corpAddressMap = corpAddressMapCF.get();
        Map<Long, CorpCommerceInfoLib> corpCommerceMap = corpCommerceMapCF.get();
        Map<Long, List<CorpShareholderInfoLib>> corpShareholderMap = corpShareholderMapCF.get();
        Map<Long, List<CorpRelatedEnterpriseLib>> corpRelatedEnterpriseMap = clientMapcorpRelatedEnterpriseMapCF.get();
        Map<Long, Integer> tycLawSuitMap = tycLawSuitMapCF.get();
        Map<Long, List<TycMortgageInfo>> tycMortgageMap = tycMortgageMapCF.get();
        Map<Long, List<TycEquityInfo>> tycEquityMap = tycEquityMapCF.get();
        Map<Long, List<TycPunishmentInfo>> tycPunishmentMap = tycPunishmentMapCF.get();
        Map<Long, List<EnvironmentPenalty>> environmentPenaltyMap = environmentPenaltyMapCF.get();
        Map<Long, List<TycAbnormal>> tycAbnormalMap = tycAbnormalMapCF.get();
        Map<Long, List<TycJudicial>> tycJudicialMap = tycJudicialMapCF.get();
        Map<Long, List<TycConsumptionRestriction>> tycConsumptionRestrictionMap = tycConsumptionRestrictionMapCF.get();
        Map<Long, List<TycZhixingInfo>> tycZhixingInfoMap = tycZhixingInfoMapCF.get();
        Map<Long, List<TycDishonest>> tycDishonestMap = tycDishonestMapCF.get();
        Map<Long, List<ZhongdengInfo>> zhongdengInfoMap = zhongdengInfoMapCF.get();
        List<ProjReviewCashFlowPlan> cashFlowPlanList = cashFlowPlanListCF.get();
        Map<Long, OrgDO> orgMap = orgMapCF.get();
        Map<Long, UserDO> userMap = userMapCF.get();
        sw.stop();


        sw.start("处理主模板数据");
        String currentDate = LocalDateTimeUtil.format(LocalDate.now(), DatePattern.CHINESE_DATE_PATTERN);
        String templatePath = "/doc/项目评审_尽调报告_租赁_主模板.docx";
        Map<String, Object> renderModelMap = new HashMap<>(256);
        if (projReviewLeasePrice.getApplyCreditAmount() >= 100000000 * Long.parseLong(GlobalConstants.MONEY_MULTIPLE)) {
            // 使用亿元单位
            BigDecimal bigDecimal = NumberUtil.div(projReviewLeasePrice.getApplyCreditAmount().toString(), String.valueOf(100000000 * Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
            renderModelMap.put(RenderModelBasicParameter.CREDIT_AMOUNT, NumberUtil.decimalFormat(",##0.00", bigDecimal) + "亿元");
        } else {
            // 使用千万单位
            BigDecimal bigDecimal = NumberUtil.div(projReviewLeasePrice.getApplyCreditAmount().toString(), String.valueOf(10000000 * Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
            renderModelMap.put(RenderModelBasicParameter.CREDIT_AMOUNT, NumberUtil.decimalFormat(",##0.00", bigDecimal) + "千万元");
        }
        renderModelMap.put(RenderModelBasicParameter.CREDIT_AMOUNT_WAN, this.toWan(projReviewLeasePrice.getApplyCreditAmount()));
        RateType rateType = RateType.of(projReviewLeasePrice.getRateType());
        if (Objects.nonNull(rateType)) {
            renderModelMap.put(RenderModelLeaseParameter.RATE_TYPE, rateType.display);
        }
        if (Objects.nonNull(projReviewLeasePrice.getLeaseRatePercent())) {
            renderModelMap.put(RenderModelLeaseParameter.RATE, NumberUtil.decimalFormat("#0.00", NumberUtil.div(projReviewLeasePrice.getLeaseRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE)));
        }
        if (Objects.nonNull(projReviewLeasePrice.getEarnestMoney())) {
            renderModelMap.put(RenderModelLeaseParameter.EARNEST_WAN, this.toWan(projReviewLeasePrice.getEarnestMoney()));
        }
        if (Objects.nonNull(projReviewLeasePrice.getNominalPrice())) {
            renderModelMap.put(RenderModelLeaseParameter.NOMINAL, this.toYuan(projReviewLeasePrice.getNominalPrice()));
        }
        if (Objects.nonNull(projReviewLeasePrice.getConsultingFee())) {
            renderModelMap.put(RenderModelLeaseParameter.CONSULTING_FEE_WAN, this.toWan(projReviewLeasePrice.getConsultingFee()));
        }
//        RentalCalcType rentalCalcType = RentalCalcType.of(projReviewLeasePrice.getRentalCalcType());
        RepayCalcType repayCalcType = RepayCalcType.find(projReviewLeasePrice.getRentalCalcType());
        if (Objects.nonNull(repayCalcType)) {
            renderModelMap.put(RenderModelLeaseParameter.RENTAL_CALCULATE_TYPE, repayCalcType.getDisplay());
        }
        PayType payType = PayType.of(projReviewLeasePrice.getPayType());
        if (Objects.nonNull(payType)) {
            renderModelMap.put(RenderModelLeaseParameter.PAY_TYPE, payType.display);
        }
        if (Objects.nonNull(projReviewLeasePrice.getLeaseMonthCount())) {
            renderModelMap.put(RenderModelLeaseParameter.LEASE_MONTH, projReviewLeasePrice.getLeaseMonthCount());
        }
        if (Objects.nonNull(projReviewLeasePrice.getIrrPercent())) {
            renderModelMap.put(RenderModelLeaseParameter.YIELD, NumberUtil.decimalFormat("#0.00", NumberUtil.div(projReviewLeasePrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE)));
        }
        renderModelMap.put(RenderModelBasicParameter.PROJECT_NAME, projReviewBaseInfo.getProjName());
        renderModelMap.put(RenderModelBasicParameter.REPORT_CREATE_DATE, currentDate);
        OrgDO orgDO = orgMap.get(projReviewBaseInfo.getBizDeptId());
        if (Objects.nonNull(orgDO)) {
            renderModelMap.put(RenderModelBasicParameter.ORG_NAME, orgDO.getName());
        }
        List<String> lesseeNameList = new LinkedList<>();
        for (Long lesseeId : lesseeIdList) {
            Client client = clientMap.get(lesseeId);
            if (Objects.nonNull(client)) {
                lesseeNameList.add(client.getClientName());
            }
        }
        if (!CollectionUtils.isEmpty(lesseeNameList)) {
            renderModelMap.put(RenderModelBasicParameter.LESSEE_INFO, Joiner.on("、").join(lesseeNameList));
        }
        List<String> guaranteeNameList = new LinkedList<>();
        for (Long guaranteeId : guaranteeIdList) {
            Client client = clientMap.get(guaranteeId);
            if (Objects.nonNull(client)) {
                guaranteeNameList.add(client.getClientName());
            }
        }
        if (!CollectionUtils.isEmpty(guaranteeNameList)) {
            renderModelMap.put(RenderModelBasicParameter.GUARANTEE_INFO, Joiner.on("、").join(guaranteeNameList));
        }
        if (Objects.nonNull(ProjectBizType.of(projReviewBaseInfo.getBizType()))) {
            renderModelMap.put(RenderModelBasicParameter.BIZ_TYPE, ProjectBizType.of(projReviewBaseInfo.getBizType()).display);
        }
        List<String> leaseTypeCodeList = JSONUtil.toList(projReviewBaseInfo.getLeaseTypes(), String.class);
        List<String> leaseTypeDescList = new ArrayList<>(leaseTypeCodeList.size());
        for (String type : leaseTypeCodeList) {
            leaseTypeDescList.add(LeaseType.of(type).display);
        }
        if (!CollectionUtils.isEmpty(leaseTypeDescList)) {
            renderModelMap.put(RenderModelBasicParameter.LEASE_TYPES, Joiner.on("、").join(leaseTypeDescList));
        }
        renderModelMap.put(RenderModelBasicParameter.IS_CLFD, Objects.equals(ProjSourceType.clfd.name(), projReviewBaseInfo.getProjSource()) ? "是" : "否");
        List<CorpAddressInfoLib> corpAddressInfoList = corpAddressMap.get(projReviewBaseInfo.getClientId());
        if (!CollectionUtils.isEmpty(corpAddressInfoList)) {
            for (CorpAddressInfo corpAddressInfo : corpAddressInfoList) {
                if (Objects.equals(CorpAddressType.WORK_ADDRESS.name(), corpAddressInfo.getAddressType())) {
                    renderModelMap.put(RenderModelBasicParameter.LESSEE_PROVINCE, businessDataRepository.getAddressNameFromLocalCache(corpAddressInfo.getProvince()));
                    renderModelMap.put(RenderModelBasicParameter.LESSEE_CITY, businessDataRepository.getAddressNameFromLocalCache(corpAddressInfo.getCity()));
                    renderModelMap.put(RenderModelBasicParameter.LESSEE_DISTRICT, businessDataRepository.getAddressNameFromLocalCache(corpAddressInfo.getDistrict()));
                    renderModelMap.put(RenderModelBasicParameter.LESSEE_WORK_ADDRESS, this.appendAddress(corpAddressInfo));
                    break;
                }
            }
        }
        if (!CollectionUtils.isEmpty(lesseeJsonArray)) {
            List<String> risk = new ArrayList<>(lesseeJsonArray.size());
            for (int i = 0; i < lesseeJsonArray.size(); i++) {
                JSONObject jsonObject = lesseeJsonArray.getJSONObject(i);
                Long stockRiskExposure = jsonObject.getLong("stockRiskExposure");
                if (stockRiskExposure != null) {
                    risk.add(this.toWan(stockRiskExposure));
                }
            }
            renderModelMap.put(RenderModelBasicParameter.RISK_EXPOSURE_WAN, Joiner.on("、").join(risk));
        }
        // 循环行表格插件生成现金流表
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder().bind(RenderModelBasicParameter.CASH_FLOW_PLAN, policy).build();
        if (!CollectionUtils.isEmpty(cashFlowPlanList)) {
            List<Map<String, Object>> table = new ArrayList<>(cashFlowPlanList.size());
            for (ProjReviewCashFlowPlan projReviewCashFlowPlan : cashFlowPlanList) {
                Map<String, Object> map = new HashMap<>(16);
                if (Objects.nonNull(projReviewCashFlowPlan.getCashFlowDate())) {
                    map.put("cashFlowDate", LocalDateTimeUtil.format(projReviewCashFlowPlan.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
                }
                map.put("cashFlowPhase", projReviewCashFlowPlan.getCashFlowPhase());
                if (Objects.nonNull(projReviewCashFlowPlan.getCashFlowAmount())) {
                    map.put("cashFlowAmount", this.toYuan(projReviewCashFlowPlan.getCashFlowAmount()));
                }
                if (Objects.nonNull(projReviewCashFlowPlan.getRent())) {
                    map.put("rent", this.toYuan(projReviewCashFlowPlan.getRent()));
                }
                if (Objects.nonNull(projReviewCashFlowPlan.getPrincipal())) {
                    map.put("principal", this.toYuan(projReviewCashFlowPlan.getPrincipal()));
                }
                if (Objects.nonNull(projReviewCashFlowPlan.getInterest())) {
                    map.put("interest", this.toYuan(projReviewCashFlowPlan.getInterest()));
                }
                table.add(map);
            }
            renderModelMap.put(RenderModelBasicParameter.CASH_FLOW_PLAN, table);
        } else {
            renderModelMap.put(RenderModelBasicParameter.CASH_FLOW_PLAN, Collections.emptyList());
        }
        renderModelMap.put(RenderModelBasicParameter.SPONSOR, userMap.get(sponsorId).getUserName());
        renderModelMap.put(RenderModelBasicParameter.ORG_LEADER, userMap.get(orgLeaderId).getUserName());
        renderModelMap.put(RenderModelBasicParameter.DIVISION_LEADER, userMap.get(divisionLeaderId).getUserName());
        if (!CollectionUtils.isEmpty(cosponsorIdList)) {
            List<String> cosponsorNames = new ArrayList<>(cosponsorIdList.size());
            for (Long id : cosponsorIdList) {
                UserDO cosponsor = userMap.get(id);
                if (Objects.nonNull(cosponsor)) {
                    cosponsorNames.add(cosponsor.getUserName());
                }
            }
            renderModelMap.put(RenderModelBasicParameter.COSPONSOR, Joiner.on("、").join(cosponsorNames));
        }
        CorpCommerceInfo lesseeCorpCommerceInfo = corpCommerceMap.get(projReviewBaseInfo.getClientId());
        if (Objects.nonNull(lesseeCorpCommerceInfo)) {
            renderModelMap.put(RenderModelBasicParameter.LESSEE_LEGAL_PERSON, lesseeCorpCommerceInfo.getCorpRepresent());
        }
        sw.stop();

        // 承租人子模板渲染
        sw.start("承租人子模板渲染");
        for (int i = 0; i < lesseeIdList.size(); i++) {
            Map<String, Object> subRenderMap = new HashMap<>(256);
            Client client = clientMap.get(lesseeIdList.get(i));
            subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_NAME, client.getClientName());
            List<CorpAddressInfoLib> addressList = corpAddressMap.get(client.getId());
            if (!CollectionUtils.isEmpty(addressList)) {
                List<String> registerAddrNameList = new ArrayList<>();
                List<String> workAddrNameList = new ArrayList<>();
                for (CorpAddressInfo corpAddressInfo : addressList) {
                    if (Objects.equals(CorpAddressType.REGISTRY_ADDRESS.name(), corpAddressInfo.getAddressType())) {
                        registerAddrNameList.add(this.appendAddress(corpAddressInfo));
                    }
                    if (Objects.equals(CorpAddressType.WORK_ADDRESS.name(), corpAddressInfo.getAddressType())) {
                        workAddrNameList.add(this.appendAddress(corpAddressInfo));
                    }
                }
                if (!CollectionUtils.isEmpty(registerAddrNameList)) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_REGISTER_ADDR, Joiner.on("、").join(registerAddrNameList));
                }
                if (!CollectionUtils.isEmpty(workAddrNameList)) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_WORK_ADDR, Joiner.on("、").join(workAddrNameList));
                }
            }
            subRenderMap.put(RenderModelBasicParameter.REPORT_CREATE_DATE, currentDate);
            CorpCommerceInfo corpCommerceInfo = corpCommerceMap.get(client.getId());
            if (Objects.nonNull(corpCommerceInfo)) {
                if (Objects.nonNull(corpCommerceInfo.getEstablishDate())) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_ESTABLISH_DATE, LocalDateTimeUtil.format(corpCommerceInfo.getEstablishDate(), DatePattern.CHINESE_DATE_FORMATTER));
                }
                if (!StringUtils.isEmpty(corpCommerceInfo.getCorpRepresent())) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_LEGAL_PERSON, corpCommerceInfo.getCorpRepresent());
                }
                if (!StringUtils.isEmpty(corpCommerceInfo.getEconomyType())) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_ECONOMY_TYPE, businessDataRepository.getEconomyTypeNameFromLocalCache(corpCommerceInfo.getEconomyType()));
                }
                if (!StringUtils.isEmpty(corpCommerceInfo.getIndustryType())) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_INDUSTRY_TYPE, businessDataRepository.getIndustryTypeNameFromLocalCache(corpCommerceInfo.getIndustryType()));
                }
                if (!StringUtils.isEmpty(corpCommerceInfo.getBizScope())) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_BIZ_SCOPE, corpCommerceInfo.getBizScope());
                }
                if (Objects.nonNull(corpCommerceInfo.getRegisterCapital())) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_REGISTER_CAPITAL, this.toWan(corpCommerceInfo.getRegisterCapital()) + "万" + businessDataRepository.getCurrencyTypeNameFromLocalCache(corpCommerceInfo.getRegisterCurrencyType()));
                }
                if (Objects.nonNull(corpCommerceInfo.getRealCapital())) {
                    subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_REAL_CAPITAL, this.toWan(corpCommerceInfo.getRealCapital()) + "万" + Optional.ofNullable(businessDataRepository.getCurrencyTypeNameFromLocalCache(corpCommerceInfo.getRealCurrencyType())).orElse(""));
                }
            }
            List<CorpShareholderInfoLib> corpShareholderInfoList = corpShareholderMap.get(client.getId());
            if (!CollectionUtils.isEmpty(corpShareholderInfoList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_SHAREHOLDER_TABLE, this.renderCorpShareholderTable(corpShareholderInfoList));
            }
            List<CorpRelatedEnterpriseLib> corpRelatedEnterpriseList = corpRelatedEnterpriseMap.get(client.getId());
            if (!CollectionUtils.isEmpty(corpRelatedEnterpriseList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_RELATED_ENTERPRISE_TABLE, this.renderCorpRelatedEnterpriseTable(corpRelatedEnterpriseList));
            }
            Integer lawSuitCount = tycLawSuitMap.get(client.getId());
            if (Objects.nonNull(lawSuitCount)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_LAW_SUIT_COUNT, lawSuitCount);
            }
            List<TycMortgageInfo> tycMortgageInfoList = tycMortgageMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycMortgageInfoList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_MORTGAGE_INFO_TABLE, this.renderTycMortgageInfoTable(tycMortgageInfoList));
            }
            List<TycEquityInfo> tycEquityInfoList = tycEquityMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycEquityInfoList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_EQUITY_INFO_TABLE, this.renderTycEquityInfoTable(tycEquityInfoList));
            }
            List<TycPunishmentInfo> tycPunishmentInfoList = tycPunishmentMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycPunishmentInfoList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_PUNISHMENT_INFO_TABLE, this.renderTycPunishmentInfoTable(tycPunishmentInfoList));
            }
            List<EnvironmentPenalty> environmentPenaltyList = environmentPenaltyMap.get(client.getId());
            if (!CollectionUtils.isEmpty(environmentPenaltyList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_ENVIRONMENT_TABLE, this.renderEnvironmentTable(environmentPenaltyList));
            }
            List<TycAbnormal> tycAbnormalList = tycAbnormalMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycAbnormalList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_ABNORMAL_TABLE, this.renderTycAbnormalTable(tycAbnormalList));
            }
            List<TycJudicial> tycJudicialList = tycJudicialMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycJudicialList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_JUDICIAL_TABLE, this.renderTycJudicialTable(tycJudicialList));
            }
            List<TycConsumptionRestriction> tycConsumptionRestrictionList = tycConsumptionRestrictionMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycConsumptionRestrictionList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_CONSUMPTION_RESTRICTION_TABLE, this.renderTycConsumptionRestrictionTable(tycConsumptionRestrictionList));
            }
            List<TycZhixingInfo> tycZhixingInfoList = tycZhixingInfoMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycZhixingInfoList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_PERFORMED_TABLE, this.renderTycPerformedTable(tycZhixingInfoList));
            }
            List<TycDishonest> tycDishonestList = tycDishonestMap.get(client.getId());
            if (!CollectionUtils.isEmpty(tycDishonestList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_DISHONEST_TABLE, this.renderTycDishonestTable(tycDishonestList));
            }
            List<ZhongdengInfo> zhongdengInfoList = zhongdengInfoMap.get(client.getId());
            if (!CollectionUtils.isEmpty(zhongdengInfoList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_ZHONGDENG_TABLE, this.renderZhongDengInfoTable(zhongdengInfoList));
            }
            // 财务报表信息
            List<CorpSubjectItemListRSP> capitalBalanceList = this.listCorpSubjectItem(client.getId(), SubjectItemType.CAPITAL_BALANCE);
            List<CorpSubjectItemListRSP> profitList = this.listCorpSubjectItem(client.getId(), SubjectItemType.PROFIT);
            List<CorpSubjectItemListRSP> cashFlowList = this.listCorpSubjectItem(client.getId(), SubjectItemType.CASH_FLOW);
            if (!CollectionUtils.isEmpty(capitalBalanceList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_CAPITAL_BALANCE_TABLE, this.renderFinancialTable(capitalBalanceList));
            }
            if (!CollectionUtils.isEmpty(profitList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_PROFIT_TABLE, this.renderFinancialTable(profitList));
            }
            if (!CollectionUtils.isEmpty(cashFlowList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_CASH_FLOW_TABLE, this.renderFinancialTable(cashFlowList));
            }
            if (!CollectionUtils.isEmpty(guaranteeIdList)) {
                subRenderMap.put(RenderLesseeSubModelParameter.SINGLE_LESSEE_GUARANTEES_TABLE, this.renderGuaranteesTable(clientMap, corpCommerceMap, guaranteeIdList));
            }
            DocxRenderData docxRenderData = Includes.ofStream(new ClassPathResource("doc/项目评审_尽调报告_承租人分析_子模板.docx").getStream()).setRenderModel(subRenderMap).create();
            renderModelMap.put("singleLesseeInfo" + (i + 1), docxRenderData);
        }
        sw.stop();

        // 担保人子模板渲染
        sw.start("担保人子模板渲染");
        if (CollectionUtils.isEmpty(guaranteeIdList)) {
            // 如果没有担保人，渲染一个空的子模板
            DocxRenderData docxRenderData = Includes.ofStream(new ClassPathResource("doc/项目评审_尽调报告_担保人分析_子模板.docx").getStream()).setRenderModel(new HashMap<>()).create();
            renderModelMap.put("singleGuaranteeInfo1", docxRenderData);
        } else {
            for (int i = 0; i < guaranteeIdList.size(); i++) {
                Map<String, Object> subRenderMap = new HashMap<>(256);
                Client client = clientMap.get(guaranteeIdList.get(i));
                // 如果担保人是自然人则忽略
                if (!Objects.equals(client.getClientType(), ClientType.CORPORATION.name())) {
                    log.info("担保人类型非法人类型，忽略不处理[projReviewId: {}]", projReviewBaseInfo.getId());
                    continue;
                }
                subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_NAME, client.getClientName());
                List<CorpAddressInfoLib> addressList = corpAddressMap.get(client.getId());
                if (!CollectionUtils.isEmpty(addressList)) {
                    List<String> registerAddrNameList = new ArrayList<>();
                    List<String> workAddrNameList = new ArrayList<>();
                    for (CorpAddressInfo corpAddressInfo : addressList) {
                        if (Objects.equals(CorpAddressType.REGISTRY_ADDRESS.name(), corpAddressInfo.getAddressType())) {
                            registerAddrNameList.add(this.appendAddress(corpAddressInfo));
                        }
                        if (Objects.equals(CorpAddressType.WORK_ADDRESS.name(), corpAddressInfo.getAddressType())) {
                            workAddrNameList.add(this.appendAddress(corpAddressInfo));
                        }
                    }
                    if (!CollectionUtils.isEmpty(registerAddrNameList)) {
                        subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_REGISTER_ADDR, Joiner.on("、").join(registerAddrNameList));
                    }
                    if (!CollectionUtils.isEmpty(workAddrNameList)) {
                        subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_WORK_ADDR, Joiner.on("、").join(workAddrNameList));
                    }
                }
                subRenderMap.put(RenderModelBasicParameter.REPORT_CREATE_DATE, currentDate);
                CorpCommerceInfo corpCommerceInfo = corpCommerceMap.get(client.getId());
                if (Objects.nonNull(corpCommerceInfo) && Objects.nonNull(corpCommerceInfo.getEstablishDate())) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_ESTABLISH_DATE, LocalDateTimeUtil.format(corpCommerceInfo.getEstablishDate(), DatePattern.CHINESE_DATE_FORMATTER));
                }
                subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_LEGAL_PERSON, corpCommerceInfo.getCorpRepresent());
                subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_ECONOMY_TYPE, businessDataRepository.getEconomyTypeNameFromLocalCache(corpCommerceInfo.getEconomyType()));
                subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_INDUSTRY_TYPE, businessDataRepository.getIndustryTypeNameFromLocalCache(corpCommerceInfo.getIndustryType()));
                subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_BIZ_SCOPE, corpCommerceInfo.getBizScope());
                if (Objects.nonNull(corpCommerceInfo.getRegisterCapital())) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_REGISTER_CAPITAL, this.toWan(corpCommerceInfo.getRegisterCapital()) + "万" + businessDataRepository.getCurrencyTypeNameFromLocalCache(corpCommerceInfo.getRegisterCurrencyType()));
                }
                if (Objects.nonNull(corpCommerceInfo.getRealCapital())) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_REAL_CAPITAL, this.toWan(corpCommerceInfo.getRealCapital()) + "万" + businessDataRepository.getCurrencyTypeNameFromLocalCache(corpCommerceInfo.getRealCurrencyType()));
                }
                List<CorpShareholderInfoLib> corpShareholderInfoList = corpShareholderMap.get(client.getId());
                if (!CollectionUtils.isEmpty(corpShareholderInfoList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_SHAREHOLDER_TABLE, this.renderCorpShareholderTable(corpShareholderInfoList));
                }
                List<CorpRelatedEnterpriseLib> corpRelatedEnterpriseList = corpRelatedEnterpriseMap.get(client.getId());
                if (!CollectionUtils.isEmpty(corpRelatedEnterpriseList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_RELATED_ENTERPRISE_TABLE, this.renderCorpRelatedEnterpriseTable(corpRelatedEnterpriseList));
                }
                Integer lawSuitCount = tycLawSuitMap.get(client.getId());
                if (Objects.nonNull(lawSuitCount)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_LAW_SUIT_COUNT, lawSuitCount);
                }
                List<TycMortgageInfo> tycMortgageInfoList = tycMortgageMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycMortgageInfoList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_MORTGAGE_INFO_TABLE, this.renderTycMortgageInfoTable(tycMortgageInfoList));
                }
                List<TycEquityInfo> tycEquityInfoList = tycEquityMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycEquityInfoList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_EQUITY_INFO_TABLE, this.renderTycEquityInfoTable(tycEquityInfoList));
                }
                List<TycPunishmentInfo> tycPunishmentInfoList = tycPunishmentMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycPunishmentInfoList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_PUNISHMENT_INFO_TABLE, this.renderTycPunishmentInfoTable(tycPunishmentInfoList));
                }
                List<EnvironmentPenalty> environmentPenaltyList = environmentPenaltyMap.get(client.getId());
                if (!CollectionUtils.isEmpty(environmentPenaltyList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_ENVIRONMENT_TABLE, this.renderEnvironmentTable(environmentPenaltyList));
                }
                List<TycAbnormal> tycAbnormalList = tycAbnormalMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycAbnormalList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_ABNORMAL_TABLE, this.renderTycAbnormalTable(tycAbnormalList));
                }
                List<TycJudicial> tycJudicialList = tycJudicialMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycJudicialList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_JUDICIAL_TABLE, this.renderTycJudicialTable(tycJudicialList));
                }
                List<TycConsumptionRestriction> tycConsumptionRestrictionList = tycConsumptionRestrictionMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycConsumptionRestrictionList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_CONSUMPTION_RESTRICTION_TABLE, this.renderTycConsumptionRestrictionTable(tycConsumptionRestrictionList));
                }
                List<TycZhixingInfo> tycZhixingInfoList = tycZhixingInfoMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycZhixingInfoList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_PERFORMED_TABLE, this.renderTycPerformedTable(tycZhixingInfoList));
                }
                List<TycDishonest> tycDishonestList = tycDishonestMap.get(client.getId());
                if (!CollectionUtils.isEmpty(tycDishonestList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_DISHONEST_TABLE, this.renderTycDishonestTable(tycDishonestList));
                }
                List<ZhongdengInfo> zhongdengInfoList = zhongdengInfoMap.get(client.getId());
                if (!CollectionUtils.isEmpty(zhongdengInfoList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_ZHONGDENG_TABLE, this.renderZhongDengInfoTable(zhongdengInfoList));
                }
                // 财务报表信息
                List<CorpSubjectItemListRSP> capitalBalanceList = this.listCorpSubjectItem(client.getId(), SubjectItemType.CAPITAL_BALANCE);
                List<CorpSubjectItemListRSP> profitList = this.listCorpSubjectItem(client.getId(), SubjectItemType.PROFIT);
                List<CorpSubjectItemListRSP> cashFlowList = this.listCorpSubjectItem(client.getId(), SubjectItemType.CASH_FLOW);
                if (!CollectionUtils.isEmpty(capitalBalanceList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_CAPITAL_BALANCE_TABLE, this.renderFinancialTable(capitalBalanceList));
                }
                if (!CollectionUtils.isEmpty(capitalBalanceList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_PROFIT_TABLE, this.renderFinancialTable(profitList));
                }
                if (!CollectionUtils.isEmpty(capitalBalanceList)) {
                    subRenderMap.put(RenderGuaranteeSubModelParameter.SINGLE_GUARANTEE_CASH_FLOW_TABLE, this.renderFinancialTable(cashFlowList));
                }
                DocxRenderData docxRenderData = Includes.ofStream(new ClassPathResource("doc/项目评审_尽调报告_担保人分析_子模板.docx").getStream()).setRenderModel(subRenderMap).create();
                renderModelMap.put("singleGuaranteeInfo" + (i + 1), docxRenderData);
            }
        }
        sw.stop();

        // 业务定价审批表子模板渲染
        if (projReviewRenderBO.isMultiparty()) {
            // 仅项目经理需要同时生成业务定价审批表
            sw.start("业务定价审批表子模板渲染");
            Map<String, Object> bizPriceRenderMap = new HashMap<>(16);
            UserDO userDO = userMap.get(projReviewBaseInfo.getProjSponsorUserId());
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.APPLY_DEPT, Optional.ofNullable(orgDO).map(OrgDO::getName).orElse(""));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.SPONSOR_NAME, Optional.ofNullable(userDO).map(UserDO::getUserName).orElse(""));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.APPLY_DATE, DateUtil.format(LocalDateTime.now(), DatePattern.CHINESE_DATE_PATTERN));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.PROJ_NAME, projReviewBaseInfo.getProjName());
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.PROJECT_CLASSIF, Optional.ofNullable(ProjectClassify.find(projReviewBaseInfo.getProjectClassify())).map(ProjectClassify::display).orElse(""));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.ENTERPRISE_NATURE, Optional.ofNullable(ProjectType.of(projReviewBaseInfo.getProjectType())).map(ProjectType::display).orElse(""));
            ProjectBizTypeBO projectBizTypeBO = new ProjectBizTypeBO();
            projectBizTypeBO.setProjectBizType(projReviewBaseInfo.getBizType());
            if (StrUtil.isNotBlank(projReviewBaseInfo.getLeaseTypes())) {
                projectBizTypeBO.setLeaseTypeList(JSONUtil.toList(projReviewBaseInfo.getLeaseTypes(), String.class));
            }
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.PROJECT_BIZ_TYPE, projectBizTypeBO.text());
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.CREDIT_AMOUNT_WAN, this.toWan(projReviewLeasePrice.getApplyCreditAmount()));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.LEASE_MONTH, projReviewLeasePrice.getLeaseMonthCount());
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.IRR, NumberUtil.div(projReviewLeasePrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.EARNEST_WAN, this.toWan(projReviewLeasePrice.getEarnestMoney()));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.INTEREST_RATE, NumberUtil.div(projReviewLeasePrice.getLeaseRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.RATE_TYPE, Optional.ofNullable(RateType.of(projReviewLeasePrice.getRateType())).map(RateType::display).orElse(""));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.RENT_CALCULATOR_TYPE, Optional.ofNullable(RepayCalcType.find(projReviewLeasePrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.CONSULTING_FEE_WAN, this.toWan(projReviewLeasePrice.getConsultingFee()));
            bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.NOMINAL_PRICE_WAN, this.toWan(projReviewLeasePrice.getNominalPrice()));
            RepayRateEnum repayRateEnum = RepayRateEnum.of(projReviewLeasePrice.getRepayRate());
            if (repayRateEnum == RepayRateEnum.LRREGULAR || repayRateEnum == RepayRateEnum.NON_STAGES) {
                bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.REPAY_RATE, repayRateEnum.display);
            } else {
                Integer n = this.calculateN(projReviewLeasePrice.getRepayRate(), projReviewLeasePrice.getLeaseMonthCount(), projReviewLeasePrice.getRepayTimesTotal());
                bizPriceRenderMap.put(RenderBizPriceParameterKeyHolder.REPAY_RATE, Optional.ofNullable(n).map(i -> "T+" + i).orElse("T+_"));
            }
            DocxRenderData docxRenderData = Includes.ofStream(new ClassPathResource("doc/项目评审_尽调报告_业务定价审批表_子模板.docx").getStream()).setRenderModel(bizPriceRenderMap).create();
            renderModelMap.put("businessPriceApproval", docxRenderData);
            sw.stop();
        }

        // 渲染并生成完整的尽调报告
        sw.start("尽调报告（租赁）主模板渲染");
        XWPFTemplate template = XWPFTemplate.compile(ProjReviewJDReportZLRender.class.getResourceAsStream(templatePath), config).render(renderModelMap);
        template.writeAndClose(outputStream);
        sw.stop();

        log.info("生成尽调报告耗时: {}", sw.prettyPrint(TimeUnit.SECONDS));

        return projReviewBaseInfo.getProjName() + "-项目报告" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private List<CorpSubjectItemListRSP> listCorpSubjectItem(Long clientId, SubjectItemType subjectItemType) {
        CorpSubjectItemListREQ req = new CorpSubjectItemListREQ();
        req.setClientId(clientId);
        req.setSubjectType(subjectItemType.name());
        req.setQuarter(12);
        req.setLatest(true);
        req.setReportType(SubjectReportType.MERGED.name());
        req.setDisplayDimensions(Collections.singletonList(SubjectItemDisplayDimension.BASE.name()));
        req.setUnit(1L);
        req.setYearTo(LocalDateTime.now().getYear());
        req.setYearFrom(req.getYearTo() - 3);
        R<List<CorpSubjectItemListRSP>> result = corpSubjectItemController.list(req);
        if (!result.isSuccess()) {
            log.warn("获取财务报表数据失败[req: {}, result: {}]", JSONUtil.toJsonStr(req), JSONUtil.toJsonStr(result));
            throw new MithrasException("获取财务报表（" + subjectItemType.display() + "）数据失败");
        }
        return result.getData();
    }

    /**
     * 子模板-担保人信息渲染数据模型参数
     */
    private static class RenderGuaranteeSubModelParameter {
        // 客户信息-基本信息-客户名称
        private static final String SINGLE_GUARANTEE_NAME = "singleGuaranteeName";
        // 客户信息-工商信息-成立日期
        private static final String SINGLE_GUARANTEE_ESTABLISH_DATE = "singleGuaranteeEstablishDate";
        // 客户信息-地址信息-注册地址
        private static final String SINGLE_GUARANTEE_REGISTER_ADDR = "singleGuaranteeRegisterAddr";
        // 客户信息-地址信息-经营地址
        private static final String SINGLE_GUARANTEE_WORK_ADDR = "singleGuaranteeWorkAddr";
        // 客户信息-工商信息-法人代表
        private static final String SINGLE_GUARANTEE_LEGAL_PERSON = "singleGuaranteeLegalPerson";
        // 客户信息-工商信息-经济类型
        private static final String SINGLE_GUARANTEE_ECONOMY_TYPE = "singleGuaranteeEconomyType";
        // 客户信息-工商信息-行业类型
        private static final String SINGLE_GUARANTEE_INDUSTRY_TYPE = "singleGuaranteeIndustryType";
        // 客户信息-工商信息-主营业务
        private static final String SINGLE_GUARANTEE_BIZ_SCOPE = "singleGuaranteeBizScope";
        // 客户信息-工商信息-注册资本
        private static final String SINGLE_GUARANTEE_REGISTER_CAPITAL = "singleGuaranteeRegisterCapital";
        // 客户信息-工商信息-实收资本
        private static final String SINGLE_GUARANTEE_REAL_CAPITAL = "singleGuaranteeRealCapital";
        // 客户信息-股东信息
        private static final String SINGLE_GUARANTEE_SHAREHOLDER_TABLE = "singleGuaranteeShareholderTable";
        // 客户信息-关联企业
        private static final String SINGLE_GUARANTEE_RELATED_ENTERPRISE_TABLE = "singleGuaranteeRelatedEnterpriseTable";
        // 客户信息-公开信息-法律诉讼
        private static final String SINGLE_GUARANTEE_LAW_SUIT_COUNT = "singleGuaranteeLawSuitCount";
        // 客户信息-公开信息-动产质押
        private static final String SINGLE_GUARANTEE_MORTGAGE_INFO_TABLE = "singleGuaranteeMortgageInfoTable";
        // 客户信息-公开信息-股权出质
        private static final String SINGLE_GUARANTEE_EQUITY_INFO_TABLE = "singleGuaranteeEquityInfoTable";
        // 客户信息-公开信息-行政处罚
        private static final String SINGLE_GUARANTEE_PUNISHMENT_INFO_TABLE = "singleGuaranteePunishmentInfoTable";
        // 客户信息-公开信息-环境处罚
        private static final String SINGLE_GUARANTEE_ENVIRONMENT_TABLE = "singleGuaranteeEnvironmentTable";
        // 客户信息-公开信息-经营异常
        private static final String SINGLE_GUARANTEE_ABNORMAL_TABLE = "singleGuaranteeAbnormalTable";
        // 客户信息-公开信息-司法协助
        private static final String SINGLE_GUARANTEE_JUDICIAL_TABLE = "singleGuaranteeJudicialTable";
        // 客户信息-公开信息-限制消费令
        private static final String SINGLE_GUARANTEE_CONSUMPTION_RESTRICTION_TABLE = "singleGuaranteeConsumptionRestrictionTable";
        // 客户信息-公开信息-被执行人
        private static final String SINGLE_GUARANTEE_PERFORMED_TABLE = "singleGuaranteePerformedTable";
        // 客户信息-公开信息-失信人
        private static final String SINGLE_GUARANTEE_DISHONEST_TABLE = "singleGuaranteeDishonestTable";
        // 客户信息-公开信息-中登网
        private static final String SINGLE_GUARANTEE_ZHONGDENG_TABLE = "singleGuaranteeZhongDengTable";
        // 客户信息-财务信息-资产负债
        private static final String SINGLE_GUARANTEE_CAPITAL_BALANCE_TABLE = "singleGuaranteeCapitalBalanceTable";
        // 客户信息-财务信息-利润
        private static final String SINGLE_GUARANTEE_PROFIT_TABLE = "singleGuaranteeProfitTable";
        // 客户信息-财务信息-现金流量
        private static final String SINGLE_GUARANTEE_CASH_FLOW_TABLE = "singleGuaranteeCashFlowTable";
    }

    /**
     * 子模板-承租人信息渲染数据模型参数
     */
    private static class RenderLesseeSubModelParameter {
        // 客户信息-基本信息-客户名称
        private static final String SINGLE_LESSEE_NAME = "singleLesseeName";
        // 客户信息-工商信息-成立日期
        private static final String SINGLE_LESSEE_ESTABLISH_DATE = "singleLesseeEstablishDate";
        // 客户信息-地址信息-注册地址
        private static final String SINGLE_LESSEE_REGISTER_ADDR = "singleLesseeRegisterAddr";
        // 客户信息-地址信息-经营地址
        private static final String SINGLE_LESSEE_WORK_ADDR = "singleLesseeWorkAddr";
        // 客户信息-工商信息-法人代表
        private static final String SINGLE_LESSEE_LEGAL_PERSON = "singleLesseeLegalPerson";
        // 客户信息-工商信息-经济类型
        private static final String SINGLE_LESSEE_ECONOMY_TYPE = "singleLesseeEconomyType";
        // 客户信息-工商信息-行业类型
        private static final String SINGLE_LESSEE_INDUSTRY_TYPE = "singleLesseeIndustryType";
        // 客户信息-工商信息-主营业务
        private static final String SINGLE_LESSEE_BIZ_SCOPE = "singleLesseeBizScope";
        // 客户信息-工商信息-注册资本
        private static final String SINGLE_LESSEE_REGISTER_CAPITAL = "singleLesseeRegisterCapital";
        // 客户信息-工商信息-实收资本
        private static final String SINGLE_LESSEE_REAL_CAPITAL = "singleLesseeRealCapital";
        // 客户信息-股东信息
        private static final String SINGLE_LESSEE_SHAREHOLDER_TABLE = "singleLesseeShareholderTable";
        // 客户信息-关联企业
        private static final String SINGLE_LESSEE_RELATED_ENTERPRISE_TABLE = "singleLesseeRelatedEnterpriseTable";
        // 客户信息-公开信息-法律诉讼
        private static final String SINGLE_LESSEE_LAW_SUIT_COUNT = "singleLesseeLawSuitCount";
        // 客户信息-公开信息-动产质押
        private static final String SINGLE_LESSEE_MORTGAGE_INFO_TABLE = "singleLesseeMortgageInfoTable";
        // 客户信息-公开信息-股权出质
        private static final String SINGLE_LESSEE_EQUITY_INFO_TABLE = "singleLesseeEquityInfoTable";
        // 客户信息-公开信息-行政处罚
        private static final String SINGLE_LESSEE_PUNISHMENT_INFO_TABLE = "singleLesseePunishmentInfoTable";
        // 客户信息-公开信息-环境处罚
        private static final String SINGLE_LESSEE_ENVIRONMENT_TABLE = "singleLesseeEnvironmentTable";
        // 客户信息-公开信息-经营异常
        private static final String SINGLE_LESSEE_ABNORMAL_TABLE = "singleLesseeAbnormalTable";
        // 客户信息-公开信息-司法协助
        private static final String SINGLE_LESSEE_JUDICIAL_TABLE = "singleLesseeJudicialTable";
        // 客户信息-公开信息-限制消费令
        private static final String SINGLE_LESSEE_CONSUMPTION_RESTRICTION_TABLE = "singleLesseeConsumptionRestrictionTable";
        // 客户信息-公开信息-被执行人
        private static final String SINGLE_LESSEE_PERFORMED_TABLE = "singleLesseePerformedTable";
        // 客户信息-公开信息-失信人
        private static final String SINGLE_LESSEE_DISHONEST_TABLE = "singleLesseeDishonestTable";
        // 客户信息-公开信息-中登网
        private static final String SINGLE_LESSEE_ZHONGDENG_TABLE = "singleLesseeZhongDengTable";
        // 客户信息-财务信息-资产负债
        private static final String SINGLE_LESSEE_CAPITAL_BALANCE_TABLE = "singleLesseeCapitalBalanceTable";
        // 客户信息-财务信息-利润
        private static final String SINGLE_LESSEE_PROFIT_TABLE = "singleLesseeProfitTable";
        // 客户信息-财务信息-现金流量
        private static final String SINGLE_LESSEE_CASH_FLOW_TABLE = "singleLesseeCashFlowTable";
        // 担保人列表
        private static final String SINGLE_LESSEE_GUARANTEES_TABLE = "singleLesseeGuaranteesTable";
    }

    /**
     * 主模板-基础渲染数据模型参数
     */
    private static class RenderModelBasicParameter {
        // 项目评审-基本信息-项目名称
        public static final String PROJECT_NAME = "projName";
        // 报告生成时间
        public static final String REPORT_CREATE_DATE = "reportCreateDate";
        // 项目评审-基本信息-承租人名称，多个用"和"拼接
        public static final String LESSEE_INFO = "lesseeInfo";
        // 项目评审-基本信息-业务类型
        public static final String BIZ_TYPE = "bizType";
        // 项目评审-基本信息-租赁类型
        public static final String LEASE_TYPES = "leaseTypes";
        // 项目评审-报价方案-申报授信金额，超过亿元用亿元，不到亿元用千万元
        public static final String CREDIT_AMOUNT = "creditAmount";
        // 项目评审-基本信息-业务部门
        public static final String ORG_NAME = "orgName";
        // 项目评审-基本信息-项目来源，若为存量翻单则为是，否则为否
        public static final String IS_CLFD = "isClfd";
        // 项目评审-基本信息-担保人名称，多个用"和"拼接
        public static final String GUARANTEE_INFO = "guaranteeInfo";
        // 客户信息-承租人办公地址省份
        public static final String LESSEE_PROVINCE = "lesseeProvince";
        // 客户信息-承租人办公地址城市
        public static final String LESSEE_CITY = "lesseeCity";
        // 客户信息-承租人办公地址区域
        public static final String LESSEE_DISTRICT = "lesseeDistrict";
        // 报价方案-申报授信金额，单位：万元
        public static final String CREDIT_AMOUNT_WAN = "creditAmountW";
        // 现金流计划-租金总额，单位：万元
        public static final String RENT_TOTAL_WAN = "rentTotalW";
        // 基本信息-承租人，存量风险敞口，若为多个则展示多个，单位：万元
        public static final String RISK_EXPOSURE_WAN = "riskExposureW";
        // 项目评审-现金流计划
        public static final String CASH_FLOW_PLAN = "cashFlowPlan";
        // 项目评审-基本信息-项目主办
        public static final String SPONSOR = "sponsor";
        // 项目评审-基本信息-项目协办，多个用中文逗号分隔
        public static final String COSPONSOR = "cosponsor";
        // 项目评审-基本信息-部门负责人
        public static final String ORG_LEADER = "deptLeader";
        // 项目评审-基本信息-分管领导
        public static final String DIVISION_LEADER = "divisionLeader";
        // 客户信息-承租人办公地址
        public static final String LESSEE_WORK_ADDRESS = "lesseeWorkAddress";
        // 客户信息-承租人法人代表
        public static final String LESSEE_LEGAL_PERSON = "lesseeLegalPerson";
    }

    /**
     * 主模板-租赁/转租赁类型渲染数据模型参数
     */
    private static class RenderModelLeaseParameter {
        // 项目评审-报价方案-利率类型
        public static final String RATE_TYPE = "rateType";
        // 项目评审-报价方案-利率百分比
        public static final String RATE = "rate";
        // 项目评审-报价方案-保证金，单位：万元
        public static final String EARNEST_WAN = "earnestW";
        // 项目评审-报价方案-名义货价，单位：元
        public static final String NOMINAL = "nominal";
        // 项目评审-报价方案-服务费/咨询费，单位：万元
        public static final String CONSULTING_FEE_WAN = "consultingFeeW";
        // 项目评审-报价方案-租金计算方式
        public static final String RENTAL_CALCULATE_TYPE = "rentalCalcType";
        // 项目评审-报价方案-支付方式
        public static final String PAY_TYPE = "payType";
        // 项目评审-报价方案-租赁期限，单位：月
        public static final String LEASE_MONTH = "leaseMonth";
        // 项目评审-报价方案-收益率，单位：百分比
        public static final String YIELD = "yield";
    }

    private static class RenderBizPriceParameterKeyHolder {
        // 申请部门
        public static final String APPLY_DEPT = "applyDept";
        // 申请人
        public static final String SPONSOR_NAME = "sponsorName";
        // 申请日期
        public static final String APPLY_DATE = "applyDate";
        // 项目名称
        public static final String PROJ_NAME = "projName";
        // 项目分类
        public static final String PROJECT_CLASSIF = "projectClassif";
        // 企业性质
        public static final String ENTERPRISE_NATURE = "enterpriseNature";
        // 业务类型
        public static final String PROJECT_BIZ_TYPE = "projectBizType";
        // 授信金额（万元）
        public static final String CREDIT_AMOUNT_WAN = "creditAmountW";
        // 租赁期限（月）
        public static final String LEASE_MONTH = "leaseMonth";
        // 收益率（百分比）
        public static final String IRR = "irr";
        // 保证金（万元）
        public static final String EARNEST_WAN = "earnestW";
        // 合同利率
        public static final String INTEREST_RATE = "interestRate";
        // 利率类型
        public static final String RATE_TYPE = "rateType";
        // 支付频率
        public static final String REPAY_RATE = "repayRate";
        // 租金支付方式
        public static final String RENT_CALCULATOR_TYPE = "rentCalculatorType";
        // 服务费（万元）
        public static final String CONSULTING_FEE_WAN = "consultingFeeW";
        // 回购价款（万元）
        public static final String NOMINAL_PRICE_WAN = "nominalPriceW";
    }
}
