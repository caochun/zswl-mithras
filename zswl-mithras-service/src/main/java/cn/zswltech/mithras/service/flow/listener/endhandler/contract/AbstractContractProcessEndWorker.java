package cn.zswltech.mithras.service.flow.listener.endhandler.contract;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.text.ContractTextStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.text.SigningWayEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.contract.mapper.contract.ContractTextManageMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextManage;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextSignInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Listener.ContractPriceChangeEvent;
import cn.zswltech.mithras.service.service.Listener.collection.CollectionAddEvent;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.client.ProjClientRoleService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.service.contract.ContractSignInfoService;
import cn.zswltech.mithras.service.service.contract.text.ContractTextManageService;
import cn.zswltech.mithras.service.service.contract.text.ContractTextSignInfoService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractVersionService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.stampduty.ReportStampDutyService;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 合同 流程结束处理器
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:19 AM
 */
@Slf4j
public abstract class AbstractContractProcessEndWorker {

    @Resource
    protected ContractIncomeSharingService contractIncomeSharingService;
    @Resource
    protected ContractVersionService contractVersionService;
    @Resource
    protected ContractService contractService;
    @Resource
    protected ContractBaseInfoService contractBaseInfoService;
    @Resource
    protected PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    protected CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    protected FlowVariableApiService flowVariableApiService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ContractTextManageService contractTextManageService;
    @Resource
    private ContractSignInfoService contractSignInfoService;

    @Transactional(rollbackFor = Exception.class)
    public void processEnd(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        // 更新状态
        recordStatus(modelKey, contractId, processPass);
        // 更新客户风控行业分类
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(contractBaseInfo.getClientId());
        contractBaseInfo.setRiskControlIndustryClassify(Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null));
        contractBaseInfoService.updateById(contractBaseInfo);
        if (processPass) {
            // 审批通过 新增版本 要在业务处理之后新增
            customProcessPass(modelKey, contractId, endType, startUserId, processInstanceId);
            contractVersionService.recordVersion(contractId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
            this.notifyCollection(modelKey, contractId);
            if (CharSequenceUtil.equalsAny(modelKey, ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name(),
                    ProcessModelTypeEnum.ContractExtensionFlow.name(), ProcessModelTypeEnum.ContractLPRChangeFlow.name(),
                    ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name())) {
                // 删掉待签约的关联文件
                ContractTextManage contractTextManage = SpringUtil.getBean(ContractTextManageMapper.class).selectByContractId(contractId);
                if (contractTextManage != null) {
                    ContractTextManageService contractTextManageService = SpringUtil.getBean(ContractTextManageService.class);
                    contractTextManageService.removeById(contractTextManage.getId());
                    SpringUtil.getBean(ContractTextSignInfoService.class)
                            .remove(Wrappers.<ContractTextSignInfo>lambdaQuery()
                                    .eq(ContractTextSignInfo::getMainId, contractTextManage.getId()));
                    // 重新生成待签约文件
                    // 这里需要将合同信息抄送到合同文本管理模块
                    ContractTextManage newContractTextManage = new ContractTextManage();
                    newContractTextManage.setContractId(contractId);
                    newContractTextManage.setPushTime(LocalDateTime.now());
                    contractTextManageService.save(newContractTextManage);
                    // 将需要签约的合同文件转化为pdf重新上传 使用异步，比较耗时
                    CompletableFuture.runAsync(() -> {
                        asyncInvoke(contractId, contractTextManageService, newContractTextManage);
                    });
                } else {
                    // 这里需要将合同信息抄送到合同文本管理模块
                    ContractTextManage textManage = new ContractTextManage();
                    textManage.setContractId(contractId);
                    textManage.setPushTime(LocalDateTime.now());
                    contractTextManageService.save(textManage);
                    // 将需要签约的合同文件转化为pdf重新上传 使用异步，比较耗时
                    CompletableFuture.runAsync(() -> {
                        asyncInvoke(contractId, contractTextManageService, textManage);
                    });
                }
            }
        } else {
            // 发起人取消 回退版本 并记录一个失效版本 要在业务处理之前处理
            contractVersionService.recordVersion(contractId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
            contractVersionService.reset(contractId);
            // 回退后要再更新一次状态
            recordStatus(modelKey, contractId, processPass);
            // 数据被回退，如果有本次审批中关联的付款需要被释放（非直租）
            if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                paymentBaseInfoService.cancelJoinReceiptByContractId(contractId);
            }
            customProcessFail(modelKey, contractId, endType, startUserId, processInstanceId);

            if (ProcessModelTypeEnum.ContractCreateFlow.name().equals(modelKey)) {
                projClientRoleService.projContractFinish(Collections.singletonList(contractId));
            } else if (ProcessModelTypeEnum.ContractModifyFlow.name().equals(modelKey)) {
                projClientRoleService.projContractRollBack(contractId);
            }
        }

        // 根据审批结果执行业务数据变更并发送通知事件
        // 合同状态变更，通知风险敞口
        ApplicationContextUtil.getApplicationContext().publishEvent(new ContractPriceChangeEvent(this, contractId));
        afterAllHook(modelKey, contractId, endType, startUserId, processInstanceId);
    }

    private void asyncInvoke(Long contractId, ContractTextManageService contractTextManageService, ContractTextManage textManage) {
        log.info("合同文本管理模块开始转换文件开始........");
        // 将所有的签约状态改回未签约
        contractSignInfoService.lambdaUpdate()
                .set(ContractSignInfo::getSignStatus, ContractTextStatusEnum.NO_SIGNED.name())
                .set(ContractSignInfo::getSignFinishTime, null)
                .eq(ContractSignInfo::getContractId, contractId)
                .update();
        contractTextManageService.transformAllFile(contractId, textManage.getId());
        log.info("合同文本管理模块开始转换文件结束........");
    }

    @Transactional(rollbackFor = Exception.class)

    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {

    }

    @Transactional(rollbackFor = Exception.class)
    public void customProcessFail(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {

    }

    /**
     * 通知收款
     */
    @Transactional(rollbackFor = Exception.class)
    public void notifyCollection(String modelKey, Long contractId) {
        if (!ProcessModelTypeEnum.ContractCreateFlow.name().equals(modelKey)) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(modelKey, contractId, CashFlowItemEnum.RENT);
            collectionAddEvent.setProcessModelTypeEnum(ProcessModelTypeEnum.getByName(modelKey));
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
    }

    /**
     * @param modelKey
     * @param contractId
     * @param endType
     * @param startUserId
     * @param processInstanceId
     */
    @Transactional(rollbackFor = Exception.class)
    public void afterAllHook(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {

    }

    @Transactional(rollbackFor = Exception.class)
    public void recordStatus(String modelKey, Long contractId, boolean processPass) {
        contractService.recordContractStatus(contractId, getContractStatus(modelKey, processPass), getContractProcessStatus(modelKey, processPass));
    }

    /**
     * 可处理的流程类型
     *
     * @return
     */
    public abstract List<ProcessModelTypeEnum> handleModelTypeList();

    /**
     * 状态更新
     *
     * @param modelKey
     * @param processPass
     * @return
     */
    public abstract ContractStatus getContractStatus(String modelKey, boolean processPass);

    public abstract ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass);

    protected void paymentFinish(String processInstanceId, Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getContractId, contractId);
        query.isNotNull(PaymentBaseInfo::getReceiptId);
        query.isNotNull(PaymentBaseInfo::getReceiptIdFinal);
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.list(query);

        PaymentBaseInfo relatePaymentInfo = new PaymentBaseInfo();
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            relatePaymentInfo = paymentBaseInfoList.get(0);
        }
        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            // 非直租合同，变更付款申请状态为"已投放"
            Map<String, Object> varMap = flowVariableApiService.getVariables(processInstanceId, Collections.singletonList(PaymentBaseInfoService.CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY));
            Object paymentId = varMap.get(PaymentBaseInfoService.CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY);
            if (Objects.isNull(paymentId)) {
                // 直接通过合同管理操作起租的，取当前合同下最近生效但未已投放的付款申请
                PaymentBaseInfo target = this.findLatestEffect(contractId);
                if (Objects.isNull(target)) {
                    log.error("操作失败，无法确定已投放的是哪个付款申请[processInstanceId:{}]", processInstanceId);
                    throw new MithrasException("操作失败，无法确定已投放的是哪个付款申请");
                } else {
                    paymentId = target.getId();
                }
            }
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(Long.parseLong(paymentId.toString()));
            if (Objects.isNull(paymentBaseInfo)) {
                log.error("操作失败，没有找到对应的付款信息[processInstanceId:{}, paymentId:{}]", processInstanceId, paymentId);
                throw new MithrasException("操作失败，没有找到对应的付款信息");
            }
            paymentBaseInfo.setPaymentStatus(PaymentStatusEnum.FINISHED.name());
            paymentBaseInfoService.updateById(paymentBaseInfo);
            relatePaymentInfo = paymentBaseInfo;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(ReportStampDutyService.TARGET_RECEIPT_ID,relatePaymentInfo.getReceiptIdFinal());
        flowVariableApiService.setVariables(processInstanceId,map);
    }

    protected void doIncomeSharing(String processModelKey, Long contractId) {
        // 异步执行收入确认分摊逻辑
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ThreadPoolUtil.getCommonPool().execute(() -> {
                    try {
                        contractIncomeSharingService.calculateIncomeSharing(contractId);
                    } catch (Exception e) {
                        log.error("异步生成收入确认分摊明细发生异常[contractId:{}, processModelType:{}]", contractId, processModelKey, e);
                    }
                });
            }
        });
    }

    /**
     * 启动合同后租检查
     * @param contractId 合同id
     */
    protected void startAfterLeaseCheck(Long contractId) {
        try {
            Long clientId = contractBaseInfoService.getById(contractId).getClientId();
            //找到应收款项本金总和，如果为空，则未起租或者之前结清过，认为敞口为0，否则敞口不为0
            long openMouth = 0;
            List<CollectionBaseInfo> collectionBaseInfos = SpringContextHolder.getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getClientId, clientId)
                    .ne(CollectionBaseInfo::getContractId, contractId)
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()));
            if (CollUtil.isNotEmpty(collectionBaseInfos)) {
                // 找到实际核销的收款本金总和
                long planPrincipalSum = collectionBaseInfos.stream().filter(obj -> Objects.nonNull(obj.getPrincipal()))
                        .mapToLong(CollectionBaseInfo::getPrincipal).sum();
                long actualPrincipalSum = collectionBaseInfos.stream().filter(obj -> Objects.nonNull(obj.getCollectionPrincipal()))
                        .mapToLong(CollectionBaseInfo::getCollectionPrincipal).sum();
                openMouth = planPrincipalSum - actualPrincipalSum;
            }
            // 客户首次投放，或者敞口从0变成非0需要加入租后检查计划中
            if (openMouth == 0) {
                SpringContextHolder.getBean(AfterLeaseCheckPlanBaseService.class).clientFirstCheck(clientId);
            }
        } catch (Exception e) {
            log.error("客户首次投放或者敞口从0变成非0需要加入租后检查计划失败 【合同编号：{}】",contractId, e);
        }
    }

    private PaymentBaseInfo findLatestEffect(Long contractId) {
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listEffectPaymentByContractId(contractId);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return null;
        }
        paymentBaseInfoList.removeIf(e -> Objects.equals(e.getPaymentStatus(), PaymentStatusEnum.FINISHED.name()));
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return null;
        }
        paymentBaseInfoList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
        return paymentBaseInfoList.get(0);
    }
}
