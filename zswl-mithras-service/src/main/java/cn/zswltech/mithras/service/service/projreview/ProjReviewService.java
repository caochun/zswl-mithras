package cn.zswltech.mithras.service.service.projreview;
import cn.zswltech.mithras.customer.domain.enums.GovernmentSubjectItemType;
import cn.zswltech.mithras.customer.domain.enums.SubjectItemType;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.ClientMaterialsLackInfoRSP;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckResultDetail;
import cn.zswltech.mithras.dto.projreview.ProjReviewRatingCheckRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.factory.model.RatingAmount;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.service.RatingAmountService;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.service.config.ProjReviewConfigProperties;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewCashFlowQuotationProposalConverter;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.customer.domain.enums.app.VisitPhaseStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitRecordStatus;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.customer.domain.enums.client.CorporationClientMaterialSubTypeEnum;
import cn.zswltech.mithras.customer.domain.enums.client.NormalClientMaterialSubTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projlifecycle.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.service.enums.projreview.MeetMinuteStatuesEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpSubjectItemMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowQuotationProposal;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.ProjClientRoleService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseReviewService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.projpricing.impl.ProjPricingVersionServiceImpl;
import cn.zswltech.mithras.service.service.lib.projreview.impl.ProjReviewVersionServiceImpl;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.impl.ProjReviewStateMachine;
import cn.zswltech.mithras.service.service.projlifecycle.ProjectLifecycleEventService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.isNotEmpty;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.service.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.*;
import static cn.zswltech.mithras.service.others.Util.missRequiredParam;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjReviewService implements FlowEndEventProcessor {
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewVersionServiceImpl projReviewVersionService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private ProjReviewStateMachine stateMachine;
    @Resource
    private ProjectLifecycleEventService projectLifecycleEventService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewPriceService priceService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ProjReviewBaseInfoService baseInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private LeaseReviewService leaseReviewService;
    @Resource
    private CorpSubjectItemMapper corpSubjectItemMapper;
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjPricingPriceService pricingPriceService;
    @Resource
    private ProjPricingVersionServiceImpl projPricingVersionService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewConfigProperties reviewConfigProperties;
    @Resource
    private VisitRecordMapper visitRecordMapper;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private RatingAmountService ratingAmountService;
    @Resource
    private ProjReviewCashFlowQuotationProposalService projReviewCashFlowQuotationProposalService;
    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;

    public boolean projReviewIsWaterTransport(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        return Objects.nonNull(projReviewBaseInfo) && Objects.equals(projReviewBaseInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.WATER_TRANSPORTATION.name());
    }

    public List<CorpSubjectItemCheckResultDetail> listClientSubjectItemCheckResult(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        List<CorpSubjectItemCheckResultDetail> checkResultList = new LinkedList<>();
        // TODO 数据量大的时候会有性能问题，可以改造为多线程加工
        if (StrUtil.isNotBlank(projReviewBaseInfo.getLesseeInfo())) {
            List<ClientInfo> lesseeList = JSONUtil.toList(projReviewBaseInfo.getLesseeInfo(), ClientInfo.class);
            checkResultList.addAll(lesseeList.stream().map(e -> getCheckResult(e.getClientId(), "承租人")).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(projReviewBaseInfo.getCreditorInfo())) {
            List<ClientInfo> creditorList = JSONUtil.toList(projReviewBaseInfo.getCreditorInfo(), ClientInfo.class);
            checkResultList.addAll(creditorList.stream().map(e -> getCheckResult(e.getClientId(), "债权人")).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(projReviewBaseInfo.getDebtorInfo())) {
            List<ClientInfo> debtorList = JSONUtil.toList(projReviewBaseInfo.getDebtorInfo(), ClientInfo.class);
            checkResultList.addAll(debtorList.stream().map(e -> getCheckResult(e.getClientId(), "债务人")).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(projReviewBaseInfo.getGuaranteeInfo())) {
            List<ClientInfo> guaranteeList = JSONUtil.toList(projReviewBaseInfo.getGuaranteeInfo(), ClientInfo.class);
            checkResultList.addAll(guaranteeList.stream().map(e -> getCheckResult(e.getClientId(), "担保人")).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        // 补全财报不完整原因
        if (StrUtil.isNotBlank(projReviewBaseInfo.getSubjectItemCheckReason())) {
            List<ProjReviewBaseInfo.SubjectReasonData> list = JSONUtil.toList(projReviewBaseInfo.getSubjectItemCheckReason(), ProjReviewBaseInfo.SubjectReasonData.class);
            Map<Long, ProjReviewBaseInfo.SubjectReasonData> map = Optional.ofNullable(list).map(e -> e.stream().collect(Collectors.toMap(ProjReviewBaseInfo.SubjectReasonData::getClientId, v -> v, (v1, v2) -> v1))).orElse(Collections.emptyMap());
            for (CorpSubjectItemCheckResultDetail detail : checkResultList) {
                ProjReviewBaseInfo.SubjectReasonData subjectReasonData = map.get(detail.getClientId());
                if (Objects.nonNull(subjectReasonData) && Objects.equals(detail.getCheckResult(), YesOrNoNumberEnum.NO.getCode())) {
                    detail.setReason(subjectReasonData.getReason());
                }
            }
        }
        return checkResultList;
    }

    private CorpSubjectItemCheckResultDetail getCheckResult(Long clientId, String roleCodeName) {
        Client client = clientService.getById(clientId);
        if (Objects.isNull(client)) {
            return null;
        }
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(ClientBaseModel::getClientId, clientId).last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(corpCommerceInfo) || Objects.isNull(corpCommerceInfo.getEstablishDate())) {
            return null;
        }
        // 查询客户有哪些年份、季度的财务报表
        LambdaQueryWrapper<CorpSubjectItem> query = Wrappers.lambdaQuery();
        query.select(CorpSubjectItem::getYear, CorpSubjectItem::getQuarter, CorpSubjectItem::getSubjectType);
        query.eq(CorpSubjectItem::getClientId, clientId);
        query.groupBy(CorpSubjectItem::getYear, CorpSubjectItem::getQuarter, CorpSubjectItem::getSubjectType);
        List<CorpSubjectItem> corpSubjectItemList = corpSubjectItemMapper.selectList(query);
        Set<String> set = Optional.ofNullable(corpSubjectItemList)
                .map(e -> e.stream().map(ele -> ele.getYear() + "-" + ele.getQuarter() + "-" + ele.getSubjectType()).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
        // 处理返回数据
        CorpSubjectItemCheckResultDetail rsp = new CorpSubjectItemCheckResultDetail();
        rsp.setClientId(clientId);
        rsp.setClientName(client.getClientName());
        rsp.setOrgType(corpCommerceInfo.getOrgType());
        rsp.setProjectRoleName(roleCodeName);
        // 处理年报和季报
        List<CorpSubjectItemCheckResultDetail.CheckResultData> checkResultDataList = new LinkedList<>();
        int currentYear = LocalDate.now().getYear();
        int establishYear = corpCommerceInfo.getEstablishDate().getYear();
        int currentQuarter = DateUtil.ensureQuarter(LocalDate.now().getMonthValue());
        int establishQuarter = DateUtil.ensureQuarter(corpCommerceInfo.getEstablishDate().getMonthValue());
        // 处理年报
        if (establishYear < currentYear) {
            int i = Math.max(currentYear - 3, establishYear);
            while (i < currentYear) {
                CorpSubjectItemCheckResultDetail.CheckResultData checkResultData = new CorpSubjectItemCheckResultDetail.CheckResultData();
                if (Objects.equals(corpCommerceInfo.getOrgType(), "1")) {
                    checkResultData.setCapitalBalanceCheckResult(set.contains(i + "-12-" + SubjectItemType.CAPITAL_BALANCE.name()) ? 1 : 0);
                    checkResultData.setProfitCheckResult(set.contains(i + "-12-" + SubjectItemType.PROFIT.name()) ? 1 : 0);
                    checkResultData.setCashFlowCheckResult(set.contains(i + "-12-" + SubjectItemType.CASH_FLOW.name()) ? 1 : 0);
                    checkResultData.setIncomeExpendResult(-1);
                } else {
                    checkResultData.setCapitalBalanceCheckResult(set.contains(i + "-12-" + GovernmentSubjectItemType.GOV_CAPITAL_BALANCE.name()) ? 1 : 0);
                    checkResultData.setProfitCheckResult(-1);
                    checkResultData.setCashFlowCheckResult(-1);
                    checkResultData.setIncomeExpendResult(set.contains(i + "-12-" + GovernmentSubjectItemType.INCOME_EXPEND.name()) ? 1 : 0);
                }
                checkResultData.setReportPeriod(i + "年-年报");
                checkResultData.setCheckResult(this.ensureSingleCheckResult(checkResultData));
                checkResultDataList.add(checkResultData);
                i++;
            }
        }
        // 处理季报（当前非第一季度时才需要处理）
        if (currentQuarter > 1) {
            if ((establishYear < currentYear) || ((establishYear == currentYear) && (establishQuarter < currentQuarter))) {
                CorpSubjectItemCheckResultDetail.CheckResultData checkResultData = new CorpSubjectItemCheckResultDetail.CheckResultData();
                int targetQuarter = currentQuarter - 1;
                int targetQueryQuarter = targetQuarter * 3;
                if (Objects.equals(corpCommerceInfo.getOrgType(), "1")) {
                    checkResultData.setCapitalBalanceCheckResult(set.contains(currentYear + "-" + targetQueryQuarter + "-" + SubjectItemType.CAPITAL_BALANCE.name()) ? 1 : 0);
                    checkResultData.setProfitCheckResult(set.contains(currentYear + "-" + targetQueryQuarter + "-" + SubjectItemType.PROFIT.name()) ? 1 : 0);
                    checkResultData.setCashFlowCheckResult(set.contains(currentYear + "-" + targetQueryQuarter + "-" + SubjectItemType.CASH_FLOW.name()) ? 1 : 0);
                    checkResultData.setIncomeExpendResult(-1);
                } else {
                    checkResultData.setCapitalBalanceCheckResult(set.contains(currentYear + "-" + targetQueryQuarter + "-" + GovernmentSubjectItemType.GOV_CAPITAL_BALANCE.name()) ? 1 : 0);
                    checkResultData.setProfitCheckResult(-1);
                    checkResultData.setCashFlowCheckResult(-1);
                    checkResultData.setIncomeExpendResult(set.contains(currentYear + "-" + targetQueryQuarter + "-" + GovernmentSubjectItemType.INCOME_EXPEND.name()) ? 1 : 0);
                }
                checkResultData.setReportPeriod(currentYear + "年-" + targetQuarter + "季报");
                checkResultData.setCheckResult(this.ensureSingleCheckResult(checkResultData));
                checkResultDataList.add(checkResultData);
            }
        }
        rsp.setCheckResultDataList(checkResultDataList);
        rsp.setCheckResult(this.ensureCheckResult(rsp));
        return rsp;
    }

    private int ensureSingleCheckResult(CorpSubjectItemCheckResultDetail.CheckResultData checkResultData) {
        if (Objects.equals(checkResultData.getCapitalBalanceCheckResult(), YesOrNoNumberEnum.NO.getCode())) {
            return YesOrNoNumberEnum.NO.getCode();
        }
        if (Objects.equals(checkResultData.getProfitCheckResult(), YesOrNoNumberEnum.NO.getCode())) {
            return YesOrNoNumberEnum.NO.getCode();
        }
        if (Objects.equals(checkResultData.getCashFlowCheckResult(), YesOrNoNumberEnum.NO.getCode())) {
            return YesOrNoNumberEnum.NO.getCode();
        }
        if (Objects.equals(checkResultData.getIncomeExpendResult(), YesOrNoNumberEnum.NO.getCode())) {
            return YesOrNoNumberEnum.NO.getCode();
        }
        return YesOrNoNumberEnum.YES.getCode();
    }

    private int ensureCheckResult(CorpSubjectItemCheckResultDetail rsp) {
        if (CollectionUtil.isEmpty(rsp.getCheckResultDataList())) {
            return YesOrNoNumberEnum.YES.getCode();
        }
        for (CorpSubjectItemCheckResultDetail.CheckResultData checkResultData : rsp.getCheckResultDataList()) {
            if (Objects.equals(checkResultData.getCheckResult(), YesOrNoNumberEnum.NO.getCode())) {
                return YesOrNoNumberEnum.NO.getCode();
            }
        }
        return YesOrNoNumberEnum.YES.getCode();
    }


    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long projReviewId) {
        //更新部门领导
        projReviewBaseInfoService.renewLeader(projReviewId);
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        ProjReviewPriceDetailRSP detail = priceService.detail(projReviewId);
        // 发起流程，需要校验irr不得低于定价审批通过的irr
        ProjPricingBaseInfo pricingBaseInfo = projPricingBaseInfoService.getPricingByReview(baseInfo);
        if(ObjectUtil.isNotEmpty(pricingBaseInfo)) {
            ProjPricingPriceDetailRSP pricingDetail = pricingPriceService.detail(pricingBaseInfo.getId());
            if (ObjectUtil.isNotEmpty(pricingDetail.getIrr())
                    && detail.getIrr() < pricingDetail.getIrr()) {
                throw new MithrasException("IRR不允许低于定价审批时的值");
            }
        }
        if (ObjectUtil.isNotEmpty(baseInfo.getProjEstablishId())) {
            //判断项目立项是否已失效
            if (projEstablishBaseInfoMapper.selectCount(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                    .eq(ProjEstablishBaseInfo::getId, baseInfo.getProjEstablishId())
                    .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.EXPIRE.name())) > 0) {
                throw new MithrasException("此项目立项已失效！流程无法提交");
            }
        } else {
            //授信ps非失效状态
            if (SpringContextHolder.getBean(GroupCreditReviewBaseInfoService.class).count(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                    .eq(GroupCreditReviewBaseInfo::getId, baseInfo.getGroupCreditReviewId())
                    .eq(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, RecordStatus.EXPIRE.name())) > 0) {
                throw new MithrasException("此授信评审已失效！流程无法提交");
            }
        }

        // 判断客户风控行业分类，填充董事会节点准入条件
        Client client = clientService.getById(baseInfo.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
        Assert.notNull(corpCommerceInfoLib, () -> MithrasException.newException("无生效的客户工商信息数据"));
        Assert.notBlank(corpCommerceInfoLib.getRiskControlIndustryClassify(), () -> MithrasException.newException(String.format("请先至客户模块维护<%s>的“风控行业分类”，否则无法判断该项目是否需经董事会审议！", client.getClientName())));
        StartProcessReq startProcessReq = new StartProcessReq();
        String event = null;
        // 判断使用创建流程还是修改流程
        if (NEW.name().equals(baseInfo.getProjReviewStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.ProjReviewCreateFlow.name());
            event = "项目评审创建审批";
            boolean onSiteDueDiligence = reviewConfigProperties.getOnSiteDueDiligence();
            if (onSiteDueDiligence) {
                boolean checkOnsite = hasProjReviewOnSite(projReviewId);
                if (!checkOnsite) {
                    throw new MithrasException("项目评审请提供现场进调照片");
                }
            }
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.ProjReviewModifyFlow.name());
            event = "项目评审修改审批";
        }
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of("riskControlManager", Objects.nonNull(baseInfo.getRiskControlManagerId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getRiskControlManagerId())) : new ArrayList<>()),
                Pair.of("legalManagerUser", Objects.nonNull(baseInfo.getLegalManagerUserId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getLegalManagerUserId())) : new ArrayList<>()),
                Pair.of("isGroupCredit", Objects.nonNull(baseInfo.getGroupCreditReviewId())),
                Pair.of("isGroupCreditAllAllow", false),
                Pair.of(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW, false),
                Pair.of(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK, false)
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(projReviewId));
        startProcessReq.setSubModule(baseInfo.getBizType());
        startProcessReq.setProcessInstanceName(baseInfo.getProjName());
        startProcessReq.setCcUserIdList(StringUtils.isBlank(baseInfo.getProjCosponsorUserIds()) ?
                new ArrayList<>() : JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId())
                .map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());
        String projType = baseInfo.getRelationDataType() == null ? ReviewRelationDataType.PROJ_ESTABLISH.name() : baseInfo.getRelationDataType();
        Long projId = baseInfo.getProjEstablishId();
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projType)) {
            projId = baseInfo.getId();
        }
        projectLifecycleEventService.add(event, ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", projId, projType);
        stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.SUBMIT_APPROVAL, baseInfo.getProcessStatus()));

        projClientRoleService.projReviewSubmit(projReviewId);
        //同步发起租赁物流程
        if(ProjectBizType.ZL.name().equals(baseInfo.getBizType()) && StrUtil.isNotBlank(baseInfo.getLeaseTypes())) {
            List<String> types = JSONUtil.toList(baseInfo.getLeaseTypes(), String.class);
            for(String type : types){
                if(LeaseType.hui_zu.name().equals(type)){
                    leaseReviewService.effectAndCreate(baseInfo.getId(), processInstanceId);
                    break;
                }
            }
        }
        //项目变更同步创建评审会会议纪要信息
       /* if (ObjectUtil.equals(ProcessModelTypeEnum.ProjReviewModifyFlow.name(), startProcessReq.getModelKey())) {
            SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).initChangeReviewMeetMinute(baseInfo.getId(), processInstanceId);
        }*/
    }

    public void checkRelatedBusinessProcess(Long projReviewId) {
        // 查询合同
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(projReviewId);
        if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
            ProcessPageReq contractProcessReq = new ProcessPageReq();
            contractProcessReq.setBusinessKeyList(contractBaseInfoList.stream().map(ContractBaseInfo::getId).map(Object::toString).collect(Collectors.toList()));
            contractProcessReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
            contractProcessReq.setModelKeyList(BusinessModuleEnum.CONTRACT.getModelKeyList());
            long contractProcessCount = taskApiService.queryProcessCount(contractProcessReq);
            if (contractProcessCount > 0) {
                throw new MithrasException("该项目存在审批中的【合同创建/变更】流程，不可提交评审变更！");
            }
            // 查询付款
            List<PaymentBaseInfo> paymentBaseInfoList = SpringUtil.getBean(PaymentBaseInfoService.class).listByContractIds(contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
                ProcessPageReq paymentProcessReq = new ProcessPageReq();
                paymentProcessReq.setBusinessKeyList(paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).map(Object::toString).collect(Collectors.toList()));
                paymentProcessReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
                paymentProcessReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.PaymentCreateFlow.name()));
                long paymentProcessCount = taskApiService.queryProcessCount(paymentProcessReq);
                if (paymentProcessCount > 0) {
                    throw new MithrasException("该项目存在审批中的【付款申请】流程，不可提交评审变更！");
                }
            }
        }
    }

    public List<ProcessResp> findRelatedProcesses(Long projReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(10);
        processPageReq.setBusinessKey(String.valueOf(projReviewId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_REVIEW.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents();
    }


    public ProcessResp findRelatedProcess(Long projReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(projReviewId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_REVIEW.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    /**
     * 获取最新的评审流程
     * @param projReviewId
     * @return
     */
    public ProcessResp findRelatedAndPassProcess(Long projReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(Integer.MAX_VALUE);
        processPageReq.setSortType(1);
        processPageReq.setBusinessKey(String.valueOf(projReviewId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_REVIEW.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType(),ProcessBusinessStatusEnum.PASS.getType(),ProcessBusinessStatusEnum.PASS_ALL.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().filter(f -> Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()).contains(f.getModelKey())).findFirst().orElse(null);
    }

    public ProcessResp findProcessByProcessInstanceId(String instanceId) {
        return taskApiService.queryProcessById(instanceId);
    }

    private void copyCashFlowPlan(Long projReviewId) {
        /*判断 proj_review_cash_flow_quotation_proposal 中数据复制到proj_review_cash_flow_plan*/

        LambdaQueryWrapper<ProjReviewCashFlowPlan> planWrapper = Wrappers.lambdaQuery();
        planWrapper.eq(ProjReviewCashFlowPlan::getProjectId, projReviewId);
        List<ProjReviewCashFlowPlan> plans = projReviewCashFlowPlanService.list(planWrapper);
        if (plans.size() > 0) {
            return;
        }

        LambdaQueryWrapper<ProjReviewCashFlowQuotationProposal> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProjReviewCashFlowQuotationProposal::getProjectId, projReviewId);
        List<ProjReviewCashFlowQuotationProposal> list = projReviewCashFlowQuotationProposalService.list(wrapper);
        List<ProjReviewCashFlowPlan> planList = new ArrayList<>();
        for (ProjReviewCashFlowQuotationProposal quotationProposal : list) {
            ProjReviewCashFlowPlan plan = ProjReviewCashFlowQuotationProposalConverter.toProjReviewCashFlowPlan(quotationProposal);
            planList.add(plan);
        }
        projReviewCashFlowPlanService.saveBatch(planList);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void processEnd(Long projectId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(projectId);
        // 更新风险敞口和最新的客户风控行业分类
        baseInfo.setLesseeInfo(setExposureRisk(baseInfo.getLesseeInfo()));
        baseInfo.setPledgorInfo(setExposureRisk(baseInfo.getPledgorInfo()));
        baseInfo.setGuaranteeInfo(setExposureRisk(baseInfo.getGuaranteeInfo()));
        baseInfo.setMortgagorInfo(setExposureRisk(baseInfo.getMortgagorInfo()));
        baseInfo.setDebtorInfo(setExposureRisk(baseInfo.getDebtorInfo()));
        baseInfo.setCreditorInfo(setExposureRisk(baseInfo.getCreditorInfo()));
        baseInfo.setRiskControlIndustryClassify(baseInfo.getRiskControlIndustryClassify());
        projReviewBaseInfoMapper.updateById(baseInfo);
        // 记录版本前要先更新状态
        if (processPass) {
            // 审批通过 新增版本
            stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.APPROVAL_PASS, baseInfo.getProcessStatus()));
            copyCashFlowPlan(projectId); // 在没有现金流计划的时候  复制现金流计划
            //更新线上评审会纪要状态
            SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).modifyStatus(processInstanceId, projectId, MeetMinuteStatuesEnum.EFFECT.name());
        } else {
            if (TAKE_EFFECT.name().equals(baseInfo.getProjReviewStatus())) {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_WITHDRAW, baseInfo.getProcessStatus()));
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_REJECT, baseInfo.getProcessStatus()));
                }
            } else {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_WITHDRAW, baseInfo.getProcessStatus()));
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_REJECT, baseInfo.getProcessStatus()));
                }
            }
            //更新部门领导
            projReviewBaseInfoService.renewLeader(projectId);
        }
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        projReviewVersionService.recordVersion(projectId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);

        if (!processPass && TAKE_EFFECT.name().equals(baseInfo.getProjReviewStatus())) {
            projReviewVersionService.reset(projectId);
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_WITHDRAW, baseInfo.getProcessStatus()));
            }
            if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_REJECT, baseInfo.getProcessStatus()));
            }
        }

        // 更新客户角色表
        if (!processPass) {
            projClientRoleService.projReviewFinish(Collections.singletonList(projectId));
            if (modelKey.equals(ProcessModelTypeEnum.ProjReviewModifyFlow.name())) {
                //需要回滚到上一版本
                projClientRoleService.projReviewRollBackLastVersion(projectId);
            }
        }
    }

    public boolean canSave(Long projEstablishId) {
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (projEstablishId == null) {
            return false;
        }
        if (Objects.isNull(loginUser)) {
            return false;
        }
        ProcessResp processResp = findRelatedProcess(projEstablishId);
        if (processResp == null) {
            // 运行中流程为空 可以保存
            return true;
        }
        if (!FlowConstants.START_USER_TASK.equals(processResp.getCurTaskActivityIds())) {
            // 有运行中流程 不在发起人节点 不能保存
            return false;
        }
        if (!Objects.equals(String.valueOf(loginUser.getId()), processResp.getStartUserId())) {
            // 在发起人节点 不是发起人 不能保存
            return false;
        }
        return true;
    }

    /**
     * 当用户变更客户信息时，系统需判断该客户是否存在审批中或审批同意的项目评审，如存在，则客户信息变更需走简易审批。
     * 如该客户不存在项目评审数据，或存在评审但评审流程状态均为审批拒绝，则都视为评审前的客户，其信息变更无需审批。
     *
     * @param clientId
     * @return
     */
    public boolean clientRelatedProjReview(Long clientId) {
        int tmpRelatedCount = projReviewBaseInfoMapper.clientRelatedProjReviewCount(clientId);
        if (tmpRelatedCount > 0) {
            return true;
        }
        return projReviewBaseInfoMapper.clientRelatedProjReviewLibCount(clientId) > 0;
    }

    /**
     * 设置风险敞口
     *
     * @param jsonInfo
     * @return
     */
    public String setExposureRisk(String jsonInfo) {
        if (StrUtil.isNotBlank(jsonInfo)) {
            List<ClientInfo> infos = JSON.parseArray(jsonInfo, ClientInfo.class);
            for (ClientInfo info : infos) {
                if (isNotNull(info.getClientId())) {
                    info.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(info.getClientId(), null, null));
                }
            }
            return JSONUtil.toJsonStr(infos);
        }
        return null;
    }

    public ProjReviewRatingCheckRSP checkRating(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = baseInfoService.getById(projReviewId);
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("项目评审数据不存在");
        }
        Set<Long> clientIds = new HashSet<>();
        int ratingClientEvaCount = 0;
        int ratingClientMainCount = 0;
        int ratingAmountMainCount = 0;
        if (Objects.nonNull(projReviewBaseInfo.getEvaluationSubjectId())) {
            clientIds.add(projReviewBaseInfo.getEvaluationSubjectId());
            ratingClientEvaCount = ratingClientService.count(Wrappers.<RatingClient>lambdaQuery()
                    .eq(RatingClient::getClientId, projReviewBaseInfo.getEvaluationSubjectId())
                    .eq(RatingClient::getRatingStatus, true));
        }
        if (Objects.nonNull(projReviewBaseInfo.getClientId())) {
            clientIds.add(projReviewBaseInfo.getClientId());
            ratingClientMainCount = ratingClientService.count(Wrappers.<RatingClient>lambdaQuery()
                    .eq(RatingClient::getClientId, projReviewBaseInfo.getClientId())
                    .eq(RatingClient::getRatingStatus, true));
        }
        ratingAmountMainCount = ratingAmountService.count(Wrappers.<RatingAmount>lambdaQuery()
                .eq(RatingAmount::getProjReviewId, projReviewId)
                .eq(RatingAmount::getRatingStatus, true));
        ProjReviewRatingCheckRSP rsp = new ProjReviewRatingCheckRSP();
        Map<Long, String> clientNameMap = SpringUtil.getBean(Id2NameService.class).clientId2Name(clientIds);
        rsp.setRatingClientIsDone(ratingClientEvaCount > 0 && ratingClientMainCount > 0);
        rsp.setRatingAmountIsDone(ratingAmountMainCount > 0);
        Map<Long, ProjReviewRatingCheckRSP.ClientInfo> clientInfoMap = new LinkedHashMap<>();
        if (ratingClientEvaCount == 0) {
            clientInfoMap.put(projReviewBaseInfo.getEvaluationSubjectId(), new ProjReviewRatingCheckRSP.ClientInfo(projReviewBaseInfo.getEvaluationSubjectId(), clientNameMap.get(projReviewBaseInfo.getEvaluationSubjectId())));
        }
        if (ratingClientMainCount == 0) {
            clientInfoMap.put(projReviewBaseInfo.getClientId(), new ProjReviewRatingCheckRSP.ClientInfo(projReviewBaseInfo.getClientId(), clientNameMap.get(projReviewBaseInfo.getClientId())));
        }
        rsp.setUndoRatingClientList(new ArrayList<>(clientInfoMap.values()));
        return rsp;
    }

    public ClientMaterialsLackInfoRSP checkClientMaterials(Long projReviewId) {
        List<ClientMaterialsLackInfoRSP.LackInfo> lackInfoList = new LinkedList<>();
        ProjReviewBaseInfo baseInfo = baseInfoService.getById(projReviewId);
        // 检查客户资料
        Set<Long> lesseeIds = new LinkedHashSet<>();
        Set<Long> corporationGuaranteeIds = new LinkedHashSet<>();
        Set<Long> normalGuaranteeIds = new LinkedHashSet<>();
        Set<Long> clientIds = new LinkedHashSet<>();
        if (StrUtil.isNotBlank(baseInfo.getLesseeInfo())) {
            List<ClientInfo> clientInfoList = JSONUtil.toList(baseInfo.getLesseeInfo(), ClientInfo.class);
            if (CollectionUtil.isNotEmpty(clientInfoList)) {
                Set<Long> ids = clientInfoList.stream().map(ClientInfo::getClientId).collect(Collectors.toSet());
                lesseeIds.addAll(ids);
                clientIds.addAll(ids);
            }
        }
        if (StrUtil.isNotBlank(baseInfo.getGuaranteeInfo())) {
            List<ClientInfo> clientInfoList = JSONUtil.toList(baseInfo.getGuaranteeInfo(), ClientInfo.class);
            if (CollectionUtil.isNotEmpty(clientInfoList)) {
                clientInfoList.forEach(e -> {
                    if (Objects.equals(e.getClientType(), ClientType.CORPORATION.name())) {
                        clientIds.add(e.getClientId());
                        corporationGuaranteeIds.add(e.getClientId());
                    }
                    if (Objects.equals(e.getClientType(), ClientType.NORMAL.name())) {
                        clientIds.add(e.getClientId());
                        normalGuaranteeIds.add(e.getClientId());
                    }
                });
            }
        }
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
//        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CLIENT.name());
//        query.in(MaterialsList::getBelongId, clientIds);
//        query.and(innerQuery -> innerQuery.eq(MaterialsList::getSourceBusinessKey, String.format("%s@%s", BusinessModuleEnum.PROJ_REVIEW.name(), projReviewId)).or().eq(MaterialsList::getSourceBusinessKey, ""));
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW_CLIENT.name());
        query.eq(MaterialsList::getBelongId, projReviewId);
        query.in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(Collectors.toSet()));
        List<MaterialsList> materialsLists = materialsListService.list(query);
//        Map<Long, List<MaterialsList>> materialsMap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
        Map<Long, List<MaterialsList>> materialsMap = materialsLists.stream().collect(Collectors.groupingBy(e -> Long.parseLong(e.getSourceBusinessKey())));
        Map<Long, String> clientNameMap = SpringUtil.getBean(Id2NameService.class).clientId2Name(clientIds);
        // 校验承租人（一定是法人）
        for (Long id : lesseeIds) {
            String clientName = clientNameMap.get(id);
            if (StrUtil.isBlank(clientName)) {
                continue;
            }
            // 承租人必传基础资料-营业执照，基础资料-公司章程，基础资料-法定代表人身份证，基础资料-征信报告，财务资料-财务报表资料，财务资料-融资明细
            List<MaterialsList> mlist = materialsMap.get(id);
            Map<CorporationClientMaterialSubTypeEnum, Boolean> needTypeMap = new LinkedHashMap<>();
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.ARTICLES_OF_ASSOCIATION, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.CREDIT_REPORT, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.FINANCIAL_REPORT, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.FINANCING_DETAIL, Boolean.TRUE);
            if (CollectionUtil.isNotEmpty(mlist)) {
                Map<String, List<MaterialsList>> mmap = mlist.stream().filter(e -> StrUtil.isNotBlank(e.getMaterialSubType())).collect(Collectors.groupingBy(MaterialsList::getMaterialSubType));
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.ARTICLES_OF_ASSOCIATION.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.ARTICLES_OF_ASSOCIATION);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.CREDIT_REPORT.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.CREDIT_REPORT);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.FINANCIAL_REPORT.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.FINANCIAL_REPORT);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.FINANCING_DETAIL.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.FINANCING_DETAIL);
                }
            }
            if (CollectionUtil.isNotEmpty(needTypeMap)) {
                ClientMaterialsLackInfoRSP.LackInfo lackInfo = new ClientMaterialsLackInfoRSP.LackInfo();
                lackInfo.setClientId(id);
                lackInfo.setClientName(clientName);
                lackInfo.setLackMaterialsList(needTypeMap.keySet().stream().map(CorporationClientMaterialSubTypeEnum::displayWithParent).collect(Collectors.toList()));
                lackInfoList.add(lackInfo);
            }
        }
        // 校验法人担保人
        for (Long id : corporationGuaranteeIds) {
            String clientName = clientNameMap.get(id);
            if (StrUtil.isBlank(clientName)) {
                continue;
            }
            // 法人担保人必传基础资料-营业执照，基础资料-公司章程，基础资料-征信报告，财务资料-财务报表资料
            List<MaterialsList> mlist = materialsMap.get(id);
            Map<CorporationClientMaterialSubTypeEnum, Boolean> needTypeMap = new LinkedHashMap<>();
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.ARTICLES_OF_ASSOCIATION, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.CREDIT_REPORT, Boolean.TRUE);
            needTypeMap.put(CorporationClientMaterialSubTypeEnum.FINANCIAL_REPORT, Boolean.TRUE);
            if (CollectionUtil.isNotEmpty(mlist)) {
                Map<String, List<MaterialsList>> mmap = mlist.stream().filter(e -> StrUtil.isNotBlank(e.getMaterialSubType())).collect(Collectors.groupingBy(MaterialsList::getMaterialSubType));
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.ARTICLES_OF_ASSOCIATION.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.ARTICLES_OF_ASSOCIATION);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.CREDIT_REPORT.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.CREDIT_REPORT);
                }
                if (mmap.containsKey(CorporationClientMaterialSubTypeEnum.FINANCIAL_REPORT.name())) {
                    needTypeMap.remove(CorporationClientMaterialSubTypeEnum.FINANCIAL_REPORT);
                }
            }
            if (CollectionUtil.isNotEmpty(needTypeMap)) {
                ClientMaterialsLackInfoRSP.LackInfo lackInfo = new ClientMaterialsLackInfoRSP.LackInfo();
                lackInfo.setClientId(id);
                lackInfo.setClientName(clientName);
                lackInfo.setLackMaterialsList(needTypeMap.keySet().stream().map(CorporationClientMaterialSubTypeEnum::displayWithParent).collect(Collectors.toList()));
                lackInfoList.add(lackInfo);
            }
        }
        // 校验自然人担保人
        for (Long id : normalGuaranteeIds) {
            String clientName = clientNameMap.get(id);
            if (StrUtil.isBlank(clientName)) {
                continue;
            }
            // 自然人担保人必传基础资料-身份证，基础资料-个人征信报告
            List<MaterialsList> mlist = materialsMap.get(id);
            Map<NormalClientMaterialSubTypeEnum, Boolean> needTypeMap = new LinkedHashMap<>();
            needTypeMap.put(NormalClientMaterialSubTypeEnum.PERSONAL_CREDIT_REPORT, Boolean.TRUE);
            needTypeMap.put(NormalClientMaterialSubTypeEnum.ID_CARD, Boolean.TRUE);
            if (CollectionUtil.isNotEmpty(mlist)) {
                Map<String, List<MaterialsList>> mmap = mlist.stream().filter(e -> StrUtil.isNotBlank(e.getMaterialSubType())).collect(Collectors.groupingBy(MaterialsList::getMaterialSubType));
                if (mmap.containsKey(NormalClientMaterialSubTypeEnum.ID_CARD.name())) {
                    needTypeMap.remove(NormalClientMaterialSubTypeEnum.ID_CARD);
                }
                if (mmap.containsKey(NormalClientMaterialSubTypeEnum.PERSONAL_CREDIT_REPORT.name())) {
                    needTypeMap.remove(NormalClientMaterialSubTypeEnum.PERSONAL_CREDIT_REPORT);
                }
            }
            if (CollectionUtil.isNotEmpty(needTypeMap)) {
                ClientMaterialsLackInfoRSP.LackInfo lackInfo = new ClientMaterialsLackInfoRSP.LackInfo();
                lackInfo.setClientId(id);
                lackInfo.setClientName(clientName);
                lackInfo.setLackMaterialsList(needTypeMap.keySet().stream().map(NormalClientMaterialSubTypeEnum::displayWithParent).collect(Collectors.toList()));
                lackInfoList.add(lackInfo);
            }
        }
        ClientMaterialsLackInfoRSP rsp = new ClientMaterialsLackInfoRSP();
        rsp.setDetailList(lackInfoList);
        rsp.setIsLack(CollectionUtil.isEmpty(lackInfoList) ? YesOrNoNumberEnum.NO.getCode() : YesOrNoNumberEnum.YES.getCode());
        return rsp;
    }

    public void effectCheck(Long projReviewId) {
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        effectCheck(baseInfo);
    }

    public void effectCheck(ProjReviewBaseInfo baseInfo) {
        //        // 检查现金流计划表
//        List<ProjReviewCashFlowPlan> cashFlowPlanList = projReviewCashFlowPlanService.listByProjReviewId(req.getId());
//        Assert.notEmpty(cashFlowPlanList, () -> MithrasException.newException("请先导入现金流计划表"));
        // 检查尽调报告
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.PROJ_REVIEW.name(), Collections.singletonList(ProjReviewMaterialsEnum.DUE_DILIGENCE_REPORT.name()), Collections.singletonList(baseInfo.getId()));
        Assert.notEmpty(materialsListList, () -> MithrasException.newException("请先上传尽调报告"));
//            missRequiredParam(SIMPLE.name().equals(baseInfo.getApprovalType()) && isNull(baseInfo.getRiskControlManagerId()), "风控经理");
//            missRequiredParam(equal(NORMAL.name(), baseInfo.getApprovalType()) && isBlank(baseInfo.getProjBackground()), "项目背景");
        missRequiredParam((ZL.name().equals(baseInfo.getBizType()) || ZZ.name().equals(baseInfo.getBizType())) && isBlank(baseInfo.getLesseeInfo()), "承租人");
//            missRequiredParam((BL.name().equals(baseInfo.getBizType()) || ZR.name().equals(baseInfo.getBizType())) && isNull(baseInfo.getCreditorClientId()), "债权人");
        missRequiredParam((BL.name().equals(baseInfo.getBizType()) || ZR.name().equals(baseInfo.getBizType())) && isBlank(baseInfo.getCreditorInfo()), "债权人");
        //检查基本信息
        ProjReviewBaseInfoDetailRSP baseInfoDetailRSP = baseInfoService.detail(baseInfo.getId(), null);
        checkBaseInfo(baseInfoDetailRSP);
        //检查报价方案
        ProjReviewPriceDetailRSP priceDetail = priceService.detail(baseInfo.getId());
        checkBaseInfo(priceDetail, false);
    }

    public void pricingApprovalCheck(Long projReviewId) {
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        pricingApprovalCheck(baseInfo);
    }

    public void pricingApprovalCheck(ProjReviewBaseInfo baseInfo) {
        // 检查业务定价审批表 -> 不在检查
        //List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.PROJ_REVIEW.name(), Collections.singletonList(ProjReviewMaterialsEnum.BUSINESS_PRICING_APPROVAL_FORM.name()), Collections.singletonList(baseInfo.getId()));
        //Assert.notEmpty(materialsListList, () -> MithrasException.newException("请先上传业务定价审批表"));
        missRequiredParam((ZL.name().equals(baseInfo.getBizType()) || ZZ.name().equals(baseInfo.getBizType()))
                && isBlank(baseInfo.getLesseeInfo()), "承租人");
        missRequiredParam((BL.name().equals(baseInfo.getBizType()) || ZR.name().equals(baseInfo.getBizType()))
                && isBlank(baseInfo.getCreditorInfo()), "债权人");

        //检查基本信息
        ProjReviewBaseInfoDetailRSP baseInfoDetailRSP = baseInfoService.detail(baseInfo.getId(), null);
        checkBaseInfo(baseInfoDetailRSP);
        //检查报价方案
        ProjReviewPriceDetailRSP priceDetail = priceService.detail(baseInfo.getId());
        checkBaseInfo(priceDetail, true);
    }

    private void checkBaseInfo(ProjReviewBaseInfoDetailRSP projReviewBaseInfoDetailRSP) {
        Boolean flag = false;
        StringBuilder builder = new StringBuilder();
        builder.append("请先完善基本信息：");
        if (projReviewBaseInfoDetailRSP == null) {
            throw new MithrasException("请填写新基本信息");
        }
        if ((ProjectBizType.ZL.name().equals(projReviewBaseInfoDetailRSP.getBizType()) || ProjectBizType.ZZ.name().equals(projReviewBaseInfoDetailRSP.getBizType())) && (projReviewBaseInfoDetailRSP.getLeaseTypes() == null || projReviewBaseInfoDetailRSP.getLeaseTypes().size() == 0)) {
            builder.append("租赁类型;");
            flag = true;
        }
        if (ProjectBizType.BL.name().equals(projReviewBaseInfoDetailRSP.getBizType()) && (projReviewBaseInfoDetailRSP.getFactoringTypes() == null || projReviewBaseInfoDetailRSP.getFactoringTypes().size() == 0)) {
            builder.append("保理类型;");
            flag = true;
        }
        if (ProjectBizType.ZR.name().equals(projReviewBaseInfoDetailRSP.getBizType()) && (projReviewBaseInfoDetailRSP.getZrTypes() == null || projReviewBaseInfoDetailRSP.getZrTypes().size() == 0)) {
            builder.append("转让类型;");
            flag = true;
        }
//        if (org.springframework.util.StringUtils.isEmpty(projReviewBaseInfoDetailRSP.getProjectType())) {
//            builder.append("项目类型;");
//            flag = true;
//        }
        if (org.springframework.util.StringUtils.isEmpty(projReviewBaseInfoDetailRSP.getRiskControlManagerName())) {
            builder.append("风控经理;");
            flag = true;
        }
//        if (org.springframework.util.StringUtils.isEmpty(projReviewBaseInfoDetailRSP.getLegalManagerName())) {
//            builder.append("法务经理;");
//            flag = true;
//        }
        if (Boolean.TRUE.equals(flag)) {
            throw new MithrasException(builder.toString());
        }
    }

    private void checkBaseInfo(ProjReviewPriceDetailRSP priceDetail, boolean isPriceApproval) {
        Boolean flag = false;
        StringBuilder builder = new StringBuilder();
        builder.append("提交审批前请先完善报价方案：");
        if (priceDetail == null || (priceDetail.getAocPriceDetailRSP() == null && priceDetail.getLeasePriceDetailRSP() == null && priceDetail.getFactoringPriceDetailRSP() == null)) {
            throw new MithrasException("提交审批前请先填写报价方案");
        }
        Assert.notNull(priceDetail.getIrr(), () -> MithrasException.newException("IRR未保存"));
        if (!isPriceApproval) {
            ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(priceDetail.getProjectReviewId());
            ProjPricingBaseInfo pricingBaseInfo = projPricingBaseInfoService.getPricingByReview(reviewBaseInfo);
            CommonVersion commonVersion = projPricingVersionService.findNewestVersionWithPriceApproval(Optional.ofNullable(pricingBaseInfo).map(ProjPricingBaseInfo::getId).orElse(-1L));
            if (Objects.nonNull(commonVersion)) {
                // 校验IRR是否变低，如果变低则需要先发起业务定价审批流程
                ProjPricingPriceDetailRSP versionRsp = pricingPriceService.detailVersion(pricingBaseInfo.getId(), commonVersion.getVersion());
                if (Objects.nonNull(versionRsp.getIrr())) {
                    Assert.isTrue(versionRsp.getIrr() <= priceDetail.getIrr(), () -> MithrasException.newException("IRR变低需要先提交业务定价审批流程"));
                }
            }
        }
        //债权转让
        if (priceDetail.getAocPriceDetailRSP() != null) {
//            if (StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getConsultingFee())) {
//                builder.append("服务费/咨询费;");
//                flag = true;
//            }
//            if (StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getRepayType())) {
//                builder.append("还款方式;");
//                flag = true;
//            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getRentalCalcType())) {
                builder.append("还款计算方式;");
                flag = true;
            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getRepayRate())) {
                builder.append("还款频率;");
                flag = true;
            }
//            if (ObjectUtil.isEmpty(priceDetail.getAocPriceDetailRSP().getPlannedStartingDate())) {
//                builder.append("计划起租日;");
//                flag = true;
//            }
        }
        //租赁
        if (priceDetail.getLeasePriceDetailRSP() != null) {
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getLeasePriceDetailRSP().getConsultingFee())) {
                builder.append("服务费/咨询费;");
                flag = true;
            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getLeasePriceDetailRSP().getNominalPrice())) {
                builder.append("名义价款;");
                flag = true;
            }
            if (ObjectUtil.isEmpty(priceDetail.getLeasePriceDetailRSP().getPlannedStartingDate())) {
                builder.append("计划起租日;");
                flag = true;
            }
            if (ObjectUtil.isEmpty(priceDetail.getLeasePriceDetailRSP().getRepayRate())) {
                builder.append("还款频率;");
                flag = true;
            }
        }
        //保理
        if (priceDetail.getFactoringPriceDetailRSP() != null) {
//            if (StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getConsultingFee())) {
//                builder.append("服务费/咨询费;");
//                flag = true;
//            }
//            if (StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getRepayType())) {
//                builder.append("还款方式;");
//                flag = true;
//            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getRentalCalcType())) {
                builder.append("还款计算方式;");
                flag = true;
            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getRepayRate())) {
                builder.append("还款频率;");
                flag = true;
            }
//            if (ObjectUtil.isEmpty(priceDetail.getFactoringPriceDetailRSP().getPlannedStartingDate())) {
//                builder.append("计划起租日;");
//                flag = true;
//            }
        }
        if (Boolean.TRUE.equals(flag)) {
            throw new MithrasException(builder.toString());
        }
    }

    //立项审批通过3个月后如果没有发起评审，系统给运营部用户发送通知：<项目名称>已立项3个月，但未发起评审，请确认是否需手工关闭立项。
    public void noticeClose(){
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .in(ProjEstablishBaseInfo::getProjEstablishStatus, ListUtil.toList(NEW.name(), TAKE_EFFECT.name()))
                .lt(ProjEstablishBaseInfo::getUpdateTime, LocalDate.now().minusMonths(3)));
        //查找评审
        if(ObjectUtil.isEmpty(projEstablishBaseInfos)){
            return;
        }
        Map<Long, String> projEstablishMap = projEstablishBaseInfos.stream().collect(Collectors.toMap(ProjEstablishBaseInfo::getId,
                ProjEstablishBaseInfo::getProjName));
        Set<Long> projEstablishIds = projEstablishMap.keySet();
        Set<Long> projReviewIds = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), EXPIRE.name())
                .in(ProjReviewBaseInfo::getProjEstablishId, projEstablishIds)).stream().map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet());
        projEstablishIds.removeAll(projReviewIds);
        List<Long> yyglb = new ArrayList<>(sysUserService.getUserByDeptCode("YYGLB").stream().map(UserDO::getId).collect(Collectors.toSet()));
        projEstablishIds.forEach(id -> {
            if(ObjectUtil.isNotEmpty(projEstablishMap.get(id))){
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统提醒");
                messageAddREQ.setFlowid(String.valueOf(id));
                messageAddREQ.setRelation(projEstablishMap.get(id));
                messageAddREQ.setNeedOa(false);
                messageAddREQ.setContent(String.valueOf(id));
                messageAddREQ.setNoticeSource("系统提醒");
                messageAddREQ.setMessageType(MessageTypeEnum.PROJ_ESTABLISH_REVIEW.name());
                messageAddREQ.setPcurl(MessageUrlEnum.REVIEW_NOTICE.pcUrl);
                messageAddREQ.setAppurl(MessageUrlEnum.REVIEW_NOTICE.appUrl);
                messageAddREQ.setTo(yyglb);
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            }
        });

    }

    /**
     * 维护失效状态
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void doExpire(Integer days) {
        //1.查找所有评审审批通过时间
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.ProjReviewCreateFlow.name()));
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(Integer.MAX_VALUE);
        flowReq.setProcessStatusList(ListUtil.toList(2, 6));
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return;
        }
        Set<Long> needExpireIds = new HashSet<>();
        List<Long> projReviewIds = flowRespPage.getContents().stream().filter(e -> isNotEmpty(e.getEndTime())).filter(e -> LocalDateTimeUtil.of(e.getEndTime()).plusDays(days).isBefore(LocalDateTime.now())).map(ProcessResp::getBusinessKey).map(Long::valueOf).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(projReviewIds)) {
            return;
        }
        //剔除已经失效或关闭的
        projReviewIds = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .in(ProjReviewBaseInfo::getId, projReviewIds)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, CLOSED.name(), EXPIRE.name())).stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(projReviewIds)) {
            return;
        }
        //剔除已经发起合同创建流程的项目
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getProjReviewId, projReviewIds));
        //需要修改为释放
        if (ObjectUtil.isNotEmpty(contractBaseInfos)) {
            Map<Long, List<ContractBaseInfo>> projReview2ContractId = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
            for (Long reviewId : projReviewIds) {
                List<ContractBaseInfo> tmpContractBaseInfos = projReview2ContractId.get(reviewId);
                if (ObjectUtil.isEmpty(tmpContractBaseInfos)) {
                    //未评审的需要释放
                    needExpireIds.add(reviewId);
                } else {
                    //已经有立项需要判断是否有提交过
                    boolean needExpire = true;
                    for (ContractBaseInfo baseInfo : tmpContractBaseInfos) {
                        if (!(ObjectUtil.equal(baseInfo.getContractStatus(), ContractStatus.NEW.name()) && ObjectUtil.equals(baseInfo.getContractProcessStatus(), ContractProcessStatusEnum.NEW_UNCOMMIT.name()))) {
                            needExpire = false;
                            break;
                        }
                    }
                    if (needExpire) {
                        needExpireIds.add(reviewId);
                    }
                }
            }
        } else {
            needExpireIds = new HashSet<>(projReviewIds);
        }
        //释放
        if (ObjectUtil.isNotEmpty(needExpireIds)) {
            LambdaUpdateWrapper<ProjReviewBaseInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(ProjReviewBaseInfo::getProjReviewStatus, EXPIRE.name());
            wrapper.in(ProjReviewBaseInfo::getId, needExpireIds);
            SpringContextHolder.getBean(ProjReviewBaseInfoService.class).update(wrapper);
        }
    }

    private boolean hasProjReviewOnSite(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .in(VisitRecord::getProjCode, projReviewBaseInfo.getProjCode())
                        .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                        .eq(VisitRecord::getDeleted, 0)
                        .orderByDesc(VisitRecord::getCheckInDate));
        if (!visitRecordList.isEmpty()) {
            Set<Long> visitIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
            List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, "VISIT_RECORD")
                    .in(MaterialsList::getBelongId, visitIds)
                    .eq(MaterialsList::getMaterialsType, VisitPhaseStatus.ON_SITE_DUE_DILIGENCE.name())
            );
            return !dataList.isEmpty();
        }
        return false;
    }

}
