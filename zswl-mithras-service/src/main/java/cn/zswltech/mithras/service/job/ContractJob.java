package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRemindRecord;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.Listener.timeout.TimeoutNotifyEventListener;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.contract.core.application.ContractRemindRecordService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author dingqi
 * @date 2022/9/14
 * @description 合同相关定时任务
 */
@Slf4j
@Component
public class ContractJob {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractRemindRecordService contractRemindRecordService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private TimeoutNotifyEventListener timeoutNotifyEventListener;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;

    @XxlJob("tryAutoStartRentJob")
    public void tryAutoStartRentJob() {
        // 寻找付款申请标记为结束投放的
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT.name());
        query.eq(PaymentBaseInfo::getIsFinishPutFinal, Boolean.TRUE);
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.list(query);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            try {
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
                // 直租合同不处理
                if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                    continue;
                }
                //合同状态= 生效 && 合同流程状态= 新建审批通过、取消新增借据(投放)、新增借据(投放)审批通过、取消变更、变更审批通过、取消起租
                if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.TAKE_EFFECT.name()) && ContractProcessStatusEnum.canStartRentStatus().contains(contractBaseInfo.getContractProcessStatus())) {
                    this.tryStartRent(contractBaseInfo, paymentBaseInfo);
                }
            } catch (Exception e) {
                log.error("付款申请{}对应的合同尝试起租发生异常", paymentBaseInfo.getPaymentCode(), e);
            }
        }
    }

    /**
     * 合同起租
     */
    private void tryStartRent(ContractBaseInfo contractBaseInfo, PaymentBaseInfo paymentBaseInfo) {
        //查询实际付款明细
        List<PaymentActualDetail> writeOffPayList = paymentActualDetailService.list(
                Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getPaymentId, paymentBaseInfo.getId())
        );
        if (CollectionUtil.isEmpty(writeOffPayList)) {
            return;
        }
        //付款待确认表 中状态为 已确认的  实际付款明细  实付金额
        List<PaymentActualDetailUnconfirmed> confirmedPayList = paymentActualDetailUnconfirmedService.list(
                Wrappers.<PaymentActualDetailUnconfirmed>lambdaQuery()
                        .eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentBaseInfo.getId())
                        .eq(PaymentActualDetailUnconfirmed::getWriteOffStatus, WriteOffStatus.CONFIRM.name())
        );
        long planPayAmount = confirmedPayList.stream().mapToLong(PaymentActualDetailUnconfirmed::getPaidInAmount).sum();
        long actualPayAmount = writeOffPayList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
        if (planPayAmount > actualPayAmount) {
            return;
        }
        // 找合同金额
        ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
        contractPriceDetailREQ.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP contractPriceDetailRSP = SpringUtil.getBean(ContractPriceService.class).detail(contractPriceDetailREQ);
        //实际付款金额< 合同授信金额
        if (actualPayAmount < Optional.ofNullable(contractPriceDetailRSP.getApplyCreditAmount()).orElse(0L)) {
            return;
        }
        //收款明细 中 非 期次>0的租金 未核销，
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listByContractIds(Collections.singletonList(paymentBaseInfo.getContractId()));
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                // 忽略非第0期租金
                if (Objects.equals(collectionBaseInfo.getCashFlowItem(), CashFlowItemEnum.RENT.name()) && collectionBaseInfo.getPhase() > 0) {
                    continue;
                }
                if (!Objects.equals(collectionBaseInfo.getWriteOffStatus(), CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())) {
                    return;
                }
            }
        }
        // 款项全部核销完毕了执行结束投放
        paymentBaseInfoService.finish(paymentBaseInfo.getId());
    }

    @XxlJob("contractStartRentRemindJobHandler")
    public ReturnT<String> contractStartRentRemindJobHandler(String param) {
        log.info("开始执行【合同起租提醒记录】任务[控制台参数: {}]", param);
        LocalDateTime beginOfToday = LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
        // 如果当天是非工作日则不执行任务
        if (!DateUtil.isWorkday(beginOfToday.toLocalDate())) {
            log.info("今日非工作日，不执行起租提醒任务逻辑");
            return ReturnT.SUCCESS;
        }
        // 找到今天之前的所有生效但未起租的合同
        // since 2023-02-01 查询条件变更为只有回租类型的合同需要提示
        LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
        contractQuery.eq(ContractBaseInfo::getLeaseType, LeaseType.hui_zu.name());
        contractQuery.eq(ContractBaseInfo::getContractStatus, ContractStatus.TAKE_EFFECT.name());
        contractQuery.lt(BaseModel::getCreateTime, beginOfToday);
        contractQuery.last(StringUtil.mysqlLimit(0, 500));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractQuery);
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return ReturnT.SUCCESS;
        }
        // 根据付款申请确定是否需要进行起租提醒
        List<ContractRemindRecord> toSaveList = new LinkedList<>();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            // 查询付款申请
            LambdaQueryWrapper<PaymentBaseInfo> paymentQuery = Wrappers.lambdaQuery();
            paymentQuery.eq(PaymentBaseInfo::getContractId, contractBaseInfo.getId());
            paymentQuery.eq(PaymentBaseInfo::getWriteOffStatus, PaymentWriteOffStatus.WRITTEN_OFF.name());
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.list(paymentQuery);
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            // 说明存在符合条件的核销完毕付款记录，需要保存为提醒记录
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                ContractRemindRecord contractRemindRecord = new ContractRemindRecord();
                contractRemindRecord.setContractId(contractBaseInfo.getId());
                contractRemindRecord.setContractCode(contractBaseInfo.getContractCode());
                contractRemindRecord.setContractCreatorId(contractBaseInfo.getProjSponsorUserId());
                contractRemindRecord.setContractCreateTime(contractBaseInfo.getCreateTime());
                contractRemindRecord.setPaymentId(paymentBaseInfo.getId());
                contractRemindRecord.setPaymentCode(paymentBaseInfo.getPaymentCode());
                toSaveList.add(contractRemindRecord);
            }
        }
        if (CollectionUtil.isNotEmpty(toSaveList)) {
            // 查询已有的提醒记录
            List<ContractRemindRecord> all = contractRemindRecordService.list(Wrappers.<ContractRemindRecord>lambdaQuery().last(StringUtil.mysqlLimit(0, 5000)));
            Set<String> existContractCodes = new HashSet<>(all.size());
            if (CollectionUtil.isNotEmpty(all)) {
                for (ContractRemindRecord contractRemindRecord : all) {
                    existContractCodes.add(contractRemindRecord.getContractCode());
                }
            }
            // 剔除已存在的
            toSaveList.removeIf(item -> existContractCodes.contains(item.getContractCode()));
            // 批量保存并生成通知
            if (CollectionUtil.isNotEmpty(toSaveList)) {
                contractRemindRecordService.saveBatch(toSaveList);
                this.sendContractRemindTodoMessage(toSaveList);
                //刷新通知
                timeoutNotifyEventListener.flashStartRentNotice();
            }
        }
        log.info("结束执行【合同起租提醒记录】任务");
        return ReturnT.SUCCESS;
    }

    private void sendContractRemindTodoMessage(List<ContractRemindRecord> contractRemindRecordList) {
        // 生成通知失败不影响保存提醒记录
        for (ContractRemindRecord contractRemindRecord : contractRemindRecordList) {
            try {
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setTo(Collections.singletonList(contractRemindRecord.getContractCreatorId()));
                messageAddREQ.setPcurl("/contract/list");
                messageAddREQ.setContent("合同起租提醒");
                messageAddREQ.setFlowid(contractRemindRecord.getContractCode());
                messageAddREQ.setRelation(String.format(GlobalConstants.CONTRACT_TODO_TEMPLATE, contractRemindRecord.getContractCode(), contractRemindRecord.getPaymentCode()));
                messageAddREQ.setMessageType(MessageTypeEnum.START_RENT.name());
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            } catch (Exception e) {
                log.error("合同起租提醒通知异常[{}]", JSONUtil.toJsonStr(contractRemindRecord), e);
            }
        }
    }
}
