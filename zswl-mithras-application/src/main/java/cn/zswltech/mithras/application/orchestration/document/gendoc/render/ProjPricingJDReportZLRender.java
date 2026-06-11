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
import cn.zswltech.mithras.projectprocess.enums.projestablish.*;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectType;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractProjReviewReportRender;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingLeasePriceMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.bo.ProjPricingRenderBO;
import cn.zswltech.mithras.projectprocess.application.bo.ProjReviewRenderBO;
import cn.zswltech.mithras.foundation.bo.ProjectBizTypeBO;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingCashFlowPlanService;
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
 * @description 项目定价模块-业务定价审批表（租赁）渲染
 */
@Slf4j
@Component
public class ProjPricingJDReportZLRender extends AbstractProjReviewReportRender<ProjPricingRenderBO> {
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Resource
    private ProjPricingLeasePriceMapper projPricingLeasePriceMapper;
    @Resource
    private ProjPricingCashFlowPlanService projPricingCashFlowPlanService;
    @Resource
    private CorpSubjectItemApplicationService corpSubjectItemController;

    /**
     * 渲染定价报告
     *
     * @param outputStream       尽调报告需要写入的输出流
     * @param projPricingRenderBO 项目评审基本信息&是否需要拼接多文档
     */
    @Override
    public String render(OutputStream outputStream, ProjPricingRenderBO projPricingRenderBO) throws Exception {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingRenderBO.getProjPricingBaseInfo();
        StopWatch sw = new StopWatch();
        sw.start("查询数据");
        if (Objects.isNull(projPricingBaseInfo)) {
            throw new MithrasException("项目定价信息不存在");
        }
        ProjPricingLeasePrice projPricingLeasePrice = projPricingLeasePriceMapper.selectOne(
                Wrappers.<ProjPricingLeasePrice>lambdaQuery()
                        .eq(ProjPricingLeasePrice::getProjectId, projPricingBaseInfo.getId())
                        .orderByDesc(ProjPricingLeasePrice::getId)
        );
        if (Objects.isNull(projPricingLeasePrice)) {
            throw new MithrasException("报价方案不存在");
        }
        if(projPricingLeasePrice.getIrrPercent() == null){
            throw new MithrasException("irr为空");
        }
        if(projPricingLeasePrice.getLeaseRatePercent() == null){
            throw new MithrasException("租赁利率值为空");
        }
        // 多线程查询关联数据
        JSONArray lesseeJsonArray = JSONUtil.parseArray(projPricingBaseInfo.getLesseeInfo());
        JSONArray guaranteeJsonArray = JSONUtil.parseArray(projPricingBaseInfo.getGuaranteeInfo());
        List<Long> lesseeIdList = this.transformIdList(lesseeJsonArray);
        List<Long> guaranteeIdList = this.transformIdList(guaranteeJsonArray);
        // 客户相关信息一次性查询，所有类型的客户id并入同一个集合中
        List<Long> clientIdList = new LinkedList<>();
        clientIdList.addAll(lesseeIdList);
        clientIdList.addAll(guaranteeIdList);
        // 查询部门信息
        CompletableFuture<Map<Long, OrgDO>> orgMapCF = CompletableFuture.supplyAsync(() -> businessDataRepository.getOrgMap(Collections.singletonList(projPricingBaseInfo.getBizDeptId())), threadPool);
        // 项目主办、项目协办、部门负责人、分管领导
        Long sponsorId = projPricingBaseInfo.getProjSponsorUserId();
        Collection<Long> cosponsorIdList = JSONUtil.toList(projPricingBaseInfo.getProjCosponsorUserIds(), Long.class);
        Long orgLeaderId = projPricingBaseInfo.getBizDeptLeaderId();
        Long divisionLeaderId = projPricingBaseInfo.getBizDivisionLeaderId();
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
        Map<Long, OrgDO> orgMap = orgMapCF.get();
        Map<Long, UserDO> userMap = userMapCF.get();
        sw.stop();


        OrgDO orgDO = orgMap.get(projPricingBaseInfo.getBizDeptId());
        Map<String, Object> renderModelMap = new HashMap<>(256);
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder().bind(RenderModelBasicParameter.CASH_FLOW_PLAN, policy).build();
        String templatePath = "/doc/项目评审_尽调报告_业务定价审批表_子模板.docx";

        // 生成业务定价审批表
        sw.start("业务定价审批表子模板渲染");
        UserDO userDO = userMap.get(projPricingBaseInfo.getProjSponsorUserId());
        renderModelMap.put(RenderBizPriceParameterKeyHolder.APPLY_DEPT, Optional.ofNullable(orgDO).map(OrgDO::getName).orElse(""));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.SPONSOR_NAME, Optional.ofNullable(userDO).map(UserDO::getUserName).orElse(""));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.APPLY_DATE, DateUtil.format(LocalDateTime.now(), DatePattern.CHINESE_DATE_PATTERN));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.PROJ_NAME, projPricingBaseInfo.getProjName());
        renderModelMap.put(RenderBizPriceParameterKeyHolder.PROJECT_CLASSIF, Optional.ofNullable(ProjectClassify.find(projPricingBaseInfo.getProjectClassify())).map(ProjectClassify::display).orElse(""));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.ENTERPRISE_NATURE, Optional.ofNullable(ProjectType.of(projPricingBaseInfo.getProjectType())).map(ProjectType::display).orElse(""));
        ProjectBizTypeBO projectBizTypeBO = new ProjectBizTypeBO();
        projectBizTypeBO.setProjectBizType(projPricingBaseInfo.getBizType());
        if (StrUtil.isNotBlank(projPricingBaseInfo.getLeaseTypes())) {
            projectBizTypeBO.setLeaseTypeList(JSONUtil.toList(projPricingBaseInfo.getLeaseTypes(), String.class));
        }
        renderModelMap.put(RenderBizPriceParameterKeyHolder.PROJECT_BIZ_TYPE, projectBizTypeBO.text());
        renderModelMap.put(RenderBizPriceParameterKeyHolder.CREDIT_AMOUNT_WAN, this.toWan(projPricingLeasePrice.getApplyCreditAmount()));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.LEASE_MONTH, projPricingLeasePrice.getLeaseMonthCount());
        renderModelMap.put(RenderBizPriceParameterKeyHolder.IRR, NumberUtil.div(projPricingLeasePrice.getIrrPercent().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        renderModelMap.put(RenderBizPriceParameterKeyHolder.EARNEST_WAN, this.toWan(projPricingLeasePrice.getEarnestMoney()));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.INTEREST_RATE, NumberUtil.div(projPricingLeasePrice.getLeaseRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        renderModelMap.put(RenderBizPriceParameterKeyHolder.RATE_TYPE, Optional.ofNullable(RateType.of(projPricingLeasePrice.getRateType())).map(RateType::display).orElse(""));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.RENT_CALCULATOR_TYPE, Optional.ofNullable(RepayCalcType.find(projPricingLeasePrice.getRentalCalcType())).map(RepayCalcType::display).orElse(""));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.CONSULTING_FEE_WAN, this.toWan(projPricingLeasePrice.getConsultingFee()));
        renderModelMap.put(RenderBizPriceParameterKeyHolder.NOMINAL_PRICE_WAN, this.toWan(projPricingLeasePrice.getNominalPrice()));
        RepayRateEnum repayRateEnum = RepayRateEnum.of(projPricingLeasePrice.getRepayRate());
        if (repayRateEnum == RepayRateEnum.LRREGULAR || repayRateEnum == RepayRateEnum.NON_STAGES) {
            renderModelMap.put(RenderBizPriceParameterKeyHolder.REPAY_RATE, repayRateEnum.display);
        } else {
            Integer n = this.calculateN(projPricingLeasePrice.getRepayRate(), projPricingLeasePrice.getLeaseMonthCount(), projPricingLeasePrice.getRepayTimesTotal());
            renderModelMap.put(RenderBizPriceParameterKeyHolder.REPAY_RATE, Optional.ofNullable(n).map(i -> "T+" + i).orElse("T+_"));
        }
        sw.stop();

        // 渲染并生成完整的尽调报告
        sw.start("业务定价报告 主模板渲染");
        XWPFTemplate template = XWPFTemplate.compile(ProjPricingJDReportZLRender.class.getResourceAsStream(templatePath), config).render(renderModelMap);
        template.writeAndClose(outputStream);
        sw.stop();

        log.info("生成业务定价报告耗时: {}", sw.prettyPrint(TimeUnit.SECONDS));

        return projPricingBaseInfo.getProjName() + "-业务定价报告" + GlobalConstants.OFFICE_WORD_SUFFIX;
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
