package cn.zswltech.mithras.service.service.payment;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.payment.dto.PaymentCheckApplyAmountReq;
import cn.zswltech.mithras.api.payment.dto.PaymentCheckApplyAmountRsp;
import cn.zswltech.mithras.api.payment.register.RegisterSaveReq;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.payment.*;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projlifecycle.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.third.enums.FinancialPaymentCodeENUM;
import cn.zswltech.mithras.service.flow.helper.CalBoardRuleHelper;
import cn.zswltech.mithras.service.gendoc.PaymentLoanReviewFileRender;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayDetail;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetail;
import cn.zswltech.mithras.service.mapper.model.payment.pubInfo.PublicInfoQuery;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.Listener.collection.CollectionAddEvent;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayDetailService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.lib.payment.libservice.impl.PaymentVersionServiceImpl;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.margin.MarginBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.pubinfo.PublicInfoQueryService;
import cn.zswltech.mithras.service.service.projlifecycle.ProjectLifecycleEventService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.service.financial.vo.FinancialPaymentVO;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.service.enums.JobEnum.leaderincharge;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 10:32
 */
@Slf4j
@Service
public class PaymentService implements FlowEndEventProcessor {
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentPolicyInfoService paymentPolicyInfoService;
    @Resource
    private PaymentVersionServiceImpl paymentVersionService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private PaymentPlanedDetailService planedDetailService;
    @Resource
    private CalBoardRuleHelper calBoardRuleHelper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjectLifecycleEventService projectLifecycleEventService;
    @Resource
    private SysUserService sysUserService;
    @Autowired
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private PaymentLoanReviewFileRender paymentLoanReviewFileRender;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private BudgetPlanPayDetailService budgetPlanPayDetailService;

    @Value("${youshu.autoRegister.saveUrl}")
    private String saveUrl;
    @Value("${oss.minio.down.expiry:86400}")
    private Integer expiry;
    @Resource
    private PaymentPlanedDetailService paymentPlanedDetailService;

    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long paymentId) {
        PaymentBaseInfo baseInfo = paymentBaseInfoService.getById(paymentId);
        ProcessResp relatedProcess = findRelatedProcess(baseInfo.getId());
        if (ObjectUtil.isNotNull(relatedProcess)) {
            throw new MithrasException("已在流程中，不允许重复提交审批");
        }
        effectCheck(baseInfo);

        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(baseInfo.getContractId());
        if (ObjectUtil.isNotNull(projReviewBaseInfoService.findRelatedProcess(contractBaseInfo.getProjReviewId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交付款申请流程！");
        }
        if (contractBaseInfo.getIsSigned().equals(0)) {
            throw new MithrasException("请确认是否允许移动端上传面签照片、视频");
        }
        Boolean projReviewNeedBoardApproveFlag = calBoardRuleHelper.needBoardApprove(contractBaseInfo.getProjReviewId());
        StartProcessReq startProcessReq = new StartProcessReq();
        // 判断使用创建流程还是修改流程
        startProcessReq.setModelKey(ProcessModelTypeEnum.PaymentCreateFlow.name());
        //增加运营部领导
        Long yyglbDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader("YYGLB", FlowAssistService.YYGLB_DESC, businesshead.name());
        Long yyglbDivisionLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader("YYGLB", FlowAssistService.YYGLB_DESC, leaderincharge.name());
        // 拟投放金额判断
        Long paymentAmount = getPaymentAmount(baseInfo.getApplyPaymentAmount(), contractBaseInfo.getProjCode());
        boolean hasPJ = paymentPlanedDetailService.list(paymentId).stream().anyMatch(detail -> PaymentMethod.PJ.name().equals(detail.getPaymentMethod()));

        startProcessReq.setVariables(MapUtil.of(
                Pair.of("contractProjectManager", Objects.isNull(contractBaseInfo.getProjSponsorUserId()) ? new ArrayList<>() : ListUtil.toList(String.valueOf(contractBaseInfo.getProjSponsorUserId()))),
                Pair.of("yyglbDeptLeader", Objects.isNull(yyglbDeptLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(yyglbDeptLeader))),
                Pair.of("yyglbDivisionLeader", Objects.isNull(yyglbDeptLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(yyglbDivisionLeader))),
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getConBizDeptLeaderId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getConBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getConBizDivisionLeaderId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getConBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of("riskControlManager", Objects.nonNull(baseInfo.getConRiskControlManagerId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getConRiskControlManagerId())) : new ArrayList<>()),
                Pair.of(ProcessVarEnum.projReviewNeedBoradApprove.name(), projReviewNeedBoardApproveFlag),
                // 若{资金拟投放金额=0}或{申请付款金额（元）＞资金拟投放金额（元）}或{付款申请本次支付明细列表中存在支付方式=票据的记录}
                Pair.of("needComplianceApprove", paymentAmount == 0L || baseInfo.getApplyPaymentAmount() > paymentAmount || hasPJ),
                // 总经理节点审批人需要计算 项目评审需要董事会审批 ? 无需审批人 : 财务总监
                Pair.of("generalManagerNodeApprover", projReviewNeedBoardApproveFlag ? new ArrayList<>() : sysUserService.queryJobUserIds(JobEnum.financialdirector.name()).stream().map(String::valueOf).collect(Collectors.toList()))
        ));
        startProcessReq.setProcessInstanceName(String.format("%s付款申请【%s】", contractBaseInfo.getProjName(), baseInfo.getPaymentCode()));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(paymentId));
        startProcessReq.setSubModule("PAYMENT");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getConBizDeptId())
                .map(String::valueOf).orElse(null));
        // 抄送给出纳
        List<UserDO> jobUsers = Optional.ofNullable(getBean(UserService.class).getUsersByjobcod(JobEnum.cashier.name())).orElse(new ArrayList<>());
        startProcessReq.setCcUserIdList(jobUsers.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList()));
        String processInstanceId = processApiService.start(startProcessReq);
        // 将流程ID放到该付款申请所有的公开信息记录里面
        getBean(PublicInfoQueryService.class).lambdaUpdate()
                .eq(PublicInfoQuery::getPaymentId, paymentId)
                .set(PublicInfoQuery::getProcessInstanceId, processInstanceId)
                .update();
        // 放款审核岗审批人需要剔除发起人
        List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.loanreviewpost.name());
        if (CollectionUtil.isNotEmpty(userIds) && userIds.size() > 1) {
            userIds.removeIf(item -> Objects.equals(AccountUtil.getLoginInfo().getId(), item));
        }
        List<String> assignUserIdList = userIds.stream().map(String::valueOf).collect(Collectors.toList());
        flowVariableApiService.setVariables(processInstanceId, MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_loanReviewPost"), assignUserIdList));
        // 辅助表落数据
        bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());
        projectLifecycleEventService.add("付款申请审批", ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", contractBaseInfo.getProjReviewId());
        // 提交版本不会修改记录的状态，因此参数置空
        recordPaymentStatus(paymentId, null, ProcessStatus.UNDER_APPROVAL);
    }

    public ProcessResp findRelatedProcess(Long paymentId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(paymentId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PAYMENT.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordPaymentStatus(Long paymentId, RecordStatus paymentStatus,
                                    ProcessStatus paymentProcessStatus) {
        if (paymentId == null || (paymentStatus == null && paymentProcessStatus == null)) {
            return;
        }
        LambdaUpdateWrapper<PaymentBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentBaseInfo::getId, paymentId);
        if (paymentStatus != null) {
            updateWrapper.set(PaymentBaseInfo::getPaymentStatus, paymentStatus.name());
        }
        if (paymentProcessStatus != null) {
            updateWrapper.set(PaymentBaseInfo::getPaymentProcessStatus, paymentProcessStatus.name());
        }
        updateWrapper.set(PaymentBaseInfo::getUpdateTime, LocalDateTime.now());
        // 付款申请流程通过时需要额外处理
        if (paymentStatus == RecordStatus.TAKE_EFFECT && paymentProcessStatus == ProcessStatus.APPROVAL_PASS) {
            updateWrapper.set(PaymentBaseInfo::getYunyingReviewState, YesOrNoNumberEnum.NO.getCode());
            updateWrapper.set(PaymentBaseInfo::getYunyingReviewDate, LocalDate.now());
        }
        paymentBaseInfoMapper.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void processEnd(Long paymentId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(paymentId);
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(baseInfo.getContractId());
        // 记录版本前要先更新状态
        if (processPass) {
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                // 如果是直租合同，直接固化付款申请中预关联的借据id
                paymentBaseInfoService.fixedContractReceiptInfo(baseInfo.getContractId());
//                // 默认生成已确认记录
//                long planPayAmount = baseInfo.getApplyPaymentAmount();
//                if (Objects.equals(baseInfo.getDownPaymentType(), YesOrNoNumberEnum.NO.getCode())) {
//                    planPayAmount -= Optional.ofNullable(baseInfo.getDownPayment()).orElse(0L);
//                }
//                if (Objects.equals(baseInfo.getRetentionMoneyType(), YesOrNoNumberEnum.NO.getCode())) {
//                    planPayAmount -= Optional.ofNullable(baseInfo.getRetentionMoney()).orElse(0L);
//                }
//                PaymentActualDetailUnconfirmed padu = new PaymentActualDetailUnconfirmed();
//                padu.setSeqCode("01");
//                padu.setClientId(baseInfo.getClientId());
//                padu.setContractId(baseInfo.getContractId());
//                padu.setInfoSource("系统确认");
//                padu.setPaymentId(baseInfo.getId());
//                padu.setPaymentMethod(PaymentMethod.WIRE_TRANSFER.getDisplay());
//                padu.setWriteOffStatus(WriteOffStatus.CONFIRM.name());
//                padu.setPaidInAmount(planPayAmount);
//                if (Objects.nonNull(baseInfo.getApplyPaymentDate())) {
//                    padu.setPaidInDate(baseInfo.getApplyPaymentDate().toLocalDate());
//                }
//                padu.setFlowId(LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATETIME_MS_PATTERN));
//                SpringUtil.getBean(PaymentActualDetailUnconfirmedService.class).save(padu);
//                List<PlanedDetailDto> planedDetailList = SpringUtil.getBean(PaymentPlanedDetailService.class).list(baseInfo.getId());
//                if (CollectionUtil.isNotEmpty(planedDetailList)) {
//                    List<PaymentActualDetailUnconfirmed> toInsertList = planedDetailList.stream().map(e -> {
//                        PaymentActualDetailUnconfirmed padu = new PaymentActualDetailUnconfirmed();
//                        padu.setClientId(baseInfo.getClientId());
//                        padu.setContractId(baseInfo.getContractId());
//                        padu.setInfoSource("系统确认");
//                        padu.setPaymentId(baseInfo.getId());
//                        PaymentMethod paymentMethod = PaymentMethod.findByName(e.getPaymentMethod());
//                        if (Objects.nonNull(paymentMethod)) {
//                            padu.setPaymentMethod(paymentMethod.getDisplay());
//                        }
//                        padu.setWriteOffStatus(WriteOffStatus.CONFIRM.name());
//                        padu.setPaidInAmount(e.getPaymentAmount());
//                        if (Objects.nonNull(baseInfo.getApplyPaymentDate())) {
//                            padu.setPaidInDate(baseInfo.getApplyPaymentDate().toLocalDate());
//                        }
//                        padu.setOppositeAccountName(e.getOppositeAccountName());
//                        padu.setOppositeAccountNumber(e.getOppositeAccount());
//                        padu.setOppositeAccountBank(e.getOppositeAccountBank());
//                        padu.setFlowId(LocalDateTimeUtil.format(LocalDate.now(), DatePattern.PURE_DATETIME_MS_PATTERN));
//                        return padu;
//                    }).collect(Collectors.toList());
//                    SpringUtil.getBean(PaymentActualDetailUnconfirmedService.class).saveBatch(toInsertList);
//                }
            }
            recordPaymentStatus(paymentId, RecordStatus.TAKE_EFFECT, ProcessStatus.APPROVAL_PASS);
        } else {
            recordPaymentStatus(paymentId, null, ProcessStatus.APPROVAL_REJECT);
        }
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        paymentVersionService.recordVersion(paymentId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);

        //额外的同步业务
        if (processPass) {
            //同步保单
            paymentPolicyInfoService.sync2Policy(paymentId, processInstanceId);
//            // 应收流水- 回租 不再由付款申请通过后触发,改为投放确认流程中财务填写的收款信息
//            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
//                //苍穹交互 付款，保证金，手续费，首期租金
//                notifyAdd(baseInfo);
//                //通知苍穹创建付款申请
//                financialManagerServiceImpl2.paymentExec(SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(baseInfo.getContractId()), Collections.singletonList(changeFinancialPaymentVO(baseInfo)));
//            }
        } else {
            paymentBaseInfoService.cancelOccupy(baseInfo.getContractId(), paymentId);
            // 处理公开信息，审批未通过需要将公开信息的【流程ID】置空
            getBean(PublicInfoQueryService.class).lambdaUpdate()
                    .eq(PublicInfoQuery::getPaymentId, paymentId)
                    .set(PublicInfoQuery::getProcessInstanceId, null)
                    .update();
        }
    }

    /**
     * 查询借据下风险敞口
     *
     * @param receiptIds 借据id
     * @return Map<Long, Long> 借据id,敞口最小为0
     * 计算规则 = 借据下实际付款-借据下收款本金核销值-借据下首期租金核销值-合同下保证金余额
     **/
    public Map<Long, Long> getStockRiskExposureByReceiptIds(List<Long> receiptIds) {
        if (CollectionUtil.isEmpty(receiptIds)) {
            return MapUtil.empty();
        }
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.listByReceiptIds(receiptIds);
        //借据，付款信息
        Map<Long, List<PaymentBaseInfo>> receiptPaymentMap = paymentBaseInfos.stream().filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal())).collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptIdFinal));
        //借据对应合同信息
        Map<Long, Long> receiptId2ContractId = paymentBaseInfos.stream().filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal())).collect(Collectors.toMap(PaymentBaseInfo::getReceiptIdFinal, PaymentBaseInfo::getContractId, (a, b) -> a));
        Set<Long> paymentIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        //每个付款对应实际付款
        Map<Long, Long> paymentIdAmountMap = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(paymentIds), PaymentActualDetail::getPaymentId, paymentIds)
                .in(PaymentActualDetail::getWriteOffStatus, ListUtil.toList(PaymentWriteOffStatus.WRITTEN_OFF.name(),
                        PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))).stream().filter(base -> LongUtil.null2zero(base.getPaidInAmount()) != 0).collect(Collectors.toMap(PaymentActualDetail::getPaymentId,
                PaymentActualDetail::getPaidInAmount, (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //查询借据租金收款信息
        Map<Long, Long> receiptCollectionId = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                        CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()))).stream().filter(base -> ObjectUtil.isNotEmpty(base.getReceiptId())).collect(Collectors.toMap(CollectionBaseInfo::getReceiptId, base -> LongUtil.null2zero(base.getCollectionPrincipal()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //查询首期租金收款信息
        Map<Long, Long> paymentIdFistRentMap = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.FIRST_RENT.name())
                .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                        CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()))).stream().filter(base -> ObjectUtil.isNotEmpty(base.getReceiptId())).collect(Collectors.toMap(CollectionBaseInfo::getReceiptId, base -> LongUtil.null2zero(base.getCollectionAmount()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //查询<借据id, 保证金余额>
        Map<Long, Long> receiptIdMarginMap = marginBaseInfoService.getAmountByReceiptIds(receiptId2ContractId.keySet(), LocalDate.now());
        //借据对应剩余本金大于0
        Map<Long, Long> receiptExposureMap = new HashMap<>();
        long sumAmount;
        List<PaymentBaseInfo> paymentList;
        for (Long receiptId : receiptPaymentMap.keySet()) {
            sumAmount = 0;
            //实际付款金额
            paymentList = receiptPaymentMap.get(receiptId);
            if (ObjectUtil.isNotEmpty(paymentList)) {
                sumAmount += paymentList.stream().map(PaymentBaseInfo::getId).map(paymentIdAmountMap::get).mapToLong(LongUtil::null2zero).sum();
            }
            //借据下本金收款金额
            sumAmount -= LongUtil.null2zero(receiptCollectionId.get(receiptId));
            //借据下减去首期租金
            sumAmount -= paymentIdFistRentMap.getOrDefault(receiptId, 0L);
            //减去合同剩余保证金
            sumAmount -= receiptIdMarginMap.getOrDefault(receiptId, 0L);
            receiptExposureMap.put(receiptId, Math.max(sumAmount, 0));
        }
        return receiptExposureMap;
    }

    private FinancialPaymentVO changeFinancialPaymentVO(PaymentBaseInfo paymentBaseInfo) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
        FinancialPaymentVO financialPayment = new FinancialPaymentVO();
        financialPayment.setPaymentCode(paymentBaseInfo.getPaymentCode());
        financialPayment.setContractCode(paymentBaseInfo.getContractCode());
        financialPayment.setPaymentType(FinancialPaymentCodeENUM.changePaymentCode(contractBaseInfo.getBizType()).getDisplay());
        financialPayment.setApplyPaymentAmount(LongUtil.null2zero(paymentBaseInfo.getApplyPaymentAmount()));
        financialPayment.setApplyPaymentDate(paymentBaseInfo.getApplyPaymentDate());
        financialPayment.setIsInitialRent(paymentBaseInfo.getDownPaymentType());
        financialPayment.setInitialRentAmount(paymentBaseInfo.getDownPayment());
        if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getDownPaymentType())) {
            //不含首期租金，需要减去首期租金
            financialPayment.setApplyPaymentAmount(financialPayment.getApplyPaymentAmount() - LongUtil.null2zero(paymentBaseInfo.getDownPayment()));
        }
        if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getRetentionMoneyType())) {
            //有内扣质保金
            financialPayment.setRetentionMoney(paymentBaseInfo.getRetentionMoney());
            financialPayment.setApplyPaymentAmount(financialPayment.getApplyPaymentAmount() - LongUtil.null2zero(paymentBaseInfo.getRetentionMoney()));
        }
        return financialPayment;
    }

    private void notifyAdd(PaymentBaseInfo baseInfo) {
        // 发送事件
        if (Objects.nonNull(baseInfo.getEarnestMoney())) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.EARNEST_MONEY, baseInfo.getEarnestMoney(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getConsultingFee()) && baseInfo.getConsultingFee() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.OTHERAMOUNT, baseInfo.getConsultingFee(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getDownPayment()) && baseInfo.getDownPayment() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.FIRST_RENT, baseInfo.getDownPayment(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getRetentionMoney()) && baseInfo.getRetentionMoney() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.RETENTION_MONEY,
                    baseInfo.getRetentionMoney(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getCommission()) && baseInfo.getCommission() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.COMMISSION,
                    baseInfo.getCommission(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getFirstInstallmentInterest()) && baseInfo.getFirstInstallmentInterest() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST,
                    baseInfo.getFirstInstallmentInterest(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
//        if (Objects.nonNull(baseInfo.getNominalPrice()) && baseInfo.getNominalPrice() > 0) {
//            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.NOMINAL_PRICE, baseInfo.getNominalPrice(),baseInfo.getApplyPaymentDate().toLocalDate());
//            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
//        }
    }

    public boolean canSave(Long paymentId) {
        return true;
    }

    public void effectCheck(Long paymentId) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(paymentId);
        effectCheck(baseInfo);
    }

    public Map<Long, List<PaymentBaseInfo>> getPaymentByContract(List<Long> contractIds) {
        return paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(CollectionUtil.isNotEmpty(contractIds), PaymentBaseInfo::getContractId, contractIds)
                .in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()))
                .in(PaymentBaseInfo::getWriteOffStatus, ListUtil.toList(PaymentWriteOffStatus.WRITTEN_OFF.name(), PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))).stream().collect(Collectors.groupingBy(PaymentBaseInfo::getContractId));
    }

    public void effectCheck(PaymentBaseInfo baseInfo) {
        Util.missRequiredParam(ObjectUtil.isEmpty(baseInfo.getApplyPaymentAmount()), "申请支付金额");
        Util.missRequiredParam(ObjectUtil.isEmpty(baseInfo.getApplyPaymentDate()), "申请支付日期");
        Util.missRequiredParam(ObjectUtil.isEmpty(baseInfo.getDownPayment()), "首期租金");
        Util.missRequiredParam(ObjectUtil.isEmpty(baseInfo.getEarnestMoney()), "保证金");
        Util.missRequiredParam(ObjectUtil.isEmpty(baseInfo.getNominalPrice()), "名义价款");
        Util.missRequiredParam(ObjectUtil.isEmpty(baseInfo.getConsultingFee()), "手续费/服务费/咨询费");

        Pair<Long, Long> capitalDistribute = paymentBaseInfoService.calculateCapitalDistribution(baseInfo.getContractId(), baseInfo.getId());
        if (baseInfo.getApplyPaymentAmount() + capitalDistribute.getKey() > baseInfo.getConApplyCreditAmount()) {
            throw new MithrasException("申请金额已超出所剩余额");
        }
        //检查保单文件信息
        paymentPolicyInfoService.checkFile(baseInfo.getId());
        List<PaymentPlanedDetail> planedDetails = planedDetailService.getBaseMapper().selectList(Wrappers.<PaymentPlanedDetail>lambdaQuery()
                .eq(PaymentPlanedDetail::getPaymentId, baseInfo.getId()));
        Long amount = 0L;
        for (PaymentPlanedDetail planedDetail : planedDetails) {
            if (null != planedDetail.getPaymentAmount()) {
                amount += planedDetail.getPaymentAmount();
            }
        }
        if (!amount.equals(baseInfo.getApplyPaymentAmount())) {
            throw new MithrasException("计划付款明细金额累计与申请金额不等");
        }
//            List<PaymentQuestionnaireAnswer> answers = questionnaireAnswerService.getBaseMapper()
//                    .selectList(Wrappers.<PaymentQuestionnaireAnswer>lambdaQuery()
//                    .eq(PaymentQuestionnaireAnswer::getPaymentId, baseInfo.getId()));
//            for (PaymentQuestionnaireAnswer answer : answers) {
//                if(null == answer.getQuestionAnswer()){
//                    throw new MithrasException("请完善问卷调查后再提交审批");
//                }
//            }
        // 合同签约照片/视频必传
        List<MaterialsList> materials = materialsListService.getList(baseInfo.getId(), BusinessModuleEnum.PAYMENT.name(), LendingMaterialType.SIGN_PHOTO_VIDEO.name(), null);
        if (CollectionUtil.isEmpty(materials)) {
            // 找一下对应合同下是否存在文件
            materials = materialsListService.getList(baseInfo.getContractId(), BusinessModuleEnum.CONTRACT.name(), LendingMaterialType.SIGN_PHOTO_VIDEO.name(), null);
        }
        Assert.notEmpty(materials, () -> MithrasException.newException(LendingMaterialType.SIGN_PHOTO_VIDEO.getDisplay() + "不能为空"));
    }

    public boolean isProjReviewPassBeyondSpecificMonth(Long paymentId, int monthCount) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请信息不存在");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        // 查询最近一次有效的版本数据（实际是需要取最近一次通过的流程时间，通过版本表获取可以无需关注是变更流程还是创建流程）
        ProjReviewBaseInfoLib projReviewBaseInfoLib = projReviewBaseInfoLibService.getEffectLatestOne(contractBaseInfo.getProjReviewId());
        if (Objects.isNull(projReviewBaseInfoLib)) {
            return false;
        }
        LocalDateTime targetDateTime = LocalDateTimeUtil.offset(projReviewBaseInfoLib.getCreateTime(), monthCount, ChronoUnit.MONTHS);
        return targetDateTime.isBefore(LocalDateTime.now());
    }

    @SneakyThrows
    public void loanReviewFile(PaymentBaseInfo paymentBaseInfo) {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = paymentLoanReviewFileRender.render(os, paymentBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            materialsListService.add(is, fileName, paymentBaseInfo.getId(), PaymentTypeEnum.LOAN_REVIEW.name(), null, BusinessModuleEnum.PAYMENT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成放款审核表发生未知异常[paymentCode:{}]", paymentBaseInfo.getPaymentCode(), e);
            throw new MithrasException("生成放款审核表发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    public void checkContractSign(Long paymentId) {
        PaymentBaseInfo baseInfo = paymentBaseInfoService.getById(paymentId);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(baseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            return;
        }
        if (contractBaseInfo.getIsSigned().equals(0)) {
            throw new MithrasException("请确认是否允许移动端上传面签照片、视频");
        }
    }

    public PaymentCheckApplyAmountRsp checkApplyAmount(PaymentCheckApplyAmountReq req) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(req.getPaymentId());
        if (Objects.isNull(paymentBaseInfo)) {
            throw MithrasException.newException("付款申请信息不存在");
        }
        // 校验一下当前付款申请是不是填了金额
        if (Objects.isNull(paymentBaseInfo.getApplyPaymentAmount())) {
            throw MithrasException.newException("申请付款金额不能为空");
        }
        PaymentCheckApplyAmountRsp rsp = new PaymentCheckApplyAmountRsp();
        // 构建合同金额
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw MithrasException.newException("合同信息不存在");
        }
        rsp.setContractAmount(contractBaseInfo.getApplyCreditAmount());

        // 校验拟投放金额
        Long paymentAmount = getPaymentAmount(paymentBaseInfo.getApplyPaymentAmount(), contractBaseInfo.getProjCode());
        rsp.setNeedConfirmTips(paymentAmount == 0L || !paymentAmount.equals(paymentBaseInfo.getApplyPaymentAmount()));
        rsp.setTipMessage(rsp.getNeedConfirmTips()? paymentAmount == 0L ? "暂未纳入资金计划，请联系资金经理确认！":"申请付款金额与资金拟投放金额不一致，请确认是否继续付款！":"");

        if("userTask_riskDeptMaster".equals(req.getActivityId())){
            rsp.setNeedConfirmTips(paymentAmount == 0L || paymentAmount < paymentBaseInfo.getApplyPaymentAmount());
            rsp.setTipMessage(rsp.getNeedConfirmTips()? paymentAmount == 0L ? "暂未纳入资金计划，请确认是否继续提交？":"申请付款金额超出月度资金拟投放金额，请确认是否继续提交？":"");
            return rsp;
        }

        // 找到项目下的所有审批通过的付款申请
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getContractId, contractBaseInfo.getId())
                .in(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.FINISHED.name(), PaymentStatusEnum.TAKE_EFFECT.name())
                .in(PaymentBaseInfo::getPaymentProcessStatus, ProcessStatus.APPROVAL_PASS.name(), ProcessStatus.UNDER_APPROVAL.name())
        );
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            Long amount = paymentBaseInfoList.stream().map(PaymentBaseInfo::getApplyPaymentAmount).reduce(0L, Long::sum);
            rsp.setIsOverContractAmount((paymentBaseInfo.getApplyPaymentAmount() + amount) > contractBaseInfo.getApplyCreditAmount());
        } else {
            rsp.setIsOverContractAmount(paymentBaseInfo.getApplyPaymentAmount() > contractBaseInfo.getApplyCreditAmount());
        }
        // 找到项目批复金额
        ProjReviewPriceDetailRSP detail = projReviewPriceService.detail(contractBaseInfo.getProjReviewId());
        if (Objects.nonNull(detail.getLeasePriceDetailRSP())) {
            rsp.setApprovedAmount(detail.getLeasePriceDetailRSP().getApprovedAmount());
        } else if (Objects.nonNull(detail.getAocPriceDetailRSP())) {
            rsp.setApprovedAmount(detail.getAocPriceDetailRSP().getApprovedAmount());
        } else if (Objects.nonNull(detail.getFactoringPriceDetailRSP())) {
            rsp.setApprovedAmount(detail.getFactoringPriceDetailRSP().getApprovedAmount());
        }

        // 找到所有已经审批中和审批通过的付款申请
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(contractBaseInfo.getProjReviewId()));
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            // 没有合同，说明是新合同，直接返回比较结果
            rsp.setIsOverApprovedAmount(paymentBaseInfo.getApplyPaymentAmount() > rsp.getApprovedAmount());
            return rsp;
        }
        List<ContractBaseInfo> baseInfoList = contractBaseInfoList.stream()
                .filter(e -> !CharSequenceUtil.equalsAny(e.getContractStatus(),
                        ContractStatus.NEW.name(), ContractStatus.CLOSED.name(), ContractStatus.INVALID.name()))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(baseInfoList)) {
            rsp.setIsOverApprovedAmount(paymentBaseInfo.getApplyPaymentAmount() > rsp.getApprovedAmount());
            return rsp;
        }
        // 找到生效的付款
        List<PaymentBaseInfo> projPaymentBaseInfoList = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getContractId, baseInfoList.stream().map(ContractBaseInfo::getId).distinct().collect(Collectors.toList()))
                .in(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.FINISHED.name(), PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.NEW.name())
                .ne(PaymentBaseInfo::getId, paymentBaseInfo.getId())
                .in(PaymentBaseInfo::getPaymentProcessStatus, ProcessStatus.APPROVAL_PASS.name(), ProcessStatus.UNDER_APPROVAL.name())
        );
        if (CollectionUtil.isEmpty(projPaymentBaseInfoList)) {
            rsp.setIsOverApprovedAmount(paymentBaseInfo.getApplyPaymentAmount() > rsp.getApprovedAmount());
            return rsp;
        }
        Long alreadyApply = projPaymentBaseInfoList.stream().map(PaymentBaseInfo::getApplyPaymentAmount).reduce(0L, Long::sum);
        rsp.setIsOverApprovedAmount((paymentBaseInfo.getApplyPaymentAmount() + alreadyApply) > rsp.getApprovedAmount());
        return rsp;
    }


    public Long getPaymentAmount(Long applyPaymentAmount,String projCode) {
        // 补充拟投放金额信息
        if(Objects.nonNull(applyPaymentAmount) && applyPaymentAmount > 0) {
            // 获取该项目最近一次审批通过的月度计划
            BudgetPlanPayDetail budgetPlanPayDetail = budgetPlanPayDetailService.getLastMonthBudgetPayDetailByProjectCode(projCode);
            if(Objects.nonNull(budgetPlanPayDetail)) {
                BigDecimal paymentAmount = new BigDecimal(applyPaymentAmount)
                        .divide(new BigDecimal(budgetPlanPayDetail.getPlanPayAmount()),99, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(budgetPlanPayDetail.getFundPlanPayAmount())).setScale(0, RoundingMode.HALF_UP);
                return paymentAmount.longValue();
            }
        }
        return 0L;
    }

    public void saveRegister(Long paymentId) {

    }

    public String getOrgCode(String orgType) {
        if (StringUtils.isBlank(orgType)) {
            return null;
        }
        String code = null;
        if (orgType.equalsIgnoreCase("1")) {
            code = "02";
        } else if (orgType.equalsIgnoreCase("A")) {
            code = "06";
        } else if (orgType.equalsIgnoreCase("9")) {
            code = "09";
        } else if (orgType.equalsIgnoreCase("3")) {
            code = "03";
        } else if (orgType.equalsIgnoreCase("5")) {
            code = "03";
        } else if (orgType.equalsIgnoreCase("7")) {
            code = "09";
        }
        return code;
    }

    public String getSize(String orgScale) {
        if (OrgScaleType.TINY.name().equalsIgnoreCase(orgScale)) {
            return "40";
        } else if (OrgScaleType.SMALL.name().equalsIgnoreCase(orgScale)) {
            return "30";
        } else if (OrgScaleType.MIDDLE.name().equalsIgnoreCase(orgScale)) {
            return "20";
        } else if (OrgScaleType.BIG.name().equalsIgnoreCase(orgScale)) {
            return "10";
        }
        return null;
    }


    public String getLeaseBusinessType(String leaseTypeCode) {
        if (StringUtils.isBlank(leaseTypeCode)) {
            return null;
        }
        if ("hui_zu".equalsIgnoreCase(leaseTypeCode)) {
            return "02";
        } else if ("zhi_zu".equalsIgnoreCase(leaseTypeCode)) {
            return "01";
        }
        return null;
    }

    public String getCity(CorpAddressInfoListRSP corpAddressInfoListRSP) {
        if ("市辖区".equalsIgnoreCase(corpAddressInfoListRSP.getCityName())) {
            return corpAddressInfoListRSP.getProvinceName();
        }
        return corpAddressInfoListRSP.getCityName();
    }

    /*public String getProvince(CorpAddressInfoListRSP corpAddressInfoListRSP) {
        if ("台湾省".equalsIgnoreCase(corpAddressInfoListRSP.getProvinceName())
                || "中国香港".equalsIgnoreCase(corpAddressInfoListRSP.getProvinceName())
                || "中国澳门".equalsIgnoreCase(corpAddressInfoListRSP.getProvinceName())) {
            return corpAddressInfoListRSP.getCityName();
        }
        return corpAddressInfoListRSP.getProvinceName();
    }*/

    public String callRemote(RegisterSaveReq registerSaveReq, List<FileListRSP> leaseItemFileList) throws IOException {
        if (leaseItemFileList == null || leaseItemFileList.isEmpty()) {
            throw new AuthCheckException("请在付款材料模块上传租赁物清单文件后，再进行中登网登记操作！");
        }
        OkHttpClient client = new OkHttpClient();
        String data = JSONObject.toJSONString(registerSaveReq);
        log.info("中登登记请求参数：{}", data);
        // 创建请求体
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (FileListRSP fileListRSP : leaseItemFileList) {
            MultipartFile multipartFile = fileUrlConvertToMultipartFile(fileListRSP);
            if (multipartFile != null) {
                multipartFiles.add(multipartFile);
            }
        }
        List<File> originFiles = convertMultiFile(multipartFiles);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jsonData", data);
        if (!originFiles.isEmpty()) {
            // 遍历文件列表并添加文件部分
            for (File file : originFiles) {
                builder.addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse(Files.probeContentType(file.toPath())), file));
            }

        }
        MultipartBody requestBody = builder
                .build();
        // 创建请求
        Request request = new Request.Builder()
                .url(saveUrl)
                .post(requestBody)
                .build();

        // 发送请求并获取响应
        String responseStr = null;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            responseStr = response.body().string();
        }
        return responseStr;
    }


    private static List<File> convertMultiFile(List<MultipartFile> multipartFiles) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }
        List<File> files = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
            files.add(file);
        }
        return files;
    }


    public MultipartFile fileUrlConvertToMultipartFile(FileListRSP fileListRSP) {

        try {
            String imageUrl = materialsListService.getPreviewUrl(fileListRSP.getOssFilename(), expiry);
            // 将在线图片地址转换为URL对象
            URL url = new URL(imageUrl);
            // 打开URL连接
            URLConnection connection = url.openConnection();
            // 转换为HttpURLConnection对象
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            // 获取输入流
            InputStream inputStream = httpURLConnection.getInputStream();
            // 读取输入流中的数据，并保存到字节数组中
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            // 将字节数组转换为字节数组
            byte[] bytes = byteArrayOutputStream.toByteArray();
            // 创建ByteArrayInputStream对象，将字节数组传递给它
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            // 创建MultipartFile对象，将ByteArrayInputStream对象作为构造函数的参数
            String originalFilename = fileListRSP.getFilename();
            String suffix = fileListRSP.getSuffix();
            String contentType = null;
            if ("pdf".equalsIgnoreCase(suffix)) {
                contentType = "application/pdf";
            } else if ("jpg".equalsIgnoreCase(suffix)) {
                contentType = "image/jpg";
            }
            MultipartFile multipartFile = new MockMultipartFile("file", originalFilename, contentType, byteArrayInputStream);
            return multipartFile;
        }catch (IOException ex){
            ex.printStackTrace();
            throw new MithrasException("附件无效");
        }
    }
}
