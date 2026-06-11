package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projreview;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowUserApiService;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.dao.NodeBackRecordMapper;
import cn.zswltech.flow.core.domain.entity.NodeBackRecord;
import cn.zswltech.flow.core.domain.req.SetTaskApproverReq;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.flow.form.ChooseApproveAuthDTO;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.ftp.oldftp.bo.FtpQuarterlyBasePricingBO;
import cn.zswltech.mithras.ftp.newftp.enums.*;
import cn.zswltech.mithras.ftp.newftp.enums.TermRange;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.convert.projpricing.ProjPricingBaseInfoConverter;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.projectprocess.enums.projpricing.RegionalClassify;
import cn.zswltech.mithras.customer.enums.client.CustomerEntityClassify;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.PricingApproveAuthEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalDivisionEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingCashFlowPlanMapper;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingLeasePriceMapper;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.bo.DailyDiscountRateCalcResultBO;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.foundation.bo.*;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.service.FtpService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.*;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/5/18
 * @description
 */
@Component
@Slf4j
public class SetPricingChooseApproveAuthHandler implements DynamicFormHandler {
    private static final String PRICING_APPROVE_AUTH_KEY = "pricingApproveAuth";
    private static final String IS_PRICE_COMMITTEE_KEY = "isPriceCommittee";
    private static final String COMMITTEE_APPROVAL_USER_ID_LIST = "committeeApprovalUserIdList";

    @Resource
    private FlowUserApiService flowUserApiService;
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private NodeBackRecordMapper nodeBackRecordMapper;
    @Resource
    private ProjPricingBaseInfoService pricingBaseInfoService;
    @Resource
    private ProjPricingPriceService pricingPriceService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private ProjPricingLeasePriceMapper pricingLeasePriceMapper;
    @Resource
    private ProjPricingCashFlowPlanMapper pricingCashFlowPlanMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private FtpService ftpService;
    @Resource
    private ProjPricingBaseInfoConverter pricingBaseInfoConverter;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (ObjectUtil.isEmpty(formMap.get(getType().name()))) {
            throw new MithrasException("审批权限不能为空");
        }
        ChooseApproveAuthDTO req = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), ChooseApproveAuthDTO.class);
        Assert.notBlank(req.getApproveAuth(), () -> MithrasException.newException("审批权限不能为空"));
        if (Objects.equals(req.getApproveAuth(), PricingApproveAuthEnum.priceCommittee.name())) {
            Assert.notEmpty(req.getApprovalUserIdList(), () -> MithrasException.newException("定价委员会审批人不能为空"));
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        ChooseApproveAuthDTO chooseApproveAuthDTO = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), ChooseApproveAuthDTO.class);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(PRICING_APPROVE_AUTH_KEY, chooseApproveAuthDTO.getApproveAuth());
        PricingApproveAuthEnum pricingApproveAuthEnum = PricingApproveAuthEnum.find(chooseApproveAuthDTO.getApproveAuth());
        if (Objects.isNull(pricingApproveAuthEnum)) {
            throw new MithrasException("非法的审批权限类型");
        }
        switch (pricingApproveAuthEnum) {
            case priceCommittee: {
                paramMap.put(IS_PRICE_COMMITTEE_KEY, true);
                paramMap.put(COMMITTEE_APPROVAL_USER_ID_LIST, JSONUtil.toJsonStr(chooseApproveAuthDTO.getApprovalUserIdList()));
                // 如果是定价委员会，需要指定下一个节点的审批人
                SetTaskApproverReq flowReq = new SetTaskApproverReq();
                flowReq.setProcessInstanceId(taskResp.getProcessInstanceId());
                flowReq.setApproverIdList(chooseApproveAuthDTO.getApprovalUserIdList());
                flowReq.setActivityId("userTask_priceCommittee");
                flowUserApiService.setTaskApprover(flowReq);
                break;
            }
            case bizDivisionLeader:
            case chiefFinancialOfficer: {
                paramMap.put(IS_PRICE_COMMITTEE_KEY, false);
            }
        }
        flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), paramMap);
        // 重要！！！此处需要清理退回记录，不然框架层面会发现有退回记录，将直接进行跳转动作，不再经过正常的网关，无法确保走正确的分支流程
        this.clearNodeBackRecord(taskResp.getProcessInstanceId());
        // 保存业务系统的流程标签
        bizProcessDataService.recordAuthLevel(taskResp.getProcessInstanceId(), pricingApproveAuthEnum.getLevel());
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        ChooseApproveAuthDTO chooseApproveAuthDTO = new ChooseApproveAuthDTO();
        Map<String, Object> paramMap = flowVariableApiService.getVariables(rsp.getProcessInstanceId(), Arrays.asList(PRICING_APPROVE_AUTH_KEY, COMMITTEE_APPROVAL_USER_ID_LIST));
        if (Objects.nonNull(paramMap.get(PRICING_APPROVE_AUTH_KEY))) {
//            chooseApproveAuthDTO.setCanChoose(Boolean.FALSE);
            // 退回后允许重新选择
            chooseApproveAuthDTO.setCanChoose(Boolean.TRUE);
            chooseApproveAuthDTO.setApproveAuth(paramMap.get(PRICING_APPROVE_AUTH_KEY).toString());
        } else {
            chooseApproveAuthDTO.setCanChoose(Boolean.TRUE);
        }
        if (Objects.nonNull(paramMap.get(COMMITTEE_APPROVAL_USER_ID_LIST))) {
            List<String> ids = JSONUtil.toList(paramMap.get(COMMITTEE_APPROVAL_USER_ID_LIST).toString(), String.class);
            chooseApproveAuthDTO.setApprovalUserIdList(ids);
        } else {
            // 初始化为定价委员会委员
            List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.pricingcommitteemember.name());
            if (CollectionUtil.isNotEmpty(userIds)) {
                chooseApproveAuthDTO.setApprovalUserIdList(userIds.stream().map(String::valueOf).collect(Collectors.toList()));
            }
        }
        String opinion = buildReviewOpinion(rsp);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(this.getType().name(), chooseApproveAuthDTO);
        resultMap.put("opinion", opinion);
        rsp.setDynamicFormData(resultMap);
    }

    private String buildReviewOpinion(TaskDetailRSP rsp) {
        ProjPricingBaseInfo projPricingBaseInfo = pricingBaseInfoService.getById(rsp.getBusinessKey());
        ProjPricingPriceDetailRSP priceDetail = pricingPriceService.detail(Long.valueOf(rsp.getBusinessKey()));
        if (Objects.isNull(priceDetail.getLeasePriceDetailRSP()) && Objects.isNull(priceDetail.getFactoringPriceDetailRSP()) && Objects.isNull(priceDetail.getAocPriceDetailRSP())) {
            throw new MithrasException("报价方案不存在");
        }
        //查询客户信息
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoService.detail(projPricingBaseInfo.getClientId(), null);
        if (Objects.isNull(corpCommerceInfo)) {
            throw new MithrasException("客户信息不存在");
        }
        Map<String, Object> renderMap = new HashMap<>(32);
        FtpQuarterlyBasePricingBO ftpQuarterlyBasePricingBO = new FtpQuarterlyBasePricingBO();
        TermRange term = getTerm(priceDetail.getMonthCount());
        renderMap.put(RenderParameterKeyHolder.TERM, term.getProjReviewEarningRateDisplay());
        ftpQuarterlyBasePricingBO.setTermRange(term.name());
//        if (StrUtil.equalsAny(corpCommerceInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name())) {
//            //当风控行业分类=公用事业类、民生消费类、旅游行业时按区域划分进行区分，①若区域划分为「浙江地区」则展示为“浙江地区”②若地区分类为「一类地区」则展示为“鼓励支持类地区”；若地区分类为「二类地区」则展示为“其他地区”；
//            renderMap.put(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY, ProjRegionalDivisionEnum.getProjReviewEarningsString(ProjRegionalDivisionEnum.of(projPricingBaseInfo.getRegionalDivision())));
//        } else {
//            //当风控行业分类≠公用事业类、民生消费类、旅游行业时按地区分类进行区分，①若地区分类为「浙江地区」则展示为“浙江地区”；②若地区分类为「鼓励支持类」则展示为“鼓励支持类地区”；③若地区分类为「适度支持类」或「谨慎支持类」则展示为“其他地区”。
//            renderMap.put(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY, RegionalClassify.getProjRegionalClassify(projPricingBaseInfo.getRegionalProjectClassify()).display());
//        }
        RegionalClassify regionalClassify;
        if (StrUtil.equalsAny(projPricingBaseInfo.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_STATE_OWNED_INDUSTRY.name(), FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES.name(), FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION.name())) {
            regionalClassify = ProjRegionalDivisionEnum.getProjReviewEarningsRegionalClassify(ProjRegionalDivisionEnum.of(projPricingBaseInfo.getRegionalDivision()));
        } else {
            regionalClassify = RegionalClassify.getProjRegionalClassify(projPricingBaseInfo.getRegionalProjectClassify());
        }
        if (Objects.isNull(regionalClassify)) {
            log.error("无法确定FTP地区分类，使用其他地区进行兜底[{}]", projPricingBaseInfo.getProjCode());
            regionalClassify = RegionalClassify.OTHER;
        }
        renderMap.put(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY, regionalClassify.display());
//        if ("浙江地区".equals(renderMap.get(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY))) {
//            ftpQuarterlyBasePricingBO.setRegionalClassify("ZHEJIANG");
//        } else if ("鼓励支持类地区".equals(renderMap.get(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY))) {
//            ftpQuarterlyBasePricingBO.setRegionalClassify("ENCOURAGE");
//        } else {
//            ftpQuarterlyBasePricingBO.setRegionalClassify("OTHER");
//        }
        ftpQuarterlyBasePricingBO.setRegionalClassify(regionalClassify.name());

        //获取项目承租人
        List<Long> projReviewClientIds = getProjReviewClientIds(projPricingBaseInfo);
        Set<String> enterpriseNatureSet = corpCommerceInfoService.list(projReviewClientIds).stream().map(CorpCommerceInfo::getEnterpriseNature).collect(Collectors.toSet());
        //企业性质
        CustomerEntityClassify customerEntityClassify = EnterpriseNatureEnum.changeProjReviewDisplay(enterpriseNatureSet);
        if (Objects.isNull(customerEntityClassify)) {
            log.error("无法确定FTP客户主体分类，使用其他进行兜底[{}]", projPricingBaseInfo.getProjCode());
            customerEntityClassify = CustomerEntityClassify.OTHER;
        }
        renderMap.put(RenderParameterKeyHolder.ENTERPRISE_NATURE, customerEntityClassify.display());
        ftpQuarterlyBasePricingBO.setCustomerEntityClassify(customerEntityClassify.name());
        //资产行业分类
        ProjectClassify projectClassify = ProjectClassify.find(projPricingBaseInfo.getProjectClassify());
        renderMap.put(RenderParameterKeyHolder.PROJECT_CLASSIFY, Optional.ofNullable(projectClassify).map(ProjectClassify::display).orElse(""));
        ftpQuarterlyBasePricingBO.setAssetIndustryClassify(Optional.ofNullable(AssetIndustryClassify.getByProjectClassify(projectClassify)).map(AssetIndustryClassify::name).orElse(""));
        //季度项目最低收益率
        //这里王振说协同取最优
        if (ProjectClassify.INTRA_GROUP_COLLABORATION.name().equals(projPricingBaseInfo.getProjectClassify())) {
            ftpQuarterlyBasePricingBO.setAssetIndustryClassify(AssetIndustryClassify.ENCOURAGE_INTERVENTION.name());
            ftpQuarterlyBasePricingBO.setRegionalClassify(RegionalClassify.ZHEJIANG.name());
            ftpQuarterlyBasePricingBO.setCustomerEntityClassify(CustomerEntityClassify.CUSTOMER_LISTED_STATE_OWNED.name());
        } else if (ProjectClassify.CONSTRUCTION_MACHINERY.name().equals(projPricingBaseInfo.getProjectClassify())) {
            ftpQuarterlyBasePricingBO.setAssetIndustryClassify(AssetIndustryClassify.ENCOURAGE_INTERVENTION.name());
        }
        // 根据评估主体是否关联方决定季度最低收益率的取值来源
        CorpCommerceInfo subjectCorpCommerceInfo = corpCommerceInfoService.detail(projPricingBaseInfo.getEvaluationSubjectId(), null);
        if (Objects.nonNull(subjectCorpCommerceInfo) && Objects.equals(subjectCorpCommerceInfo.getIsRelated(), YesOrNoNumberEnum.YES.getCode())) {
            ftpQuarterlyBasePricingBO.setRelated(true);
            ftpQuarterlyBasePricingBO.setRelatedTermRange(RelatedTermRange.convertFromMonthCount(priceDetail.getMonthCount()));
            renderMap.put(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD, LongUtil.tenThousand2Dollar(String.valueOf(ftpService.getFtpQuarterlyBasePricing(ftpQuarterlyBasePricingBO))).setScale(2, RoundingMode.HALF_UP).toPlainString());
        } else {
            renderMap.put(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD, LongUtil.tenThousand2Dollar(String.valueOf(ftpService.getFtpQuarterlyBasePricing(ftpQuarterlyBasePricingBO))).setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
        BigDecimal ratePercent = LongUtil.tenThousand2Dollar(String.valueOf(priceDetail.getRatePercent())).setScale(2, RoundingMode.HALF_UP);
        renderMap.put(RenderParameterKeyHolder.RATE_PERCENT, ratePercent.toPlainString());
        //折现率
        BigDecimal discountRate = getCashFlowBOS(projPricingBaseInfo);
        renderMap.put(RenderParameterKeyHolder.DISCOUNT_RATE, discountRate);
        //构建参数
        BigDecimal cashFtp = null;
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listEffectPaymentByClientId(projPricingBaseInfo.getClientId());
        Set<Long> contractIdList = null;
        if (!CollectionUtil.isEmpty(paymentBaseInfoList)) {
            contractIdList = paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet());
        }
        try {
            cashFtp = LongUtil.tenThousand2Dollar(String.valueOf(ftpService.getCashFtp(getCashFtpInfluenceBO(projPricingBaseInfo, priceDetail, corpCommerceInfo, ftpQuarterlyBasePricingBO.getRegionalClassify()), contractIdList))).setScale(2, RoundingMode.HALF_UP);
            renderMap.put(RenderParameterKeyHolder.CASH_FTP, cashFtp.toPlainString());
            renderMap.put(RenderParameterKeyHolder.RATE_PERCENT_COMPARE_COMPARE_CASH_FTP, discountRate.compareTo(cashFtp) > 0 ? "高" : ratePercent.compareTo(cashFtp) < 0 ? "低" : "等");
        } catch (Exception e) {
            log.error("生成项目收益率审查意见中的FTP数据异常", e);
        }
        ProjectBizTypeBO projectBizTypeBO = new ProjectBizTypeBO();
        projectBizTypeBO.setProjectBizType(projPricingBaseInfo.getBizType());
        if (StrUtil.isNotBlank(projPricingBaseInfo.getLeaseTypes())) {
            projectBizTypeBO.setLeaseTypeList(JSONUtil.toList(projPricingBaseInfo.getLeaseTypes(), String.class));
        } else {
            if (ProjectBizType.ZL.name().equals(projPricingBaseInfo.getBizType()) || ProjectBizType.ZZ.name().equals(projPricingBaseInfo.getBizType())) {
                throw new MithrasException("请先补全租赁类型");
            }
        }
        if (Objects.nonNull(priceDetail.getIrr())) {
            renderMap.put(RenderParameterKeyHolder.IRR, NumberUtil.div(priceDetail.getIrr().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
        if (Objects.nonNull(priceDetail.getApplyCreditAmount()) && Objects.nonNull(priceDetail.getEarnestMoney())) {
            BigDecimal b1 = BigDecimal.valueOf(priceDetail.getApplyCreditAmount());
            BigDecimal b2 = BigDecimal.valueOf(priceDetail.getEarnestMoney());
            BigDecimal r = b2.divide(b1, 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            renderMap.put(RenderParameterKeyHolder.EARNEST_RATE, r.toPlainString());
        }
        Object irr = renderMap.get(RenderParameterKeyHolder.IRR);
        Object requireIrr = renderMap.get(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD);
        String opinion;
        if(irr != null && requireIrr != null && new BigDecimal(String.valueOf(irr)).compareTo(new BigDecimal(String.valueOf(requireIrr))) < 0){
            if (ftpQuarterlyBasePricingBO.isRelated()) {
                opinion = String.format("项目收益率测算为【%s】%%，低于【集团控股公司项目】【%s】期报价【%s】%%的要求，需上报定价委员会审议。",
                        renderMap.get(RenderParameterKeyHolder.IRR),
                        ftpQuarterlyBasePricingBO.getRelatedTermRange().getDisplay(),
                        renderMap.get(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD)
                );
            } else {
                opinion = String.format("项目收益率测算为【%s】%%，低于【%s】【%s】【%s】【%s】期报价【%s】%%的要求，需上报定价委员会审议。",
                        renderMap.get(RenderParameterKeyHolder.IRR),
                        renderMap.get(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY),
                        renderMap.get(RenderParameterKeyHolder.ENTERPRISE_NATURE),
                        renderMap.get(RenderParameterKeyHolder.PROJECT_CLASSIFY),
                        renderMap.get(RenderParameterKeyHolder.TERM),
                        renderMap.get(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD)
                );
            }
        }else{
            if (ftpQuarterlyBasePricingBO.isRelated()) {
                opinion = String.format("项目收益率测算为【%s】%%，符合【集团控股公司项目】【%s】期报价【%s】%%的要求，项目合同利率【%s】%%，折现率【%s %%】【%s】于同期FTP利率【%s】%%，保证金率为【%s】%%，均符合报价结构要求。在维持项目收益率不变的情况下，允许调整报价结构，但需确保各年创利均为正数。",
                        renderMap.get(RenderParameterKeyHolder.IRR),
                        ftpQuarterlyBasePricingBO.getRelatedTermRange().getDisplay(),
                        renderMap.get(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD),
                        renderMap.get(RenderParameterKeyHolder.RATE_PERCENT),
                        renderMap.get(RenderParameterKeyHolder.DISCOUNT_RATE),
                        renderMap.get(RenderParameterKeyHolder.RATE_PERCENT_COMPARE_COMPARE_CASH_FTP),
                        renderMap.get(RenderParameterKeyHolder.CASH_FTP),
                        renderMap.get(RenderParameterKeyHolder.EARNEST_RATE)
                );
            } else {
                opinion = String.format("项目收益率测算为【%s】%%，符合【%s】【%s】【%s】【%s】期报价【%s】%%的要求，项目合同利率【%s】%%，折现率【%s %%】【%s】于同期FTP利率【%s】%%，保证金率为【%s】%%，均符合报价结构要求。在维持项目收益率不变的情况下，允许调整报价结构，但需确保各年创利均为正数。",
                        renderMap.get(RenderParameterKeyHolder.IRR),
                        renderMap.get(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY),
                        renderMap.get(RenderParameterKeyHolder.ENTERPRISE_NATURE),
                        renderMap.get(RenderParameterKeyHolder.PROJECT_CLASSIFY),
                        renderMap.get(RenderParameterKeyHolder.TERM),
                        renderMap.get(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD),
                        renderMap.get(RenderParameterKeyHolder.RATE_PERCENT),
                        renderMap.get(RenderParameterKeyHolder.DISCOUNT_RATE),
                        renderMap.get(RenderParameterKeyHolder.RATE_PERCENT_COMPARE_COMPARE_CASH_FTP),
                        renderMap.get(RenderParameterKeyHolder.CASH_FTP),
                        renderMap.get(RenderParameterKeyHolder.EARNEST_RATE)
                );
            }
        }
        return opinion;

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_pricingChooseApproveAuth;
    }

    private List<Long> getProjReviewClientIds(ProjPricingBaseInfo projPricingBaseInfo) {
        ProjectBizType projectBizType = ProjectBizType.of(projPricingBaseInfo.getBizType());
        List<Long> clientIds = new ArrayList<>();
        switch (projectBizType) {
            case ZZ:
            case ZL:
                clientIds = JSONArray.parseArray(projPricingBaseInfo.getLesseeInfo()).toJavaList(ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList());
                break;
            case BL:
            case ZR:
                clientIds = JSONArray.parseArray(projPricingBaseInfo.getCreditorInfo()).toJavaList(ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList());
                break;
        }
        if (ObjectUtil.isNotEmpty(projPricingBaseInfo.getGuaranteeInfo())) {
            clientIds.addAll(JSONArray.parseArray(projPricingBaseInfo.getGuaranteeInfo()).toJavaList(ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        return clientIds;
    }

    private void clearNodeBackRecord(String processInstanceId) {
        Example example = new Example(NodeBackRecord.class);
        example.createCriteria()
                .andEqualTo("status", YesOrNoNumberEnum.YES.getCode())
                .andEqualTo("sinkTaskActivityId", "userTask_financeOfficer_1")
                .andEqualTo("processInstanceId", processInstanceId);
        NodeBackRecord entity = new NodeBackRecord();
        entity.setStatus(YesOrNoNumberEnum.NO.getCode());
        nodeBackRecordMapper.updateByConditionSelective(entity, example);
    }

    private TermRange getTerm(Integer month) {
        if (month <= 12) {
            return TermRange.ONE_YEAR;
        } else if (month > 36) {
            return TermRange.MORE_THAN_THREE_YEARS;
        } else {
            return TermRange.ONE_TO_THREE_YEARS;
        }
    }

    private CashFtpInfluenceBO getCashFtpInfluenceBO(ProjPricingBaseInfo projPricingBaseInfo, ProjPricingPriceDetailRSP priceDetail, CorpCommerceInfo corpCommerceInfo, String regionalClassify) {
        CashFtpInfluenceBO cashFtpInfluenceBO = new CashFtpInfluenceBO();
        ProjPricingBaseInfoDetailRSP projPricingBaseInfoDetailRSP = pricingBaseInfoConverter.entityToDetailRSP(projPricingBaseInfo);
        List<ClientInfo> lesseeInfo = projPricingBaseInfoDetailRSP.getLesseeInfo();
        List<ClientInfo> guaranteeInfo = projPricingBaseInfoDetailRSP.getGuaranteeInfo();
        cashFtpInfluenceBO.setTenantId(lesseeInfo.get(0).getClientId());
        cashFtpInfluenceBO.setGuarantorIdList(guaranteeInfo.stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        cashFtpInfluenceBO.setJudgePledge(false);
        cashFtpInfluenceBO.setBizType(projPricingBaseInfo.getBizType());
        cashFtpInfluenceBO.setContractMonthCount(priceDetail.getMonthCount());
        cashFtpInfluenceBO.setRiskControlIndustryClassify(corpCommerceInfo.getRiskControlIndustryClassify());
        cashFtpInfluenceBO.setEnterpriseNature(corpCommerceInfo.getEnterpriseNature());
        cashFtpInfluenceBO.setAssetIndustryClassify(projPricingBaseInfo.getProjectClassify());
        cashFtpInfluenceBO.setRegionClassify(projPricingBaseInfo.getRegionalProjectClassify());
        cashFtpInfluenceBO.setTargetDate(LocalDate.now());
        cashFtpInfluenceBO.setZhejiang("ZHEJIANG".equals(regionalClassify));
        // 如果项目评审-行业分类是特定的两种类型则特殊处理
        if (Objects.equals(projPricingBaseInfo.getProjectClassify(), ProjectClassify.CONSTRUCTION_MACHINERY.name())) {
//            cashFtpInfluenceBO.setRiskControlIndustryClassify(RiskControlIndustryClassify.ENGINEERING_MACHINERY.name());
            cashFtpInfluenceBO.setAssetIndustryClassify(null);
        } else if (Objects.equals(projPricingBaseInfo.getProjectClassify(), ProjectClassify.INTRA_GROUP_COLLABORATION.name())) {
            cashFtpInfluenceBO.setRiskControlIndustryClassify(RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name());
            cashFtpInfluenceBO.setAssetIndustryClassify(null);
        }
        cashFtpInfluenceBO.setFtpIndustryCategory(projPricingBaseInfo.getFtpIndustryCategory());
        cashFtpInfluenceBO.setRegionalDivision(projPricingBaseInfo.getRegionalDivision());
        cashFtpInfluenceBO.setEvaluationSubjectId(projPricingBaseInfo.getEvaluationSubjectId());
        cashFtpInfluenceBO.setProjectManageLevel(projPricingBaseInfo.getProjectManageLevel());
        cashFtpInfluenceBO.setIsAAA(projPricingBaseInfo.getIsAAA());
        return cashFtpInfluenceBO;
    }

    private BigDecimal getCashFlowBOS(ProjPricingBaseInfo pricingBaseInfo) {
        //获取现金流计划
        LambdaQueryWrapper<ProjPricingCashFlowPlan> PlanQueryWrapper = Wrappers.<ProjPricingCashFlowPlan>lambdaQuery()
                .eq(Objects.nonNull(pricingBaseInfo.getId()), ProjPricingCashFlowPlan::getProjectId, pricingBaseInfo.getId());
        List<ProjPricingCashFlowPlan> projPricingBaseInfoList = pricingCashFlowPlanMapper.selectList(PlanQueryWrapper);
        //获取现金流计划
        Wrapper<ProjPricingLeasePrice> priceQueryWrapper = Wrappers.<ProjPricingLeasePrice>lambdaQuery()
                .eq(Objects.nonNull(pricingBaseInfo.getId()), ProjPricingLeasePrice::getProjectId, pricingBaseInfo.getId());
        ProjPricingLeasePrice projPricingLeasePrice = pricingLeasePriceMapper.selectOne(priceQueryWrapper);

        List<CashFlowBO> cashFlowBOList = projPricingBaseInfoList.stream().map(item -> {
            CashFlowBO cashFlowBO = new CashFlowBO()
                    .setCashFlowDate(item.getCashFlowDate())//日期
                    .setCashFlowPhase(item.getCashFlowPhase())//期项
                    .setRemainingPrincipal(item.getRemainingPrincipal())//剩余本金
                    .setRent(item.getRent())//租金
                    .setPrincipal(item.getPrincipal())//本金
                    .setInterest(item.getInterest());//利息

            // 最后一个期限现金流金额为：租金-保证金
            if (Objects.nonNull(projPricingLeasePrice.getRepayTimesTotal())
                    && Objects.nonNull(item.getCashFlowPhase())
                    && projPricingLeasePrice.getRepayTimesTotal().equals(item.getCashFlowPhase())) {
                cashFlowBO.setCashFlowAmount(item.getRent() - projPricingLeasePrice.getEarnestMoney());
            } else {
                cashFlowBO.setCashFlowAmount(item.getRent());
            }
            return cashFlowBO;
        }).collect(Collectors.toList());

        //0期的现金流：-1 * 授信金额 + 首期租金 + 保证金 + 手续费 + 首期利息
        Long cashFlowAmount = -1 * projPricingLeasePrice.getApplyCreditAmount()
                + Optional.ofNullable(projPricingLeasePrice.getDownPayment()).orElse(0L)
                + Optional.ofNullable(projPricingLeasePrice.getEarnestMoney()).orElse(0L)
                + Optional.ofNullable(projPricingLeasePrice.getCommission()).orElse(0L)
                + Optional.ofNullable(projPricingLeasePrice.getFirstInstallmentInterest()).orElse(0L);
        //方法需要0期限的数据
        CashFlowBO cashFlowBO = new CashFlowBO()
                .setCashFlowAmount(cashFlowAmount)
                .setCashFlowDate(projPricingLeasePrice.getPlannedStartingDate())
                .setCashFlowPhase(0);
        cashFlowBOList.add(cashFlowBO);
        //日折现率
        DailyDiscountRateCalcResultBO dailyDiscountRateCalcResultBO = FinancialUtil.calculateDailyDiscountRate(cashFlowBOList);
        //转为年折现率
        return NumberUtil.mul(dailyDiscountRateCalcResultBO.getDailyDiscountRate(), BigDecimal.valueOf(100), BigDecimal.valueOf(365)).setScale(2, RoundingMode.HALF_UP);
    }
    
    private static class RenderParameterKeyHolder {
        // 收益率
        private static final String IRR = "irr";
        //地区分类
        private static final String REGIONAL_PROJECT_CLASSIFY = "regionalProjectClassify";
        // 企业性质
        public static final String ENTERPRISE_NATURE = "enterpriseNature";
        //资产行业分类
        private static final String PROJECT_CLASSIFY = "projectClassify";
        //期限 1年、1-3年、3年以上
        private static final String TERM = "term";
        //季度项目最低收益率
        private static final String QUARTERLY_MINIMUM_YIELD = "quarterlyMinimumYield";
        //租金利率
        private static final String RATE_PERCENT = "ratePercent";
        //折现率
        public static final String DISCOUNT_RATE = "discountRate";
        //租金利率与月度 FTP 指导定价前后两者进行比较，前者大则为高，以此类推
        private static final String RATE_PERCENT_COMPARE_COMPARE_CASH_FTP = "ratePercentCompareCashFtp";
        //月度 FTP 指导定价
        private static final String CASH_FTP = "cashFtp";
        // 保证金率，保证金/申请授信 * 100%，保留2位小数
        private static final String EARNEST_RATE = "earnestRate";

    }
}
