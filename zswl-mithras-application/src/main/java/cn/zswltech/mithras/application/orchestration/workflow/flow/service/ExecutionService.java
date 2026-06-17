package cn.zswltech.mithras.application.orchestration.workflow.flow.service;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractDeductRentInfoService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.*;
import cn.zswltech.flow.core.domain.req.execution.*;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.NodeDefineResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.*;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.flow.core.service.impl.FlowAddSignRecordService;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.flow.execution.*;
import cn.zswltech.mithras.rating.service.RatingClientService;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.workflow.flow.convert.FlowExecutionConvert;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.enums.ProcessVarEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ResolutionTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportMaterialTypeEnum;
import cn.zswltech.mithras.leaseholdproperty.enums.LeaseFileTypeEnums;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialCommentsEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandlerFactory;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.workflow.persistence.model.remark.ProcessModifyRemark;
import cn.zswltech.mithras.contract.model.contract.*;
import cn.zswltech.mithras.creditreport.model.CreditReportClientItem;
import cn.zswltech.mithras.policy.persistence.model.PolicyInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewMaterial;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.workflow.flow.attention.ProcAttentionRecordService;
import cn.zswltech.mithras.workflow.process.ProcessModifyRemarkService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.application.orchestration.budget.BudgetPlanPayFlowService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.application.orchestration.contract.*;
import cn.zswltech.mithras.application.orchestration.contract.effectcheck.ContractEffectCheckFactory;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import cn.zswltech.mithras.creditreport.service.CreditReportClientItemService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishService;
import cn.zswltech.mithras.application.orchestration.credit.groupcredit.review.GroupCreditReviewService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemInfoService;
import cn.zswltech.mithras.customer.versioning.impl.ClientVersionServiceImpl;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentService;
import cn.zswltech.mithras.application.orchestration.policy.PolicyInfoService;
import cn.zswltech.mithras.application.orchestration.policy.PolicyInfoVersionService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewMaterialService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewMeetMinuteBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import cn.zswltech.mithras.contract.core.ContractRetreatInfoService;
import cn.zswltech.mithras.contract.core.ContractRentActualService;

/**
 * 流程相关 操作
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:48 PM
 */
@Slf4j
@Service
public class ExecutionService {
    // 协同需要处理动态表单的流程
    public static final List<String> COLLABORATE_NEED_HANDLE_DYNAMIC_FORM = ListUtil.toList(
            ProcessModelTypeEnum.NewAfterLeaseCheckReportFlow.name(),
            ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name()
    );

    @Resource
    private FlowExecutionApiService executionApiService;
    @Resource
    private FlowExecutionConvert flowExecutionConvert;
    @Resource
    private FlowModelApiService modelApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private DynamicFormHandlerFactory dynamicFormHandlerFactory;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowAddSignRecordService addSignRecordService;
    @Resource
    private FlowVariableApiService variableApiService;

    @Resource
    private ClientVersionServiceImpl clientVersionService;
    @Resource
    private ProjEstablishService projEstablishService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private ProjPricingService projPricingService;
    @Resource
    private GroupCreditEstablishService groupCreditEstablishService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;
    @Resource
    private AfterLeaseCheckExternalQueryService afterLeaseCheckExternalQueryService;
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private ContractService contractService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProcAttentionRecordService procAttentionRecordService;
    @Resource
    private PolicyInfoVersionService policyInfoVersionService;
    @Resource
    private PolicyInfoService policyInfoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private RiskControlWarnMonitorService riskControlWarnMonitorService;
    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;
    @Resource
    private BudgetPlanPayFlowService budgetPlanPayFlowService;
    @Resource
    private FilingMaterialsService filingMaterialsService;
    @Resource
    private ProjReviewMaterialService projReviewMaterialService;
    @Resource
    private ContractRetreatInfoService contractRetreatInfoService;
    @Resource
    private ContractDeductRentInfoService contractDeductRentInfoService;



    @Transactional(rollbackFor = Throwable.class)
    public void pass(ExecutionPassREQ req) {
        TaskResp systemTaskById = taskApiService.querySystemTaskById(req.getTaskId());
        TaskResp taskResp = systemTaskById;
        if (Objects.isNull(taskResp) || !TaskBusinessStatusEnum.RUNNING.getStatus().equals(taskResp.getTaskStatus())) {
            throw new MithrasException("任务不存在或已完成");
        }
        // 判断意见是否必填 不知道要不要 先放着吧
        if (ApprovalButtonTypeEnum.VOTE_BACK.name().equals(req.getButtonKey()) || ApprovalButtonTypeEnum.FOLLOWING.name().equals(req.getButtonKey())) {
            if (StringUtils.isBlank(req.getMessage())) {
                throw new MithrasException(String.format("%s时，需填写审批意见", ApprovalButtonTypeEnum.getByName(req.getButtonKey()).getDisplay()));
            }
        }
        //退抵流程中审批需要校验 资料清单是否完整，租金信息是否完整
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.MarginFlowAuto.name())) {
            ContractRetreatInfo contractRetreatInfo = contractRetreatInfoService.getById(taskResp.getBusinessKey());
            if (contractRetreatInfo.getDeductionAmount() > 0) {
                LambdaQueryWrapper<ContractDeductRentInfo> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(ContractDeductRentInfo::getRetreatInfoId, contractRetreatInfo.getId());
                List<ContractDeductRentInfo> contractDeductRentInfoList = contractDeductRentInfoService.list(wrapper);
                if (ObjectUtil.isEmpty(contractDeductRentInfoList)) {
                    throw new MithrasException("抵扣租金与内扣金额不匹配，请检查！");
                }
            }


            LambdaQueryWrapper<MaterialsList> materialsWrapper = Wrappers.lambdaQuery();
            materialsWrapper.in(MaterialsList::getBelongId, taskResp.getBusinessKey());
            materialsWrapper.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTARCT_DEPOSIT.name());
            List<MaterialsList> materialsLists = materialsListService.list(materialsWrapper);
            if(org.apache.commons.lang3.ObjectUtils.isEmpty(materialsLists)){
                throw new MithrasException("请上传保证金退抵文件！");
            }
        }
        //判断项目用户是否被占用
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name())) {
            ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoService.getById(Long.valueOf(taskResp.getBusinessKey()));
            //项目节点需判断是否客户已经被占用
            clientService.checkClientOccupy(baseInfo.getClientId(), baseInfo.getProjSponsorUserId());
        }
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name())) {
            ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(Long.valueOf(taskResp.getBusinessKey()));
            //项目节点需判断是否客户已经被占用
            clientService.checkClientOccupy(baseInfo.getClientId(), baseInfo.getProjSponsorUserId());
        }
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.LeaseCreateFlow.name(), ProcessModelTypeEnum.LeaseModifyFlow.name())) {
            // 租赁物审核管理需要在通过流程节点的时候校验
            leaseItemInfoService.checkData(Long.parseLong(taskResp.getBusinessKey()));
        }
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.PolicyReminderFlow.name(), ProcessModelTypeEnum.PolicyOverdueReminderFlow.name())) {
            policyInfoVersionService.checkRenewalInsurance(Long.parseLong(taskResp.getBusinessKey()));
        }
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.PaymentCreateFlow.name())) {
            // 合同面签是否允许移动端上传
            paymentService.checkContractSign(Long.parseLong(taskResp.getBusinessKey()));
        }
        if(CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.RiskControlNotPaymentFlow.name(), ProcessModelTypeEnum.RiskControlPaymentFlow.name())) {
            riskControlOpinionMonitorService.subCheck(Long.parseLong(taskResp.getBusinessKey()));
        }
        if (BusinessModuleEnum.RISK_WARN.getModelKeyList().contains(taskResp.getModelKey())) {
            riskControlWarnMonitorService.subCheck(Long.parseLong(taskResp.getBusinessKey()));
        }
        if(CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ContractLPRChangeFlow.name()
                , ProcessModelTypeEnum.ContractExtensionFlow.name(), ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name(), ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name())) {
            ContractBaseInfo contractBaseInfo =  contractBaseInfoService.getById(Long.parseLong(taskResp.getBusinessKey()));
            contractService.checkMortgagePledgeFile(contractBaseInfo);
        }
        if (FlowConstants.OPERATION_MANAGEMENT.equals(taskResp.getTaskActivityId())) {
            // 运营经理节点查询必传文件
            List<MaterialsList> materialsLists = materialsListService.list(
                    BusinessModuleEnum.LEASE_DATA_LIST.name(),
                    Collections.singletonList(LeaseFileTypeEnums.OPERATION_MANAGER_REVIEW_SUBMISSION.name()),
                    Collections.singletonList(Long.parseLong(taskResp.getBusinessKey()))
            );
            //operationManagement
            if (CollectionUtil.isEmpty(materialsLists)) {
                throw new MithrasException("资料清单中<运营经理上传-审核意见书>不能为空");
            }
        }

        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name())) {
            List<ContractRentActual> contractRentActualList = contractRentActualService.listByContract(Long.parseLong(taskResp.getBusinessKey()));
            if (CollectionUtil.isEmpty(contractRentActualList)) {
                throw new MithrasException("实际租金/支付表不存在，请先导入实际租金/支付表");
            }
            for (ContractRentActual contractRentActual : contractRentActualList) {
                if (StrUtil.isBlank(contractRentActual.getCashFlowCode())) {
                    throw new MithrasException("存在现金流编号为空的期限，请检查");
                }
            }
        }


        //征信查询校验数据
        if(StrUtil.equals(taskResp.getModelKey(), ProcessModelTypeEnum.CreditReportSelectFlow.name())) {
            //查询客户数据
            List<CreditReportClientItem> clientItems =SpringContextHolder.getBean(CreditReportClientItemService.class).listByBaseInfoId(Long.valueOf(taskResp.getBusinessKey()));
            Map<String, List<String>> map = SpringContextHolder.getBean(CreditReportBaseInfoService.class).checkEnterprise(clientItems, CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT.name(), BusinessModuleEnum.CREDIT_REPORT_SELECT.name());
            if (ObjectUtil.isNotEmpty(map) && map.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                map.forEach((k,v) -> {
                    stringBuilder.append(k);
                    stringBuilder.append(":缺少文件");
                    stringBuilder.append(v);
                    stringBuilder.append(";");
                });
                throw new MithrasException(stringBuilder.toString());
            }
        }
        // 月度计划流程待办流程字段校验
        if( ProcessModelTypeEnum.FinalPlanEventFlow.name().equals(taskResp.getModelKey())) {
            // 新增项目运营进度反馈、运营优先级在运营负责人节点必填
            if ("userTask_headofyyglb".equals(taskResp.getTaskActivityId())) {
                budgetPlanPayFlowService.checkDataForYy(Long.parseLong(taskResp.getBusinessKey()));
            }
            // 是否纳入资金计划在资金负责人节点必填
            if ("userTask_headofzj".equals(taskResp.getTaskActivityId())) {
                budgetPlanPayFlowService.checkDataForFz(Long.parseLong(taskResp.getBusinessKey()));
            }
        }

        if(ProcessModelTypeEnum.MonthPlanEventFlow.name().equals(taskResp.getModelKey())){
            // XMX-58 必填校验
            Long belongDeptId = null;
            Map<String, Object> variables = variableApiService.getVariables(taskResp.getProcessInstanceId(), Collections.singletonList(("belongDeptId")));
            List<String> canViewAllNode = Arrays.asList("userTask_deptMaster", "userTask_bizDivisionLeader", "userTask_financialmanager");
            if (CollectionUtil.isNotEmpty(variables) && variables.containsKey("belongDeptId") && canViewAllNode.contains(taskResp.getTaskActivityId())) {
                belongDeptId = (Long) variables.get("belongDeptId");
            }
            if("userTask_financialmanager".equals(taskResp.getTaskActivityId()) && Objects.nonNull(belongDeptId)){
                budgetPlanPayFlowService.checkDataForCw(Long.parseLong(taskResp.getBusinessKey().split("-")[0]), belongDeptId);
            }
            budgetPlanPayFlowService.checkData(Long.parseLong(taskResp.getBusinessKey().split("-")[0]), belongDeptId);
        }

        // 动态表单处理
        this.doDynamicForm(req.getDynamicFormData(), taskResp);
        // 数据校验
        ExecutionPassReq flowReq = flowExecutionConvert.passREQ2FlowReq(req);
        // 项目评审流程发起人节点特殊处理
        boolean isProjReview = CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                ProcessModelTypeEnum.ProjReviewModifyFlow.name(),
                ProcessModelTypeEnum.AfterLeaseExtendFlow.name(),
                ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name()
        );
        if (isProjReview) {
            // 如果是秘书汇票节点，重置特殊参数
            if (Objects.equals("userTask_jurySecretaryCollect", taskResp.getTaskActivityId())) {
                variableApiService.setVariables(taskResp.getProcessInstanceId(), MapUtil.of(FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION, Boolean.FALSE));
            }
            this.passSpecialProjReview(taskResp, req);
        }
        // 发起人节点 审批通过 校验数据完整性
        if (FlowConstants.START_USER_TASK.equals(taskResp.getTaskActivityId())) {
            checkDataValidation(taskResp);
        }
        //判断项目 评审会秘书节点 会议纪要是否完整
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                ProcessModelTypeEnum.ProjReviewModifyFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name())) {
            //
            if ("userTask_jurySecretaryCollect".equals(taskResp.getTaskActivityId())) {
                SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).subPass(taskResp.getProcessInstanceId());
            }
            if (FlowConstants.START_USER_TASK.equals(taskResp.getTaskActivityId())) {
                SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).checkCashFlowPlan(taskResp.getProcessInstanceId(), Long.parseLong(taskResp.getBusinessKey()));
            }
        }
        if (Objects.nonNull(systemTaskById)) {
            if (CharSequenceUtil.equalsAny(systemTaskById.getModelKey(), ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name())) {
                Long contractId = Long.valueOf(taskResp.getBusinessKey());
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
                if (Objects.nonNull(contractBaseInfo)) {
                    List<ContractTenantry> tList = getBean(ContractTenantryService.class).list(Wrappers.<ContractTenantry>lambdaQuery()
                            .eq(ContractTenantry::getResolutionType, ResolutionTypeEnum.OTHER.name())
                            .eq(ContractTenantry::getResolutionFileId, JSON.toJSONString(Collections.emptyList()))
                            .eq(ContractTenantry::getContractId, contractId));
                    List<ContractGuarantor> gList = getBean(ContractGuarantorService.class).list(Wrappers.<ContractGuarantor>lambdaQuery()
                            .eq(ContractGuarantor::getResolutionType, ResolutionTypeEnum.OTHER.name())
                            .eq(ContractGuarantor::getResolutionFileId, JSON.toJSONString(Collections.emptyList()))
                            .eq(ContractGuarantor::getContractId, contractId));
                    if (CollectionUtils.isNotEmpty(tList) || CollectionUtils.isNotEmpty(gList)) {
                        throw new MithrasException("决议类型为其他的《决议文件》不存在，请上传《决议文件》后再提交流程！");
                    }
                    // 校验
                    ContractEffectCheckFactory.getInstance(contractBaseInfo.getBizType()).check(contractBaseInfo, false);
                }
            }
        }
        procAttentionRecordService.recordAttention(taskResp.getProcessInstanceId(), AccountUtil.getLoginInfo().getId(), req.getAttentionFlag());
        executionApiService.pass(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void reject(ExecutionTaskBaseREQ req) {
        TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
        ExecutionTaskBaseReq flowReq = flowExecutionConvert.taskBaseREQ2FlowReq(req);
        procAttentionRecordService.recordAttention(taskResp.getProcessInstanceId(), AccountUtil.getLoginInfo().getId(), req.getAttentionFlag());
        executionApiService.reject(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void backToStartUser(ExecutionBackToStartUserREQ req) {
        if (StringUtils.isBlank(req.getMessage())) {
            throw new MithrasException("退回发起人时审批意见必填");
        }
        TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
        // 如果是直达本节点，先规避并行网关分支，该场景需要对二方包进行深度修改，风险极大
//        if (Objects.equals(req.getBackType(), 2)) {
//            this.checkBackNodeInParallelGateway(taskResp);
//        }
        this.backProjReviewOnRiskManager(req,taskResp);
        // 特殊逻辑
        if (ApprovalButtonTypeEnum.ZL_PR_CONDITION_AGREE.name().equals(req.getButtonKey()) || ApprovalButtonTypeEnum.ZL_PR_AGREE.name().equals(req.getButtonKey())) {
            if ("userTask_jurySecretaryCollect".equals(taskResp.getTaskActivityId())) {
                SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).subPass(taskResp.getProcessInstanceId());
            }
            // 处理动态表单
            this.doDynamicForm(req.getDynamicFormData(), taskResp);
            req.setBackType(1);
            Map<String, Object> map = new HashMap<>();
            map.put(FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION, Boolean.FALSE);
            map.put(FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE, FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE_CONDITION_AGREE);
            map.put(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK, Boolean.TRUE);
            variableApiService.setVariables(taskResp.getProcessInstanceId(), map);
        } else if (ApprovalButtonTypeEnum.ZL_PR_MEETING_SECRETARY_DISAGREE.name().equals(req.getButtonKey())) {
            if ("userTask_jurySecretaryCollect".equals(taskResp.getTaskActivityId())) {
                SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).subPass(taskResp.getProcessInstanceId());
            }
            // 处理动态表单
            this.doDynamicForm(req.getDynamicFormData(), taskResp);
            req.setBackType(1);
            Map<String, Object> map = new HashMap<>();
            map.put(FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION, Boolean.FALSE);
            map.put(FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE, FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE_DISAGREE);
            map.put(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK, Boolean.TRUE);
            variableApiService.setVariables(taskResp.getProcessInstanceId(), map);
        }
        ExecutionBackToStepReq flowReq = flowExecutionConvert.backToStartUserREQ2FlowReq(req);
        procAttentionRecordService.recordAttention(taskResp.getProcessInstanceId(), AccountUtil.getLoginInfo().getId(), req.getAttentionFlag());
        executionApiService.backToStep(flowReq);

        ProcessPageReq taskReq = new ProcessPageReq();
        taskReq.setSortType(1);
        taskReq.setProcessInstanceIdList(Collections.singletonList(taskResp.getProcessInstanceId()));
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(taskReq);
        if (ObjectUtil.isNotEmpty(flowRespPage) && ObjectUtil.isNotEmpty(flowRespPage.getContents()) && ObjectUtil.equals(flowRespPage.getContents().get(0).getStartUserId(), String.valueOf(GlobalConstants.READONLY_ID))
                && CharSequenceUtil.equalsAny(flowRespPage.getContents().get(0).getModelKey(), ProcessModelTypeEnum.PolicyReminderFlow.name(), ProcessModelTypeEnum.PolicyOverdueReminderFlow.name())) {
            //退回发起人的
            //结束流程
            ExecutionProcessBaseREQ rejectReq = new ExecutionProcessBaseREQ();
            rejectReq.setProcessInstanceId(taskResp.getProcessInstanceId());
            rejectReq.setMessage("发起人为超管,系统关闭流程");
            executionService.rejectAll(rejectReq);
        }
        if(ObjectUtil.isNotEmpty(flowRespPage) && ObjectUtil.isNotEmpty(flowRespPage.getContents())
                && CharSequenceUtil.equalsAny(flowRespPage.getContents().get(0).getModelKey(), ProcessModelTypeEnum.FilingMaterialsApplyFlow.name())){
            String businessKey = flowRespPage.getContents().get(0).getBusinessKey();
            filingMaterialsService.updateFilingMaterialsReturnDate(businessKey,null, LocalDate.now());
            filingMaterialsService.updateFilingLedgeField(taskResp.getTaskActivityId(),Long.parseLong(businessKey),req.getMessage());
        }

    }

    @Transactional(rollbackFor = Throwable.class)
    public void backToStep(ExecutionBackToStepREQ req) {

        if (Objects.equals(req.getButtonKey(), ApprovalButtonTypeEnum.VOTE_BACK.name())) {
            // 如果是评审会议纪要退回按钮，则默认退回上一个流程节点
            req.setBackType(1);
            req.setActivityId("userTask_jurySecretaryCollect");
        }
        if (ApprovalButtonTypeEnum.BACK_TO_STEP.name().equals(req.getActivityId())) {
            throw new MithrasException("退回节点不能为空");
        }
        if (ApprovalButtonTypeEnum.BACK_TO_STEP.name().equals(req.getButtonKey())) {
            if (StringUtils.isBlank(req.getMessage())) {
                throw new MithrasException(String.format("%s时，需填写审批意见", ApprovalButtonTypeEnum.getByName(req.getButtonKey()).getDisplay()));
            }
        }
        TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
        this.backProjReviewOnRiskManager(req,taskResp);
//        // 复议只能点一次
//        if (ApprovalButtonTypeEnum.ZL_PR_RECONSIDER.name().equals(req.getButtonKey())) {
//            TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
//            if (Objects.isNull(taskResp) || !TaskBusinessStatusEnum.RUNNING.getStatus().equals(taskResp.getTaskStatus())) {
//                throw new MithrasException("任务不存在或已完成");
//            }
//            Integer reconsiderCount = processApiService.queryExecutionTimes(taskResp.getProcessInstanceId(), CommentTypeEnum.ZL_PR_RECONSIDER);
//            if (reconsiderCount > 0) {
//                throw new MithrasException("只允许复议一次");
//            }
//        }
        ExecutionBackToStepReq flowReq = flowExecutionConvert.backToStepREQ2FlowReq(req);
        // 判断按钮类型 定制化写死一些逻辑
//        if (ApprovalButtonTypeEnum.ZL_PR_RECONSIDER.name().equals(req.getButtonKey())) {
//            flowReq.setTaskActivityId(FlowConstants.START_USER_TASK);
//            flowReq.setJumpToSourceFlag(0);
//        } else
        if (ApprovalButtonTypeEnum.ZL_PR_RE_VOTE.name().equals(req.getButtonKey())) {
            Map<String, Object> variables = variableApiService.getVariables(taskResp.getProcessInstanceId(), Collections.singletonList(ProcessVarEnum.projReviewHasJuryDirector.name()));
            if(variables != null && variables.containsKey(ProcessVarEnum.projReviewHasJuryDirector.name()) && (boolean) variables.get(ProcessVarEnum.projReviewHasJuryDirector.name())){
                flowReq.setTaskActivityId("userTask_juryMeetingReview_member");
            }else{
                flowReq.setTaskActivityId("userTask_juryMeetingReview");
            }
            flowReq.setJumpToSourceFlag(0);
        }
        // TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
        procAttentionRecordService.recordAttention(taskResp.getProcessInstanceId(), AccountUtil.getLoginInfo().getId(), req.getAttentionFlag());
        executionApiService.backToStep(flowReq);

        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.FilingMaterialsApplyFlow.name())
                && Objects.equals(req.getActivityId(), FilingMaterialsConstants.TASK_NODE_1)) {
            filingMaterialsService.updateFilingMaterialsReturnDate(taskResp.getBusinessKey(),null, LocalDate.now());
            filingMaterialsService.updateFilingLedgeField(taskResp.getTaskActivityId(),Long.parseLong(taskResp.getBusinessKey()),req.getMessage());
        }
        // 修改状态（）
//        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(),ProcessModelTypeEnum.YearHalfOtherPlanEventFlow.name(),ProcessModelTypeEnum.MonthPlanEventFlow.name())) {
//            budgetPlanPayFlowService.restartConfirmStatus(taskResp);
//        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void withdrawTask(ExecutionTaskBaseREQ req) {
        TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
        ExecutionTaskBaseReq flowReq = flowExecutionConvert.taskBaseREQ2FlowReq(req);
        procAttentionRecordService.recordAttention(taskResp.getProcessInstanceId(), AccountUtil.getLoginInfo().getId(), req.getAttentionFlag());
        executionApiService.withdrawTask(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void withdrawToStartUser(ExecutionProcessBaseREQ req) {
        checkWithdrawToStartUser(req);
        ExecutionWithdrawProcessReq flowReq = flowExecutionConvert.processBaseREQ2WithdrawReq(req);
        flowReq.setTaskActivityId(FlowConstants.START_USER_TASK);
        executionApiService.withdrawProcessToTargetNode(flowReq);
        ProcessPageReq taskReq = new ProcessPageReq();
        taskReq.setSortType(1);
        taskReq.setProcessInstanceIdList(Collections.singletonList(req.getProcessInstanceId()));
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(taskReq);
        if (ObjectUtil.isNotEmpty(flowRespPage) && CollUtil.isNotEmpty(flowRespPage.getContents()) && CharSequenceUtil.equalsAny(flowRespPage.getContents().get(0).getModelKey(), ProcessModelTypeEnum.FilingMaterialsApplyFlow.name())) {
            filingMaterialsService.updateFilingMaterialsReturnDate(flowRespPage.getContents().get(0).getBusinessKey(),null,null);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void cancelProcess(ExecutionProcessBaseREQ req) {
        if (ObjectUtil.isNotEmpty(req.getScene()) && ObjectUtil.equals(req.getScene(), YesOrNoNumberEnum.YES.getCode())) {
            //流程关闭校验
            checkCancelProcess(req);
        }
        ExecutionProcessBaseReq flowReq = flowExecutionConvert.processBaseREQ2FlowReq(req);
        executionApiService.cancel(flowReq);
    }

    private void checkCancelProcess(ExecutionProcessBaseREQ req) {
        //查询流程
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(10);
        processPageReq.setProcessInstanceIdList(Collections.singletonList(req.getProcessInstanceId()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        if (ObjectUtil.isEmpty(processRespPage) || ObjectUtil.isEmpty(processRespPage.getContents())) {
            return;
        }
        if (ObjectUtil.equals(ProcessModelTypeEnum.RatingClientUpdateFlow.name(), processRespPage.getContents().get(0).getModelKey())) {
            SpringContextHolder.getBean(RatingClientService.class).checkCancelProcess(Long.valueOf(processRespPage.getContents().get(0).getBusinessKey()));
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void cc(ExecutionProcessBaseREQ req) {
        ExecutionProcessBaseReq flowReq = flowExecutionConvert.processBaseREQ2FlowReq(req);
        executionApiService.cc(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void collaborate(ExecutionCollaborateREQ req) {
        TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
        ExecutionBeforeAddSignReq flowReq = flowExecutionConvert.collaborateREQ2FlowReq(req);
        procAttentionRecordService.recordAttention(taskResp.getProcessInstanceId(), AccountUtil.getLoginInfo().getId(), req.getAttentionFlag());
        executionApiService.beforeAddSign(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void transfer(ExecutionTransferREQ req) {
        ExecutionTransferReq flowReq = flowExecutionConvert.transferREQ2FlowReq(req);
        executionApiService.transfer(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void jump(ExecutionJumpREQ req) {
        ExecutionJumpReq flowReq = flowExecutionConvert.jumpREQ2FlowReq(req);
        executionApiService.jump(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void passAll(ExecutionProcessBaseREQ req) {
        ExecutionProcessBaseReq flowReq = flowExecutionConvert.processBaseREQ2FlowReq(req);
        executionApiService.passAll(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void rejectAll(ExecutionProcessBaseREQ req) {
        // 校验模块 不是每个模块都有审批拒绝
        ProcessResp processResp = taskApiService.queryProcessById(req.getProcessInstanceId());
        if (Objects.isNull(processResp)) {
            throw new MithrasException("流程不存在");
        }
        //信科部要求放开这个白名单，所有流程都支持一键通过和一键拒绝
        /*if (!CharSequenceUtil.equalsAny(processResp.getModelKey(),
                // 项目评审
                ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                ProcessModelTypeEnum.ProjReviewModifyFlow.name(),
                ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name(),
                // 租金催收
                ProcessModelTypeEnum.RentCollectionExemptionFlow.name(),
                // 付款
                ProcessModelTypeEnum.PaymentCreateFlow.name(),
                // 客户移交
                ProcessModelTypeEnum.ClientTransferFlow.name(),
                // 授信评审
                ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name(),
                // 租后
                ProcessModelTypeEnum.AfterLeaseExtendFlow.name(),
                ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name(),
                // 租后
                ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name(),
                ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishModifyFlow.name(),
                ProcessModelTypeEnum.NewAfterLeaseCheckPlanFinishFlow.name(),
                ProcessModelTypeEnum.NewAfterLeaseCheckReportFlow.name(),
                ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name(),
                ProcessModelTypeEnum.NewAfterLeaseCheckExternalQueryFlow.name(),
                // 融资
                ProcessModelTypeEnum.FundFinancingCreateFlow.name(),
                //20240828 信科部要求清理长时间未通过流程，先放开以下流程，后续是否关闭待定
                ProcessModelTypeEnum.AfterLeaseCheckExternalQueryFlow.name(),
                ProcessModelTypeEnum.PaymentCreateFlow.name(),
                ProcessModelTypeEnum.ContractCreateFlow.name(),
                ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name(),
                ProcessModelTypeEnum.ClientModifyFlow.name(),
                ProcessModelTypeEnum.FundFinancingCreateFlow.name(),
                ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name(),
                ProcessModelTypeEnum.ProjEstablishCreateFlow.name(),
                ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name(),
                ProcessModelTypeEnum.RiskControlOpinionHandleFlow.name(),
                ProcessModelTypeEnum.RiskControlOpinionHandleAfterLaunchFlow.name(),
                ProcessModelTypeEnum.NewAfterLeaseCheckPlanFinishFlow.name(),
                ProcessModelTypeEnum.LeaseCreateFlow.name()
        )) {
            // 只有这些流程有审批拒绝
            throw new MithrasException(String.format("该流程模型类型[%s]，审批流程中无审批拒绝按钮，请检查", processResp.getModelName()));
        }*/
        ExecutionProcessBaseReq flowReq = flowExecutionConvert.processBaseREQ2FlowReq(req);
        executionApiService.rejectAll(flowReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void randomReturn(ExecutionRandomReturnREQ req) {
        ExecutionRandomReturnReq flowReq = flowExecutionConvert.randomReturnREQ2FlowReq(req);
//        TaskResp taskResp = taskApiService.querySystemTaskById(req.getTaskId());
//        if (Objects.equals(req.getBackType(), 2)) {
//            this.checkBackNodeInParallelGateway(taskResp);
//        }
        executionApiService.randomReturn(flowReq);
        ProcessPageReq taskReq = new ProcessPageReq();
        taskReq.setSortType(1);
        taskReq.setProcessInstanceIdList(Collections.singletonList(req.getProcessInstanceId()));
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(taskReq);
        if (ObjectUtil.isNotEmpty(flowRespPage) && CollUtil.isNotEmpty(flowRespPage.getContents())
                && CharSequenceUtil.equalsAny(flowRespPage.getContents().get(0).getModelKey(), ProcessModelTypeEnum.FilingMaterialsApplyFlow.name())
                && Objects.equals(req.getActivityId(), FilingMaterialsConstants.TASK_NODE_1)) {
            String businessKey = flowRespPage.getContents().get(0).getBusinessKey();
            filingMaterialsService.updateFilingMaterialsReturnDate(businessKey, null, LocalDate.now());
            filingMaterialsService.updateFilingLedgeField(flowReq.getTaskActivityId(),Long.parseLong(businessKey),req.getMessage());
        }
    }

    // 项目评审风控节点回退,处理材料审核表
    private void backProjReviewOnRiskManager(ExecutionTaskBaseREQ req,TaskResp taskResp){
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(),
                ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name(),
                ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name())) {
            String projReviewId = taskResp.getBusinessKey();
            if("userTask_riskManager".equals(taskResp.getTaskActivityId())){
                List<ProjReviewMaterial> reviewMaterialsByReviewId = projReviewMaterialService.getReviewMaterialsByReviewId(Long.parseLong(projReviewId));
                String reviewInstructions = reviewMaterialsByReviewId.stream()
                        .filter(item -> (!StrUtil.isBlank(item.getReviewInstructions()) && ProjReviewMaterialCommentsEnum.SUPPLEMENT_INFO.name().equals(item.getReviewComments())))
                        .map(item -> item.getName()+"-"+item.getReviewInstructions()).collect(Collectors.joining(","));
                req.setMessage(req.getMessage()+reviewInstructions);
            }
            projReviewMaterialService.updateVersionByReviewId(Long.parseLong(projReviewId));
        }
    }

    private void passSpecialProjReview(TaskResp taskResp, ExecutionPassREQ req) {
        Map<String, Object> map = variableApiService.getVariables(taskResp.getProcessInstanceId(), Arrays.asList(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK, FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION));
        boolean isMeetingBack = Objects.equals(Boolean.TRUE, map.get(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK));
        if (isMeetingBack) {
            Map<String, Object> toSetMap = new HashMap<>();
            if (Objects.equals(FlowConstants.START_USER_TASK, taskResp.getTaskActivityId())) {
                // 发起人节点
                // 根据按钮类型不同填充不同变量
                if (ApprovalButtonTypeEnum.SUBMIT.name().equals(req.getButtonKey())) {
                    toSetMap.put(FlowConstants.PROJ_REVIEW_START_USER_CHOICE, FlowConstants.PROJ_REVIEW_START_USER_CHOICE_COMMIT);
                } else if (ApprovalButtonTypeEnum.ZL_PR_RECONSIDER.name().equals(req.getButtonKey())) {
                    if (!TaskBusinessStatusEnum.RUNNING.getStatus().equals(taskResp.getTaskStatus())) {
                        throw new MithrasException("任务不存在或已完成");
                    }
                    // 先校验是否在复议作用域内，不在的话判断是否可进行复议
                    Object result = map.get(FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION);
                    if (Objects.isNull(result) || Objects.equals(result, Boolean.FALSE)) {
                        // 校验复议次数
                        Integer reconsiderCount = processApiService.queryExecutionTimes(taskResp.getProcessInstanceId(), CommentTypeEnum.ZL_PR_RECONSIDER);
                        if (reconsiderCount > 0) {
                            throw new MithrasException("只允许复议一次");
                        }
                    }
                    toSetMap.put(FlowConstants.PROJ_REVIEW_START_USER_CHOICE, FlowConstants.PROJ_REVIEW_START_USER_CHOICE_RECONSIDERATION);
                    toSetMap.put(FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION, Boolean.TRUE);
                    //
                    if (null != req.getRemarkAddREQ()) {
                        req.getRemarkAddREQ().check();
                        getBean(ProcessModifyRemarkService.class).saveOrUpdateByKey(copyProperties(req.getRemarkAddREQ(), ProcessModifyRemark.class));
                    }
                }
            } else if (Objects.equals("userTask_jurySecretaryCollect", taskResp.getTaskActivityId())) {
                // 评审会秘书汇票节点
                toSetMap.put(FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE, FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE_AGREE);
                toSetMap.put(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK, Boolean.FALSE);
            }
            if (CollectionUtil.isNotEmpty(toSetMap)) {
                variableApiService.setVariables(taskResp.getProcessInstanceId(), toSetMap);
            }
        }
    }

    private void doDynamicForm(Map<String, Object> formMap, TaskResp taskResp) {
        boolean collaborateFlag = Objects.nonNull(addSignRecordService.findByTaskId(taskResp.getTaskId()));
        if (collaborateFlag && !COLLABORATE_NEED_HANDLE_DYNAMIC_FORM.contains(taskResp.getModelKey())) {
            // 协同人不需要处理表单逻辑
            return;
        }
        UserTaskExt userTaskExt = modelApiService.findUserTaskExtByProcessDefinitionId(taskResp.getProcessDefineId(), taskResp.getTaskActivityId());
        if (CollectionUtil.isEmpty(userTaskExt.getDynamicFormList())) {
            return;
        }
        // 校验
        userTaskExt.getDynamicFormList().forEach(e -> {
            DynamicFormHandler handler = dynamicFormHandlerFactory.getHandler(e);
            if (Objects.isNull(handler)) {
                return;
            }
            handler.check(formMap, taskResp, userTaskExt);
        });
        // 处理
        userTaskExt.getDynamicFormList().forEach(e -> {
            DynamicFormHandler handler = dynamicFormHandlerFactory.getHandler(e);
            if (Objects.isNull(handler)) {
                return;
            }
            handler.handle(formMap, taskResp, userTaskExt);
        });
    }

    private void checkDataValidation(TaskResp taskResp) {
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ClientModifyFlow.name(), ProcessModelTypeEnum.ClientAuthorityCreateFlow.name(), ProcessModelTypeEnum.ClientAuthorityModifyFlow.name())) {
            clientVersionService.validateData(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name())) {
            projEstablishService.effectCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name())) {
            projReviewService.effectCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name(), ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.name())) {
            projPricingService.pricingApprovalCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(), ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name())) {
            groupCreditEstablishService.effectCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name())) {
            groupCreditReviewService.effectCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.PaymentCreateFlow.name())) {
            paymentService.effectCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.AfterLeaseExtendFlow.name(), ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name())) {
            afterLeaseAdjustInfoService.checkDetail(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.NewAfterLeaseCheckExternalQueryFlow.name())) {
            afterLeaseCheckExternalQueryService.submitCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name(), ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishModifyFlow.name())) {
            afterLeaseCheckPlanBaseService.publishCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.NewAfterLeaseCheckReportFlow.name())) {
            afterLeaseCheckPlanClientService.submitApprovalCheck(Long.valueOf(taskResp.getBusinessKey()));
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name(), ProcessModelTypeEnum.ContractStartRentFlow.name(), ProcessModelTypeEnum.ContractEarlyRepayFlow.name())) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.valueOf(taskResp.getBusinessKey()));
            if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ContractCreateFlow.name())) {
                ContractEffectCheckFactory.getInstance(contractBaseInfo.getBizType()).check(contractBaseInfo, true);
            } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ContractStartRentFlow.name())) {
                contractService.startRentCheck(contractBaseInfo);
            } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ContractEarlyRepayFlow.name())) {
//                contractService.contractPrepaymentCheck(contractBaseInfo);
            } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ContractModifyFlow.name())) {
                ContractEffectCheckFactory.getInstance(contractBaseInfo.getBizType()).check(contractBaseInfo, true);
            }
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.PolicyModifyFlow.name(), ProcessModelTypeEnum.PolicyCreateFlow.name())) {
            PolicyInfo baseInfo = policyInfoService.getById(Long.valueOf(taskResp.getBusinessKey()));
            policyInfoVersionService.effectCheck(baseInfo);
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionModifyFlow.name())) {
            kpiProjectDistributionService.checkBeforeSubmit(Long.valueOf(taskResp.getBusinessKey()));
        }
    }

    private boolean hasParallelGateway(List<NodeDefineResp> nodeDefineRespList) {
        for (NodeDefineResp nodeDefineResp : nodeDefineRespList) {
            if (Objects.equals(nodeDefineResp.getType(), NodeDefineEnum.PARALLEL_GATEWAY.getType())) {
                return true;
            }
        }
        return false;
    }

    private void checkWithdrawToStartUser(ExecutionProcessBaseREQ req) {
        ProcessResp flowResp = taskApiService.queryProcessById(req.getProcessInstanceId());
        if (ObjectUtils.isEmpty(flowResp)) {
            throw new MithrasException("流程信息不存在");
        }
        // 判断当前节点是否在发起人节点，防止停留在页面上时流程已经被退回（这种场景会使流程实例产生异常分支而无法结束）
        if (Objects.equals(flowResp.getCurTaskActivityIds(), FlowConstants.START_USER_TASK)) {
            throw new MithrasException("当前流程已处于发起人节点，不允许撤回，请刷新页面");
        }
        if (StrUtil.equalsAny(flowResp.getModelKey(), ProcessModelTypeEnum.LeaseCreateFlow.name(), ProcessModelTypeEnum.LeaseModifyFlow.name())) {
            throw new MithrasException("租赁物审核不允许撤回");
        }
    }

//    private void checkBackNodeInParallelGateway(TaskResp taskResp) {
//        // 特殊逻辑，为了保证老流程没有这个限制，需判断流程模型中是否包含并行网关节点
//        List<NodeDefineResp> nodeDefineRespList = modelApiService.getNodeDefineListByProcessDefinitionId(taskResp.getProcessDefineId());
//        boolean hasParallelGateway = this.hasParallelGateway(nodeDefineRespList);
//        log.info("审批人选择退回后直达本节点[taskId:{}, hasParallelGateway:{}]", taskResp.getTaskId(), hasParallelGateway);
    ////            boolean isParallelNode = FlowConstants.PARALLEL_ACTIVITY_ID_LIST.contains(taskResp.getTaskActivityId());
//        if (hasParallelGateway) {
//            // FIXME 目前并行网关分支中只有一个节点，如果出现多节点，判断逻辑需要改造
//            Map<String, NodeDefineResp> map = nodeDefineRespList.stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, e -> e));
//            for (NodeDefineResp nodeDefineResp : nodeDefineRespList) {
//                if (Objects.equals(taskResp.getTaskActivityId(), nodeDefineResp.getActivityId())) {
//                    if (this.nodeIsInParallelGateway(nodeDefineResp, map)) {
//                        throw new MithrasException("并行流程节点暂不支持退回后直达本节点，请选择逐级审批");
//                    }
//                }
//            }
//        }
//    }

//    private boolean nodeIsInParallelGateway(NodeDefineResp nodeDefineResp, Map<String, NodeDefineResp> map) {
//        boolean parallelGateWayIn = false;
//        boolean parallelGateWayOut = false;
//        List<String> incomingIds = nodeDefineResp.getIncomingIds();
//        List<String> outgoingIds = nodeDefineResp.getOutgoingIds();
//        if (CollectionUtil.isNotEmpty(incomingIds)) {
//            for (String id : incomingIds) {
//                NodeDefineResp ndr = map.get(id);
//                if (Objects.nonNull(ndr) && Objects.equals(ndr.getType(), NodeDefineEnum.PARALLEL_GATEWAY.getType())) {
//                    parallelGateWayIn = true;
//                }
//            }
//        }
//        if (CollectionUtil.isNotEmpty(outgoingIds)) {
//            for (String id : outgoingIds) {
//                NodeDefineResp ndr = map.get(id);
//                if (Objects.nonNull(ndr) && Objects.equals(ndr.getType(), NodeDefineEnum.PARALLEL_GATEWAY.getType())) {
//                    parallelGateWayOut = true;
//                }
//            }
//        }
//        return parallelGateWayIn && parallelGateWayOut;
//    }

    //当前节点自动提交
    @Transactional(rollbackFor = Throwable.class)
    public void currentNodeAutoCommit(TaskResp task, String handlerId, String message) {
        if(ObjectUtil.isEmpty(task)) {
            return;
        }
        //自动通过
        ExecutionPassReq passReq = new ExecutionPassReq();
        passReq.setHandlerId(handlerId == null ? String.valueOf(GlobalConstants.READONLY_ID) : handlerId);
        passReq.setMessage(message);
        passReq.setTaskId(task.getTaskId());
        passReq.setCheckAssignee(Boolean.FALSE);
        passReq.setButtonKey(PassBizTypeEnum.AGREE.name());
        executionApiService.pass(passReq);
    }
}
