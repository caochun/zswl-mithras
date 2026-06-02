package cn.zswltech.mithras.service.flow.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.VoteResp;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.enums.ProcessGlobalVariableEnum;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.trackEvent.TrackEventContractInfoREQ;
import cn.zswltech.mithras.dto.trackEvent.TrackEventContractInfoRSP;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.model.RatingSnapshot;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.factory.service.RatingReportService;
import cn.zswltech.mithras.factory.service.RatingSnapshotService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.MithrasConstants;
import cn.zswltech.mithras.service.delayed.RedisDelayedQueue;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.service.enums.afterlease.SaveStatusEnum;
import cn.zswltech.mithras.service.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractTextTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjectType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.service.mapper.afterlease.NewAfterLeaseCheckReportMetaMapper;
import cn.zswltech.mithras.service.mapper.client.ClientAuthorityMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.SystemConfig;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ClientAuthority;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRetreatInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSettlePlan;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.pubInfo.PublicInfoQuery;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.mapper.model.trackEvent.TrackEventInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.afterlese.NewAfterLeaseCheckReportDetailService;
import cn.zswltech.mithras.service.service.afterlese.PenaltyReduceBaseInfoService;
import cn.zswltech.mithras.service.service.assetclassify.AssetManagementReviewExpirationListener;
import cn.zswltech.mithras.service.service.assetclassify.AssetReviewExpirationListener;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayFlowService;
import cn.zswltech.mithras.service.service.client.ClientBusinessOpinionService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionDeptWeightService;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.impl.ClientVersionServiceImpl;
import cn.zswltech.mithras.service.service.lib.groupcreditreview.handler.impl.GroupCreditReviewBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.payment.pubinfo.PublicInfoQueryService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.*;
import cn.zswltech.mithras.service.service.trackEvent.TrackEventService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import cn.zswltech.mithras.contract.core.application.ContractRetreatInfoService;

/**
 * 节点结束监听
 *
 * @author wangchuanhao
 * @date 2022/8/9 11:54 PM
 */
@Slf4j
@Component
public class ProcessNodeEndListener implements ApplicationListener<NodeEndEvent> {

    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
    @Resource
    private ClientVersionServiceImpl clientVersionService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private ClientBusinessOpinionService clientBusinessOpinionService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private ClientAuthorityMapper clientAuthorityMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private RatingSnapshotService ratingSnapshotService;
    @Resource
    private RatingReportService ratingReportService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private BudgetPlanPayFlowService budgetPlanPayFlowService;
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;
    @Resource
    private RedisDelayedQueue redisDelayedQueue;
    @Resource
    private TrackEventService trackEventService;
    @Resource
    private ContractRetreatInfoService contractRetreatInfoService;


    @Override
    public void onApplicationEvent(NodeEndEvent event) {
        NodeCommonContext nodeCommonContext = event.getNodeCommonContext();
        //判断工商信息是否合法
        if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractCreateFlow.name(),
                ProcessModelTypeEnum.ContractModifyFlow.name(), ProcessModelTypeEnum.ContractLPRChangeFlow.name(), ProcessModelTypeEnum.ContractExtensionFlow.name(),
                ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name())) {
            //判断是否发起人和运营岗 "userTask_startUser".equals(nodeCommonContext.getActivityId()) ||
            if ("Activity_0wrrxch".equals(nodeCommonContext.getActivityId())) {
                clientBusinessOpinionService.checkOpinion(nodeCommonContext.getProcessInstanceId(), nodeCommonContext.getModelKey(),
                        nodeCommonContext.getActivityId(), Long.valueOf(nodeCommonContext.getBusinessKey()));
            }
            // 已起租的合同发起的【合同变更流程】在运营管理经办【同意】后，抄送「运营管理经办岗」
            boolean isCcYunYingGuanLi = false;
            if (StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractModifyFlow.name()) && StrUtil.equals(nodeCommonContext.getActivityId(), "userTask_yunYingGuanLi")) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.parseLong(nodeCommonContext.getBusinessKey()));
                isCcYunYingGuanLi = StrUtil.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name());
            }
            if (StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractExtensionFlow.name()) && StrUtil.equals(nodeCommonContext.getActivityId(), "userTask_operationManagement")) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.parseLong(nodeCommonContext.getBusinessKey()));
                isCcYunYingGuanLi = StrUtil.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name());
            }
            if (StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name()) && StrUtil.equals(nodeCommonContext.getActivityId(), "userTask_operationManagement")) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.parseLong(nodeCommonContext.getBusinessKey()));
                isCcYunYingGuanLi = StrUtil.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name());
            }
            if (isCcYunYingGuanLi) {
                List<Long> ccUserIds = sysUserService.queryJobUserIds(JobEnum.yunYingGuanLi.name());
                if (CollectionUtil.isNotEmpty(ccUserIds)) {
                    ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
                    req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
                    req.setCcUserIdList(ccUserIds);
                    req.setMessage("合同变更流程-运营经办节点已通过");
                    getBean(ExecutionService.class).cc(req);
                }
            }
        } else if (ProcessModelTypeEnum.PaymentCreateFlow.name().equals(nodeCommonContext.getModelKey())) {
            //判断是否发起人,项目经理和放款审核岗 "userTask_startUser".equals(nodeCommonContext.getActivityId()) || || "userTask_projectmanager".equals(nodeCommonContext.getActivityId())
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
            if ("userTask_loanReviewPost".equals(nodeCommonContext.getActivityId())) {
                if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
                    clientBusinessOpinionService.checkOpinion(nodeCommonContext.getProcessInstanceId(), nodeCommonContext.getModelKey(),
                            nodeCommonContext.getActivityId(), paymentBaseInfo.getContractId());
                }

            }
        }
        if (ProcessModelTypeEnum.ProjReviewCreateFlow.name().equals(nodeCommonContext.getModelKey())
                || ProcessModelTypeEnum.ProjReviewModifyFlow.name().equals(nodeCommonContext.getModelKey())) {
            ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(Long.valueOf(nodeCommonContext.getBusinessKey()));
            //项目节点需判断是否客户已经被占用 换到审批通过按钮处
            //clientService.checkClientOccupy(baseInfo.getClientId(), baseInfo.getProjSponsorUserId());
            // 项目评审流程 总经理节点 填充变量
            if ("userTask_generalManager".equals(nodeCommonContext.getActivityId())) {
                // 风险敞口 是否省内 项目类型 做在总经理节点结束后 监听器
                Long clientId = extractClientIdFromProjReviewBaseInfo(baseInfo);
                // 找到立项的基本信息
                /*逻辑优化  针对集团的评审  走到这里会空指针  何老师  暂定加判空逻辑*/
                if(ObjectUtil.isNotEmpty(baseInfo.getProjEstablishId())) {
                    ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoService.getById(baseInfo.getProjEstablishId());
                    boolean isPublicOrCivil = Objects.equals(projEstablishBaseInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name())
                            || Objects.equals(projEstablishBaseInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name());

                    Map<String, Object> varMap = new HashMap<>(2);
                    varMap.put(ProcessVarEnum.riskExposure.name(), calClientRiskExposure(clientId, baseInfo));
                    varMap.put(ProcessVarEnum.isPublicOrCivil.name(), isPublicOrCivil);
                    // isPublicOrCivil用于老流程，这个字段的逻辑暂时不能删除，用一个新字段来实现需求
                    varMap.put(ProcessVarEnum.clientRiskControlIndustryClassify.name(), Optional.ofNullable(projEstablishBaseInfo.getRiskControlIndustryClassify()).orElse(""));

                    flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
                    // 客户风控行业分类冗余到业务表中
                    baseInfo.setRiskControlIndustryClassify(projEstablishBaseInfo.getRiskControlIndustryClassify());
                    projReviewBaseInfoMapper.updateById(baseInfo);
                }
            }
            if ("userTask_fullReviewCommittee".equals(nodeCommonContext.getActivityId())) {
                //查询专职评审委员投票结果
                List<VoteResp> fullReviewCommittees = flowProcessApiService.queryVoteResult(nodeCommonContext.getProcessInstanceId(), "userTask_fullReviewCommittee");
                boolean isFinish = false;
                if (ObjectUtil.isNotEmpty(fullReviewCommittees)) {
                    Set<String> collect = fullReviewCommittees.stream().map(VoteResp::getType).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
                    if (!collect.contains(ApprovalButtonTypeEnum.VOTE_AGREE.name())) {
                        //结束流程
                        isFinish = true;
                        runtimeService.setVariable(nodeCommonContext.getProcessInstanceId(), ProcessGlobalVariableEnum.END_FLAG.generateGlobalVarName(), ProcessBusinessStatusEnum.REJECT.getType());
                    }
                }
                Map<String, Object> varMap = new HashMap<>(1);
                varMap.put("isFinish", isFinish);
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
            }
            //评审会秘书统计投票结果 同步创建评审会会议纪要信息
            if ("userTask_juryVote".equals(nodeCommonContext.getActivityId())) {
                //项目变更同步创建评审会会议纪要信息
                if (ObjectUtil.equals(ProcessModelTypeEnum.ProjReviewModifyFlow.name(), nodeCommonContext.getModelKey())) {
                    SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).initChangeReviewMeetMinute(Long.valueOf(nodeCommonContext.getBusinessKey()), nodeCommonContext.getProcessInstanceId());
                } else {
                    SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).initProjReviewMeetMinute(Long.valueOf(nodeCommonContext.getBusinessKey()), nodeCommonContext.getProcessInstanceId());
                }
            }
        } else if (ProcessModelTypeEnum.ContractCreateFlow.name().equals(nodeCommonContext.getModelKey())
                || ProcessModelTypeEnum.ContractModifyFlow.name().equals(nodeCommonContext.getModelKey())
                || ProcessModelTypeEnum.ContractExtensionFlow.name().equals(nodeCommonContext.getModelKey())
                || ProcessModelTypeEnum.ContractEarlySettleFlow.name().equals(nodeCommonContext.getModelKey())
                || ProcessModelTypeEnum.ContractEarlyRepayFlow.name().equals(nodeCommonContext.getModelKey())
                || ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name().equals(nodeCommonContext.getModelKey())) {
            // 合同创建、合同变更流程 总经理节点 填充变量
            if ("userTask_generalManager".equals(nodeCommonContext.getActivityId())) {
                // 风险敞口 是否省内 项目类型 做在总经理节点结束后 监听器
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
                Map<String, Object> varMap = new HashMap<>(1);
                if (ProcessModelTypeEnum.ContractEarlySettleFlow.name().equals(nodeCommonContext.getModelKey())) {
                    // 合同提前结清的总经理节点需要增加申请减免金额的变量
                    ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractBaseInfo.getId());
                    if (Objects.isNull(contractSettlePlan)) {
                        varMap.put("applyDerateAmount", 0);
                    } else {
                        varMap.put("applyDerateAmount", Optional.ofNullable(contractSettlePlan.getApplyDerateAmount()).orElse(0L));
                    }
                }
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
            }
            if (ProcessModelTypeEnum.ContractCreateFlow.name().equals(nodeCommonContext.getModelKey())
                    || ProcessModelTypeEnum.ContractModifyFlow.name().equals(nodeCommonContext.getModelKey())) {
                if (Objects.equals("userTask_lawManager", nodeCommonContext.getActivityId())) {
                    // 需要处理第2个法务经理节点剔除第1个法务经理节点的审批人，在第1个法务经理结束节点后就设置第2个节点的审批人
                    List<Long> legalManagerIds = sysUserService.queryJobUserIds(JobEnum.legalmanager.name());
                    if (CollectionUtil.isNotEmpty(legalManagerIds) && legalManagerIds.size() > 1) {
                        legalManagerIds.removeIf(item -> Objects.equals(AccountUtil.getLoginInfo().getId(), item));
                    }
                    List<String> assignUserIdList = legalManagerIds.stream().map(String::valueOf).collect(Collectors.toList());
                    flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_lawManager_2"), assignUserIdList));
                }
                // 如果是运营管理（经办）节点审批完成，需要在运营管理（复核）节点中踢掉当前次审批的人
                if (Objects.equals("userTask_yunYingGuanLi", nodeCommonContext.getActivityId())) {
                    List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.yunYingGuanLiReview.name());
                    if (CollectionUtil.isNotEmpty(userIds) && userIds.size() > 1) {
                        userIds.removeIf(item -> Objects.equals(AccountUtil.getLoginInfo().getId(), item));
                    }
                    List<String> assignUserIdList = userIds.stream().map(String::valueOf).collect(Collectors.toList());
                    flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_yunYingGuanLiReview"), assignUserIdList));
                    // 合同创建流程如果是运营管理（经办）节点，添加流程变量用于控制后续是否需要经过法务节点
                    if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractCreateFlow.name())) {
                        Long contractId = Long.valueOf(nodeCommonContext.getBusinessKey());
                        flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessVarEnum.contractCreateSkipLawFlag.name(), this.getSkipLawFlag(contractId)));
                    }
                    // 合同其他变更流程如果是运营管理（经办）节点，添加流程变量用于控制后续是否需要经过法务节点
                    if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractModifyFlow.name())) {
                        Long contractId = Long.valueOf(nodeCommonContext.getBusinessKey());
                        flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessVarEnum.contractModifySkipLawFlag.name(), this.getSkipLawFlag(contractId)));
                    }
                }
            }
        } else if (ProcessModelTypeEnum.PaymentCreateFlow.name().equals(nodeCommonContext.getModelKey())) {
            if ("userTask_generalManager".equals(nodeCommonContext.getActivityId())) {
                /*PaymentBaseInfo payment = paymentBaseInfoService.getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
                Long clientId = payment.getClientId();
                boolean clientInProvinceFlag = clientInProvinceFlag(clientId);
                // 2022-09-08 此处变量在新流程图中不再使用 开始-------------
                Map<String, Object> varMap = MapUtil.of(
                        Pair.of(ProcessVarEnum.InsideProvinceFlag.name(), clientInProvinceFlag),
                        Pair.of(ProcessVarEnum.ProjectType.name(), Optional.ofNullable(payment.getConProjectType()).orElse(ProjectType.OTHER.name())),
                        Pair.of(ProcessVarEnum.riskExposure.name(), calClientRiskExposure(clientId, payment.getId(), BusinessModuleEnum.PAYMENT))
                );
                // 2022-09-08 此处变量在新流程图中不再使用 结束-------------
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);*/
            }
            // 在项目经理阶审批过后，需要设置公开信息的相关信息
            if ("userTask_projectmanager".equals(nodeCommonContext.getActivityId())) {
                getBean(PublicInfoQueryService.class).lambdaUpdate()
                        .eq(PublicInfoQuery::getPaymentId, Long.valueOf(nodeCommonContext.getBusinessKey()))
                        .set(PublicInfoQuery::getConfirmedBy, AccountUtil.getLoginInfo().getId())
                        .set(PublicInfoQuery::getConfirmedTime, LocalDateTime.now())
                        .update();
            }
        } else if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name())) {
            if ("userTask_generalManager".equals(nodeCommonContext.getActivityId())) {
                GroupCreditReviewBaseInfo reviewBaseInfo = groupCreditReviewBaseInfoService.getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
                Long clientId = reviewBaseInfo.getClientId();
                boolean clientInProvinceFlag = clientInProvinceFlag(clientId);
                // 客户风控行业分类判断
                Client client = clientService.getById(reviewBaseInfo.getClientId());
                Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
                CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
                Assert.notNull(corpCommerceInfoLib, () -> MithrasException.newException("无生效的客户工商信息数据"));
                Assert.notBlank(corpCommerceInfoLib.getRiskControlIndustryClassify(), () -> MithrasException.newException(String.format("请先至客户模块维护<%s>的“风控行业分类”，否则无法判断该项目是否需经董事会审议！", client.getClientName())));
                boolean isPublicOrCivil = false;
                if (Objects.equals(corpCommerceInfoLib.getRiskControlIndustryClassify(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name())
                        || Objects.equals(corpCommerceInfoLib.getRiskControlIndustryClassify(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name())) {
                    isPublicOrCivil = true;
                }
                Map<String, Object> varMap = MapUtil.of(
                        // 2023-03-17 此处变量在新流程图中不再使用 开始-------------
                        Pair.of(ProcessVarEnum.InsideProvinceFlag.name(), clientInProvinceFlag),
                        Pair.of(ProcessVarEnum.ProjectType.name(), Optional.ofNullable(reviewBaseInfo.getProjectType()).orElse(ProjectType.OTHER.name())),
                        // 2023-03-17 此处变量在新流程图中不再使用 结束-------------
                        Pair.of(ProcessVarEnum.riskExposure.name(), calGroupCreditRiskExposureNew(clientId, reviewBaseInfo)),
                        Pair.of(ProcessVarEnum.isPublicOrCivil.name(), isPublicOrCivil),
                        // isPublicOrCivil用于老流程，这个字段的逻辑暂时不能删除，用一个新字段来实现需求
                        Pair.of(ProcessVarEnum.clientRiskControlIndustryClassify.name(), Optional.ofNullable(corpCommerceInfoLib.getRiskControlIndustryClassify()).orElse(""))
                );
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
                // 客户风控行业分类冗余到业务表中
                reviewBaseInfo.setRiskControlIndustryClassify(corpCommerceInfoLib.getRiskControlIndustryClassify());
                groupCreditReviewBaseInfoService.updateById(reviewBaseInfo);
            }
            if ("userTask_fullReviewCommittee".equals(nodeCommonContext.getActivityId())) {
                //查询专职评审委员投票结果
                List<VoteResp> fullReviewCommittees = flowProcessApiService.queryVoteResult(nodeCommonContext.getProcessInstanceId(), "userTask_fullReviewCommittee");
                boolean isFinish = false;
                if (ObjectUtil.isNotEmpty(fullReviewCommittees)) {
                    Set<String> collect = fullReviewCommittees.stream().map(VoteResp::getType).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
                    if (!collect.contains(ApprovalButtonTypeEnum.VOTE_AGREE.name())) {
                        //结束流程
                        isFinish = true;
                        runtimeService.setVariable(nodeCommonContext.getProcessInstanceId(), ProcessGlobalVariableEnum.END_FLAG.generateGlobalVarName(), ProcessBusinessStatusEnum.REJECT.getType());
                    }
                }
                Map<String, Object> varMap = new HashMap<>(1);
                varMap.put("isFinish", isFinish);
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
            }
            //评审会秘书统计投票结果 同步创建评审会会议纪要信息
            if ("userTask_juryVote".equals(nodeCommonContext.getActivityId())) {
                if (ObjectUtil.equals(ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name(), nodeCommonContext.getModelKey())) {
                    SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).initChangeReviewMeetMinute(Long.valueOf(nodeCommonContext.getBusinessKey()), nodeCommonContext.getProcessInstanceId());
                } else {
                    SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).initGroupProjReviewMeetMinute(Long.valueOf(nodeCommonContext.getBusinessKey()), nodeCommonContext.getProcessInstanceId());
                }
            }
        } else if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ProjEstablishCreateFlow.name(),
                ProcessModelTypeEnum.ProjEstablishModifyFlow.name())) {
            //项目立项
            ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoService.getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
            //项目节点需判断是否客户已经被占用
            //clientService.checkClientOccupy(baseInfo.getClientId(), baseInfo.getProjSponsorUserId());
        } else if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(),
                ProcessModelTypeEnum.NewAfterLeaseCheckReportFlow.name(),
                ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name())) {
            // 如果当前结点是项目经理结点，需要校验数据是否合法
            if (Objects.equals(nodeCommonContext.getActivityId(), "project_manager")) {
                NewAfterLeaseCheckPlanClient checkPlanClient = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).getById(Long.parseLong(nodeCommonContext.getBusinessKey()));
                if (Objects.isNull(checkPlanClient)) {
                    throw new MithrasException("没有找到租后检查记录");
                }
                NewAfterLeaseCheckReportDetail reportDetail = getBean(NewAfterLeaseCheckReportDetailService.class).getOneByCheckPlanClientId(checkPlanClient.getId());
                Assert.notNull(reportDetail, () -> MithrasException.newException("没有找到租后检查报告记录"));
                if (CharSequenceUtil.equalsAny(SaveStatusEnum.NO_SAVE.name(), reportDetail.getReportSummarySaveStatus(), reportDetail.getReportContentSaveStatus())) {
                    throw new MithrasException("存在暂存的检查报告，请先保存后提交");
                }
            }
            // 如果是风控负责人节点则获取客户风险敞口后填充流程参数，用于判断后续是否要经过首席风险官
            if (Objects.equals(nodeCommonContext.getActivityId(), "userTask_riskDeptMaster")) {
                NewAfterLeaseCheckPlanClient checkPlanClient = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).getById(Long.parseLong(nodeCommonContext.getBusinessKey()));
                if (Objects.isNull(checkPlanClient)) {
                    throw new MithrasException("没有找到租后检查记录");
                }
                // 找元数据信息，获取模版类型
                NewAfterLeaseCheckReportMeta afterLeaseCheckReportMeta = getBean(NewAfterLeaseCheckReportMetaMapper.class).selectOne(Wrappers.<NewAfterLeaseCheckReportMeta>lambdaQuery()
                        .eq(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, checkPlanClient.getId()));
                if (Objects.isNull(afterLeaseCheckReportMeta)) {
                    throw new MithrasException("没有找到租后检查报告元数据信息");
                }
                CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(checkPlanClient.getClientId());
                Map<String, Object> varMap = new HashMap<>();
                boolean isIndustry = true;
                if (Objects.nonNull(corpCommerceInfoLib)) {
                    if (CharSequenceUtil.equalsAny(corpCommerceInfoLib.getRiskControlIndustryClassify(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name())) {
                        isIndustry = false;
                    }
                }
                // 若模版选择为新增加的「公交类」「国有资产类」，无需根据客户风控行业非类进行判断，审批流程走「非产业类」分支。
                // 如果是公交类或者是国有资产类，则设置行业分类为非产业类
                if (CharSequenceUtil.equalsAny(afterLeaseCheckReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.BUS.name(), AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name())) {
                    isIndustry = false;
                }
                varMap.put(ProcessVarEnum.isIndustry.name(), isIndustry);
                Long riskExposure = contractBaseInfoService.getStockRiskExposure(checkPlanClient.getClientId(), null, null);
                BigDecimal b = BigDecimal.valueOf(riskExposure).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
                varMap.put(ProcessVarEnum.riskExposure.name(), b.doubleValue());
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
            }
            if (Objects.equals(nodeCommonContext.getActivityId(), "project_manager")) {
                // 提交时间 计划台账展示使用
                NewAfterLeaseCheckPlanClient checkPlanClient = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).getById(Long.parseLong(nodeCommonContext.getBusinessKey()));
                if (Objects.isNull(checkPlanClient)) {
                    throw new MithrasException("没有找到租后检查记录");
                }
                checkPlanClient.setCommitTime(LocalDateTime.now());
                SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).updateById(checkPlanClient);

                SpringContextHolder.getBean(AfterLeaseCheckPlanClientService.class).submitApprovalCheck(Long.valueOf(nodeCommonContext.getBusinessKey()));
            }
        } else if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.LeaseCreateFlow.name(), ProcessModelTypeEnum.LeaseModifyFlow.name())) {
            // 如果是运营经理（经办）节点审批完成，需要在运营经理（复核）节点中踢掉当前次审批的人
            if (Objects.equals("operationManagement", nodeCommonContext.getActivityId())) {
                List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.operationManagementReview.name());
                if (CollectionUtil.isNotEmpty(userIds) && userIds.size() > 1) {
                    userIds.removeIf(item -> Objects.equals(AccountUtil.getLoginInfo().getId(), item));
                }
                List<String> assignUserIdList = userIds.stream().map(String::valueOf).collect(Collectors.toList());
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_operationManagementReview"), assignUserIdList));
            } // 租赁物创建/变更审核流程抄送部门负责人，项目经理提交流程即抄送
            else if (Objects.equals("projManager", nodeCommonContext.getActivityId())) {
                // 抄送部门负责人
                LeaseItemInfo leaseItemInfo = leaseItemInfoService.getById(nodeCommonContext.getBusinessKey());
                if (ObjectUtil.isEmpty(leaseItemInfo)) {
                    throw new MithrasException("租赁基本信息为空");
                }
                ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(leaseItemInfo.getProjReviewId());
                if (projReviewBaseInfo == null) {
                    throw new MithrasException("没有找到项目不存在");
                }
                List<OrgDO> orgList = sysUserService.listOrgByJob(projReviewBaseInfo.getProjSponsorUserId(), JobEnum.projmanager.name());
                if (CollectionUtil.isEmpty(orgList)) {
                    throw new MithrasException("没有找到项目主办作为项目经理的部门信息，保存失败");
                }
                Long deptId = orgList.get(0).getId();
                List<UserDO> businessHeadList = getBean(UserService.class).jobUsers(deptId, JobEnum.businesshead.name());
                Set<Long> ccUserIdList = new HashSet<>();
                for (UserDO userDO : businessHeadList) {
                    ccUserIdList.add(userDO.getId());
                }
                ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
                req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
                req.setCcUserIdList(new ArrayList<>(ccUserIdList));
                getBean(ExecutionApi.class).cc(req);
            }
        } else if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ClientAuthorityCreateFlow.name(), ProcessModelTypeEnum.ClientAuthorityModifyFlow.name())) {
            // 如果是运营经理（经办）节点审批完成，需要在运营经理（复核）节点中踢掉当前次审批的人
            if (Objects.equals("userTask_yyglbDeptLeader", nodeCommonContext.getActivityId())) {
                Client client = clientService.getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
                if (client == null) {
                    throw new MithrasException("没有找到客户");
                }
                List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                        .eq(ClientAuthority::getClientId, client.getId())
                        .eq(ClientAuthority::getDeleted, 0));
                if (clientAuthorityList != null && !clientAuthorityList.isEmpty()) {
                    for (ClientAuthority clientAuthority : clientAuthorityList) {
                        if (ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()
                        ) {
                            throw new MithrasException(String.format("该客户已被%s-%s占用",
                                    id2NameService.deptId2NameSingle(clientAuthority.getDeptId()), id2NameService.sysUserId2NameSingle(clientAuthority.getUserId())));
                        }
                    }
                }
            }
        }
        if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractStartRentFlow.name())) {
            if (Objects.equals("userTask_startUser", nodeCommonContext.getActivityId())) {
                //SpringContextUtil.getBean(ContractReceiptService.class).checkActualIrrByContractId(Long.valueOf(nodeCommonContext.getBusinessKey()));
            }
        }
        if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name())) {
            if (Objects.equals("userTask_projectSponsor", nodeCommonContext.getActivityId())) {
                ContractBaseInfo contractBaseInfo = SpringContextUtil.getBean(ContractBaseInfoService.class).getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
                if (Objects.nonNull(contractBaseInfo)) {
                    SpringContextUtil.getBean(ContractService.class).startRentCheck(contractBaseInfo);
                }
                //SpringContextUtil.getBean(ContractReceiptService.class).checkActualIrrByContractId(Long.valueOf(nodeCommonContext.getBusinessKey()));
            }
        }
        if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionModifyFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name())) {
            if (Objects.equals("userTask_kpiManagement", nodeCommonContext.getActivityId())) {
                ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
                req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
                List<UserDO> jobUsers = Optional.ofNullable(getBean(UserService.class).getUsersByjobcod(JobEnum.humanresourcessupervisor.name())).orElse(new ArrayList<>());
                req.setCcUserIdList(jobUsers.stream().map(UserDO::getId).collect(Collectors.toList()));
                getBean(ExecutionApi.class).cc(req);
            }
        }

        if (CharSequenceUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractLPRChangeFlow.name())
                && CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_startUser")) {
            setNextNodeVal(event);
        }

        if (CharSequenceUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractEarlyRepayFlow.name())
                && CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_bizDivisionLeader")) {
            setNextNodeVal(event);
        }

        if (CharSequenceUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name())
                && CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_bizDivisionLeader")) {
            setNextNodeVal(event);
        }

        if (CharSequenceUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractEarlySettleFlow.name())
                && CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_yunYingGuanLi")) {
            setNextNodeVal(event);
        }

        if (CharSequenceUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ContractExtensionFlow.name())
                && CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_bizDivisionLeader")) {
            setNextNodeVal(event);
        }

        //租后相关参数添加
        if (CharSequenceUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.NewRentCollectionExemptionFlow.name())
                && CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_generalManager")) {
            Long maxContractAmountByReduceId = SpringContextHolder.getBean(PenaltyReduceBaseInfoService.class).getMaxContractAmountByReduceId(Long.valueOf(nodeCommonContext.getBusinessKey()));
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(ProcessVarEnum.applyCreditAmount.name()), NumberUtil.div(maxContractAmountByReduceId.toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).doubleValue()));
        }

        if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.RatingClientCreateFlow.name(), ProcessModelTypeEnum.RatingClientUpdateFlow.name())) {
            Map<String, Object> varMap = new HashMap<>(1);
            if (CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_riskManager", "userTask_riskManager_fuhe")) {
                // 风控经理下迁
                RatingClient ratingClient = ratingClientService.getById(nodeCommonContext.getBusinessKey());

                // 指标校验另设接口
//                ratingReportService.checkApprovalOpinion(ratingClient);
                varMap.put("overturn", Objects.equals(Boolean.TRUE, ratingClient.getOverturn()));

                if (ratingClient.getOverturn() == null) {
                    ratingClientService.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, ratingClient.getId()).set(RatingClient::getOverturn, Boolean.FALSE));
                }
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
            } else if (CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_startUser", "userTask_projManager_fuhe")) {
                // 项目经理调整
                RatingClient ratingClient = ratingClientService.getById(nodeCommonContext.getBusinessKey());
                varMap.put("adjust", Objects.equals(Boolean.TRUE, ratingClient.getAdjust()));

                if (ratingClient.getAdjust() == null) {
                    ratingClientService.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, ratingClient.getId()).set(RatingClient::getAdjust, Boolean.FALSE));
                }
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
                // 发起人节点
                if (CharSequenceUtil.equals(nodeCommonContext.getActivityId(), "userTask_startUser")) {
                    // 清除审批状态
                    ratingReportService.removeApprovalStatus(ratingClient);
                    // 重置试算次数
                    ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                            .eq(RatingSnapshot::getId, ratingClient.getSnapshotId())
                            .set(RatingSnapshot::getExecuteCount, 0));
                }

            }
        }
        // 预算投放计划修正
        if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.YearHalfOtherPlanEventFlow.name(), ProcessModelTypeEnum.MonthPlanEventFlow.name())) {
            // 部门总-确认状态：初始默认为“待确认”，月度投放计划收集（部门）待办流程的部门负责人节点提交后，更新为“已确认”
            if (CharSequenceUtil.equals(nodeCommonContext.getActivityId(), "userTask_deptMaster")) {
                budgetPlanPayFlowService.bizDeptLeaderConfirm(nodeCommonContext);
            }
            // 分管总-确认状态：初始默认为“待确认”，月度投放计划收集（部门）待办流程的分管领导节点提交后，更新为“已确认”
            if (CharSequenceUtil.equals(nodeCommonContext.getActivityId(), "userTask_bizDivisionLeader")) {
                budgetPlanPayFlowService.bizDivisionLeaderConfirm(nodeCommonContext);
            }
        }
        // 付款实际核销运营提前审核
        if (StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.PaymentReviewInAdvancedFlow.name())) {
            if (StrUtil.equals(nodeCommonContext.getActivityId(), "userTask_loanreviewpost")) {
                // 放款审核岗通过后抄送运营管理经办岗
                List<Long> targetUserIds = sysUserService.queryJobUserIds(JobEnum.yunYingGuanLi.name());
                if (CollectionUtil.isNotEmpty(targetUserIds)) {
                    ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
                    req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
                    req.setCcUserIdList(targetUserIds);
                    req.setMessage(String.format("%s-放款审核岗节点已通过", ProcessModelTypeEnum.PaymentReviewInAdvancedFlow.getDisplay()));
                    getBean(ExecutionService.class).cc(req);
                }
            }
        }
        if(StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.ProjectProfitSharingFlow.name())){
            // 部门的分配信息校验
            List<FinanceProjectDistributionDeptWeightInfo> deptWeightInfos = SpringUtil.getBean(FinanceProjectDistributionDeptWeightService.class).queryList(Long.parseLong(nodeCommonContext.getBusinessKey()));
            if (CollectionUtil.isEmpty(deptWeightInfos)) {
                throw new MithrasException("请维护部门分配信息");
            }
            /*业务部门负责人*/
            List<String> leaderIds = new LinkedList<>();
            /*分管领导*/
            List<String> businessHeaderIds = new LinkedList<>();

            // 拿出所有的部门，再根据部门id获取部门负责人和分管领导
            List<Long> deptIds = deptWeightInfos.stream().map(FinanceProjectDistributionDeptWeightInfo::getWeightTarget)
                    .filter(Objects::nonNull).distinct().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(deptIds)) {
                throw new MithrasException("请维护部门分配信息");
            }

            for (Long deptId : deptIds) {
                // 部门负责人
                List<UserDO> businessHeaderList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.businesshead.name());
                if (CollectionUtil.isNotEmpty(businessHeaderList)) {
                    for (UserDO userDO : businessHeaderList) {
                        businessHeaderIds.add(String.valueOf(userDO.getId()));
                    }
                }

                // 分管领导
                List<UserDO> leaderList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.leaderincharge.name());
                if (CollectionUtil.isNotEmpty(leaderList)) {
                    for (UserDO userDO : leaderList) {
                        leaderIds.add(String.valueOf(userDO.getId()));
                    }
                }
            }
            // 获取部门负责人和分管领导 去重
            leaderIds = leaderIds.stream().distinct().collect(Collectors.toList());
            businessHeaderIds = businessHeaderIds.stream().distinct().collect(Collectors.toList());
            if (StrUtil.equals(nodeCommonContext.getActivityId(), "userTask_startUser")) {
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_deptHand"), businessHeaderIds));
            }
            if (StrUtil.equals(nodeCommonContext.getActivityId(), "userTask_deptHand")) {
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_leader"), leaderIds));
            }

        }
        // 增加跟踪事项处理
//        if (CharSequenceUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.TrackEventCreateFlow.name())){
//            log.info("trackEventInfo modelKey: {}, activityId: {}, businessKey: {}",nodeCommonContext.getModelKey(),nodeCommonContext.getActivityId(),nodeCommonContext.getBusinessKey());
//            if (CharSequenceUtil.equals(nodeCommonContext.getActivityId(), "userTask_bizDeptLeader")) {
//                final TrackEventInfo trackEventInfo = trackEventMapper.selectById(nodeCommonContext.getBusinessKey());
//                if (Objects.nonNull(trackEventInfo)) {
//                    Map<String, Object> varMap = new HashMap<>(1);
//                    varMap.put("processor", Objects.nonNull(trackEventInfo.getProcessorId()) ? ListUtil.toList(String.valueOf(trackEventInfo.getProcessorId())) : new ArrayList<>());
//                }
//            }
//        }
        //资产五级分类复核流程调整--资产复核岗审批通过后，若24小时后未进行确认则系统默认业务部门对分类结果完成确认，审批通过
        SystemConfig config = getBean(SystemConfigMapper.class).selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getConfigValue)
                .eq(SystemConfig::getConfigKey, "assetQueueTime")
                .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.AssetClassifyReview.name())&&Objects.equals("assetManagementReview", nodeCommonContext.getActivityId())) {
            if(ObjectUtil.isNotEmpty(config)&&StrUtil.isNotBlank(config.getConfigValue())){
                redisDelayedQueue.addQueueMinutes(nodeCommonContext.getProcessInstanceId(), Long.valueOf(config.getConfigValue()), AssetReviewExpirationListener.class);
            }else{
                redisDelayedQueue.addQueueHours(nodeCommonContext.getProcessInstanceId(), 24, AssetReviewExpirationListener.class);
            }
        }
        //资产五级分类复核流程调整--发起人提交后，若资产复核岗24小时后未进行确认则系统默认提交一岗
        if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.AssetClassifyReview.name())&&Objects.equals("userTask_startUser", nodeCommonContext.getActivityId())) {
            if(ObjectUtil.isNotEmpty(config)&&StrUtil.isNotBlank(config.getConfigValue())){
                redisDelayedQueue.addQueueMinutes(nodeCommonContext.getProcessInstanceId(), Long.valueOf(config.getConfigValue()), AssetManagementReviewExpirationListener.class);
            }else {
                redisDelayedQueue.addQueueHours(nodeCommonContext.getProcessInstanceId(), 24, AssetManagementReviewExpirationListener.class);
            }
        }
        //跟踪事项创建流程，发起人重新提交写入项目经理，项目经理、资产经理提交写入部门负责人，部门负责人提交写入风控经理
        if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.TrackEventCreateFlow.name())&&CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_startUser","userTask_assetManager", "userTask_project_manager2", "userTask_deptMaster")) {
            TrackEventInfo info = getBean(TrackEventService.class).getById(nodeCommonContext.getBusinessKey());
            final TrackEventContractInfoREQ req = new TrackEventContractInfoREQ();
            req.setContractCode(info.getContractCode());
            req.setBizId(info.getBizId());
            req.setProjName(info.getProjName());
            req.setClientId(info.getClientId());
            req.setBizSource(info.getBizSource());
            final TrackEventContractInfoRSP trackEventContractInfoRSP = trackEventService.contractInfo(req);
            if(Objects.nonNull(trackEventContractInfoRSP)){
                if (Objects.equals("userTask_startUser", nodeCommonContext.getActivityId())&&Objects.nonNull(trackEventContractInfoRSP.getProjSponsorUserId())) {
                    /*项目经理*/
                    List<String> projectManagerIds = new LinkedList<>();
                    projectManagerIds.add(String.valueOf(trackEventContractInfoRSP.getProjSponsorUserId()));
                    // 设置项目经理
                    flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_project_manager2"), projectManagerIds));
                }else if (CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_assetManager", "userTask_project_manager2")&&Objects.nonNull(trackEventContractInfoRSP.getBizDeptLeaderId())) {
                    /*部门负责人*/
                    List<String> deptLeaderIds = new LinkedList<>();
                    deptLeaderIds.add(String.valueOf(trackEventContractInfoRSP.getBizDeptLeaderId()));
                    // 设置部门负责人
                    flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_deptMaster"), deptLeaderIds));
                }else if (Objects.equals("userTask_deptMaster", nodeCommonContext.getActivityId())&&Objects.nonNull(trackEventContractInfoRSP.getRiskControlManagerId())) {
                    /*风控经理*/
                    List<String> riskControlManagerIds = new LinkedList<>();
                    riskControlManagerIds.add(String.valueOf(trackEventContractInfoRSP.getRiskControlManagerId()));
                    // 设置风控经理
                    flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_riskManager"), riskControlManagerIds));
                }
            }
        }
        //跟踪事项创建流程，流程结束抄送发起人
        if (Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.TrackEventCreateFlow.name())&&CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_riskDeptMaster","userTask_principal")) {
            TrackEventInfo info = getBean(TrackEventService.class).getById(nodeCommonContext.getBusinessKey());
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
            req.setCcUserIdList(ListUtil.toList((info.getCreateBy())));
            getBean(ExecutionService.class).cc(req);
        }

        /*保证金退抵流程 （手动+自动）在财务经理审批节点通过后记录处理人，后续流程用变量*/
        if ((Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.MarginFlowAuto.name()) || Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.MarginFlowManually.name()))
                &&Objects.equals("userTask_financeManager", nodeCommonContext.getActivityId())) {
            Map<String, Object> varMap = new HashMap<>();
            List<String> userTask_financeManagerUser = new LinkedList<>();
            userTask_financeManagerUser.add(String.valueOf(AccountUtil.getLoginInfo().getId()));
            varMap.put("userTask_financeManagerUser", userTask_financeManagerUser);
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_financeManager2"), userTask_financeManagerUser));
        }
        /*流程通过 抄送协办经理、抄送运营管理岗、运营管理部负责人*/
        if ((Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.MarginFlowAuto.name()) || Objects.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.MarginFlowManually.name()))
                &&Objects.equals("userTask_financeManager2", nodeCommonContext.getActivityId())) {
            Set<Long> ccUserIdSet = new HashSet();
            List<Long> headofyyglbList  = sysUserService.queryJobUserIds(JobEnum.headofyyglb.name()); //运营管理部负责人
            List<Long> operationmanagementagent  = sysUserService.queryJobUserIds(JobEnum.operationmanagementagent.name()); //运营管理部经办人
            ccUserIdSet.addAll(headofyyglbList);
            ccUserIdSet.addAll(operationmanagementagent);

            /*协办经理*/
            ContractRetreatInfo contractRetreatInfo =  contractRetreatInfoService.getById(Long.valueOf(nodeCommonContext.getBusinessKey()));
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.valueOf(contractRetreatInfo.getContractId()));
            String projCosponsorUserIds = contractBaseInfo.getProjCosponsorUserIds();
            if(ObjectUtil.isNotEmpty(projCosponsorUserIds)){
                Set<Long> projCosponsorUserIdsSet = JSON.parseObject(projCosponsorUserIds, new TypeReference<Set<Long>>() {});
                ccUserIdSet.addAll(projCosponsorUserIdsSet);
            }

            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
            req.setCcUserIdList(new ArrayList<>(ccUserIdSet));
            getBean(ExecutionService.class).cc(req);
        }
    }

    private boolean getSkipLawFlag(Long contractId) {
        // 确认合同文本类型
        SpringUtil.getBean(ContractTextInfoService.class).confirm(contractId);
        // 查询合同文本类型
        ContractTextInfo contractTextInfo = SpringUtil.getBean(ContractTextInfoService.class).getOneByContractId(contractId);
        boolean skipLawNode;
        if (Objects.isNull(contractTextInfo)) {
            skipLawNode = false;
        } else {
            if (StrUtil.isNotBlank(contractTextInfo.getTextType()) && Objects.equals(contractTextInfo.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode())) {
                List<String> textInfoTypeList = CharSequenceUtil.split(contractTextInfo.getTextType(), ",");
                skipLawNode = textInfoTypeList.contains(ContractTextTypeEnum.STANDARD_TEXT.name());
            } else {
                skipLawNode = false;
            }
        }
        return skipLawNode;
    }

    private void setNextNodeVal(NodeEndEvent event) {
        NodeCommonContext nodeCommonContext = event.getNodeCommonContext();
        Map<Boolean, Long> dataMap = new HashMap<>();
        // 直融
        List<FundDirectFinancingPledgeInfo> fundDirectFinancingPledgeInfos = fundDirectFinancingPledgeInfoService.lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getContractId, Long.valueOf(nodeCommonContext.getBusinessKey()))
                .list();

        if (CollUtil.isNotEmpty(fundDirectFinancingPledgeInfos)) {
            fundDirectFinancingPledgeInfos.forEach(fundDirectFinancingPledgeInfo -> {
                //  监管或者质押
                if (fundDirectFinancingPledgeInfo.getIsPledge() || fundDirectFinancingPledgeInfo.getIsSupervise()) {
                    Long financingId = fundDirectFinancingPledgeInfo.getFinancingId();
                    FundDirectFinancingBaseInfo financingBaseInfoServiceById = fundDirectFinancingBaseInfoService.getById(financingId);

                    if (Objects.isNull(financingBaseInfoServiceById)) {
                        return;
                    }
//                    //  变更日期 流程提交日期
//                    ProcessListREQ req = new ProcessListREQ();
//                    req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
//                    req.setPage(1);
//                    req.setPageSize(10);
//                    PageR<ProcessListRSP> list = myTaskService.searchList(req);
//                    ProcessListRSP rsp = list.getList().get(0);
//                    LocalDateTime updateTime = rsp.getStartTime();
                    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(nodeCommonContext.getProcessInstanceId()).singleResult();
                    LocalDateTime updateTime = DateUtil.toLocalDateTime(processInstance.getStartTime());

                    //  实际还款计划列表
                    FundDirectFinancingRepayActual fundDirectFinancingRepayActual = fundDirectFinancingRepayActualService.lambdaQuery()
                            .eq(FundDirectFinancingRepayActual::getFinancingId, financingId)
                            .orderByDesc(FundDirectFinancingRepayActual::getPhase)
                            .last(StringUtil.mysqlLimitOne())
                            .one();

                    //  若质押监管合同变更租金表时实际还款计划为空则不增加审批节点。
                    //  若质押监管合同变更日期晚于实际还款计划中最后一期还款日期则无需增加审批节点
                    if (!Objects.isNull(fundDirectFinancingRepayActual) && fundDirectFinancingRepayActual.getRepayDate().atStartOfDay().isAfter(updateTime)) {
                        if (Boolean.TRUE.equals(fundDirectFinancingPledgeInfo.getIsSupervise())) {
                            dataMap.put(Boolean.TRUE, financingBaseInfoServiceById.getFundManagerId());
                        } else {
                            dataMap.put(Boolean.FALSE, financingBaseInfoServiceById.getFundManagerId());
                        }
                    }
                }
            });
        }

        //  间融
        List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = fundFinancingPledgeInfoService.lambdaQuery()
                .eq(FundFinancingPledgeInfo::getContractId, Long.valueOf(nodeCommonContext.getBusinessKey()))
                .list();
        if (CollUtil.isNotEmpty(fundFinancingPledgeInfos)) {
            fundFinancingPledgeInfos.forEach(fundFinancingPledgeInfo -> {
                //  监管或者质押
                if (fundFinancingPledgeInfo.getIsPledge() || fundFinancingPledgeInfo.getIsSupervise()) {
                    Long financingId = fundFinancingPledgeInfo.getFinancingId();
                    //  融资状态为生效和起息的质押或监管合同变更需要增加审批节点，融资状态为新建、关闭、结清的不需要。
                    FundFinancingBaseInfo financingBaseInfoServiceById = fundFinancingBaseInfoService.getById(financingId);
                    if (Objects.nonNull(financingBaseInfoServiceById) &&
                            CharSequenceUtil.equalsAny(financingBaseInfoServiceById.getFinancingStatus(), FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name())) {
                        if (Boolean.TRUE.equals(fundFinancingPledgeInfo.getIsSupervise())) {
                            dataMap.put(Boolean.TRUE, financingBaseInfoServiceById.getFundManagerId());
                        } else {
                            dataMap.put(Boolean.FALSE, financingBaseInfoServiceById.getFundManagerId());
                        }
                    }
                }
            });
        }
        Map<String, Object> varMap = new HashMap<>();
        if (CollUtil.isEmpty(dataMap)) {
            varMap.put("isRegulatedOrPledged", Boolean.FALSE);
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
        } else {
            varMap.put("isRegulatedOrPledged", Boolean.TRUE);
            Long userId = dataMap.get(Boolean.TRUE);
            if (Objects.nonNull(userId)) {
                varMap.put(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_moneymanager"), Collections.singletonList(String.valueOf(userId)));
            } else {
                varMap.put(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_moneymanager"), Collections.singletonList(String.valueOf(dataMap.get(Boolean.FALSE))));
            }
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), varMap);
        }
    }

    private Long extractClientIdFromProjReviewBaseInfo(ProjReviewBaseInfo baseInfo) {
        ProjectBizType projectBizType = ProjectBizType.of(baseInfo.getBizType());
        // 获取客户id
        Long clientId = null;
        switch (projectBizType) {
            case ZL:
                clientId = JSONArray.parseArray(baseInfo.getLesseeInfo()).toJavaList(ClientInfo.class).get(0).getClientId();
                break;
            case BL:
                clientId = JSONArray.parseArray(baseInfo.getCreditorInfo()).toJavaList(ClientInfo.class).get(0).getClientId();
                break;
            case ZZ:
                clientId = JSONArray.parseArray(baseInfo.getLesseeInfo()).toJavaList(ClientInfo.class).get(0).getClientId();
                break;
            case ZR:
                clientId = JSONArray.parseArray(baseInfo.getCreditorInfo()).toJavaList(ClientInfo.class).get(0).getClientId();
                break;
        }
        return clientId;
    }

    /**
     * 计算客户的风险敞口
     *
     * @param clientId
     * @return
     */
    private Double calClientRiskExposure(Long clientId, ProjReviewBaseInfo baseInfo) {
        Long amount = contractBaseInfoService.getStockRiskExposure(clientId, baseInfo.getId(), BusinessModuleEnum.PROJ_REVIEW);
        //需要 - 本次申请的 保证金 - 本次申请的 首期租金
        String bizType = baseInfo.getBizType();
        ProjectBizType type = ProjectBizType.of(bizType);
        if (isNotNull(type)) {
            switch (type) {
                case ZL:
                case ZZ:
                    ProjReviewLeasePrice leasePrice = getBean(ProjReviewLeasePriceService.class).getByProjectId(baseInfo.getId());
                    if (null != leasePrice) {
                        amount -= Optional.ofNullable(leasePrice.getEarnestMoney()).orElse(0L);
                        amount -= Optional.ofNullable(leasePrice.getDownPayment()).orElse(0L);
                    }
                    break;
                case BL:
                    ProjReviewFactoringPrice factoringPrice = getBean(ProjReviewFactoringPriceService.class).getByProjectId(baseInfo.getId());
                    if (null != factoringPrice) {
                        amount -= Optional.ofNullable(factoringPrice.getEarnestMoney()).orElse(0L);
                    }
                    break;
                case ZR:
                    ProjReviewAocPrice aocPrice = getBean(ProjReviewAocPriceService.class).getByProjectId(baseInfo.getId());
                    if (null != aocPrice) {
                        amount -= Optional.ofNullable(aocPrice.getEarnestMoney()).orElse(0L);
                    }
                    break;
                default:
                    break;
            }
        }

        log.info("审批流中获取客户风险敞口[clientId: {}, amount: {}]", clientId, amount);
        return NumberUtil.div(amount.toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 集团授信风险敞口
     *
     * @param clientId
     * @return
     */
    @Deprecated
    private Double calGroupCreditRiskExposure(Long clientId, Long curApplyAmount) {
        Long amount = contractBaseInfoService.getGroupCreditStockRiskExposure(clientId) + LongUtil.null2zero(curApplyAmount);
        log.info("审批流中获取客户风险敞口[clientId: {}, amount: {}]", clientId, amount);
        return NumberUtil.div(amount.toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private Double calGroupCreditRiskExposureNew(Long clientId, GroupCreditReviewBaseInfo curGroupCreditReviewBaseInfo) {
        Long amount = contractBaseInfoService.getGroupCreditStockRiskExposure(clientId);
        long curAmount = 0L;
        if (Objects.nonNull(curGroupCreditReviewBaseInfo.getApplyCreditAmount())) {
            curAmount = curGroupCreditReviewBaseInfo.getApplyCreditAmount();
        }
        // 找当前授信评审的上一个生效版本
        ILib ilib = SpringUtil.getBean(GroupCreditReviewBaseInfoLibHandler.class).queryLatestDataByOriginId(curGroupCreditReviewBaseInfo.getId());
        if (Objects.nonNull(ilib) && ilib instanceof GroupCreditReviewBaseInfoLib) {
            GroupCreditReviewBaseInfoLib groupCreditReviewBaseInfoLib = (GroupCreditReviewBaseInfoLib) ilib;
            if (Objects.nonNull(groupCreditReviewBaseInfoLib.getApplyCreditAmount())) {
                curAmount = curAmount - groupCreditReviewBaseInfoLib.getApplyCreditAmount();
            }
        }
        amount = amount + curAmount;
        log.info("审批流中获取客户风险敞口[clientId: {}, amount: {}]", clientId, amount);
        return NumberUtil.div(amount.toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 判断客户是否在省内
     *
     * @param clientId
     * @return
     */
    private boolean clientInProvinceFlag(Long clientId) {
        if (Objects.isNull(clientId)) {
            return false;
        }
        CommonVersion commonVersion = clientVersionService.findNewestVersion(clientId);
        if (Objects.isNull(commonVersion)) {
            return false;
        }
        CorpAddressInfoLib workAddress = corpAddressInfoLibMapper.selectOne(Wrappers.<CorpAddressInfoLib>lambdaQuery()
                .eq(CorpAddressInfoLib::getClientId, clientId)
                .eq(CorpAddressInfoLib::getVersion, commonVersion.getVersion())
                .eq(CorpAddressInfoLib::getAddressType, CorpAddressType.WORK_ADDRESS.name())
                .last("LIMIT 1")
        );
        if (Objects.nonNull(workAddress)) {
            return MithrasConstants.ZHEJIANG_CODE.equals(workAddress.getProvince());
        }
        CorpAddressInfoLib registerAddress = corpAddressInfoLibMapper.selectOne(Wrappers.<CorpAddressInfoLib>lambdaQuery()
                .eq(CorpAddressInfoLib::getClientId, clientId)
                .eq(CorpAddressInfoLib::getVersion, commonVersion.getVersion())
                .eq(CorpAddressInfoLib::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name())
                .last("LIMIT 1")
        );
        if (Objects.nonNull(registerAddress)) {
            return MithrasConstants.ZHEJIANG_CODE.equals(registerAddress.getProvince());
        }
        return false;
    }

}
