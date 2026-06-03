package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.zswl.notice.model.message.MessageModel;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.message.MessageReadREQ;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.versioning.application.ContractRentActualLibService;
import cn.zswltech.mithras.service.service.message.MessageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import liquibase.pro.packaged.M;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2023/11/22/18:51
 * @description 定时扫描租金还款表，提醒财务尽快核销
 */
@Slf4j
@Component
public class RentRepayNoticeFinanceJob {
    private static final String LOCK_KEY = "RentRepayNoticeFinanceJob";
    @Resource
    private RedisDistLock lock;
    @Resource
    private MessageService messageService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private SysUserService sysUserService;

    @XxlJob(value = "rentRepayNoticeFinance")
    public void rentRepayNoticeFinance() {
        try {
            lock.tryLockWithoutReleaseTime(LOCK_KEY, 10000);
            try {
                log.info("定时任务租金还款通知财务开始了");
                List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
                        .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()));

                Map<String, List<CollectionBaseInfo>> baseInfoMap = Optional.ofNullable(list).orElse(Collections.emptyList()).stream()
                        .collect(Collectors.groupingBy(CollectionBaseInfo::getContractCode));

//                Set<Long> collectionRecordIds = Optional.ofNullable(list)
//                        .orElse(Collections.emptyList())
//                        .stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet());
//
//                List<CollectionRecordInfo> collectionRecordInfos = collectionRecordInfoService.listByIds(collectionRecordIds);
//                Map<Long, List<CollectionRecordInfo>> recordMap = Optional.ofNullable(collectionRecordInfos).orElse(Collections.emptyList()).stream()
//                        .collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
//
//                List<CollectionRecordInfo> data = new ArrayList<>();
//                for (Map.Entry<Long, List<CollectionRecordInfo>> entry : recordMap.entrySet()) {
//                    data.addAll(entry.getValue().stream()
//                            .filter(a -> WriteOffStatus.TO_BE_WRITE_OFF.name().equals(a.getWriteOffStatus()))
//                            .filter(a -> WriteOffStatus.WRITTEN_OFF.name().equals(a.getWriteOffStatus()) && a.getCollectionAmount() != a.getPrincipal() + a.getInterest())
//                            .collect(Collectors.toList()));
//                }
                //拼接通知格式
                StringBuilder buffer = new StringBuilder();
                Integer flag = 0;
                if (CollUtil.isNotEmpty(baseInfoMap)) {
                    for (Map.Entry<String, List<CollectionBaseInfo>> entry : baseInfoMap.entrySet()) {
                        if (flag < 3) {
                            buffer.append(entry.getKey()).append("、");
                            flag++;
                        } else {
                            buffer = new StringBuilder(buffer.substring(0, buffer.length() - 1));
                            buffer.append("等").append(baseInfoMap.size()).append("个合同");
                            break;
                        }
                    }
                }

                //发送通知
                Set<Long> userIds = new HashSet<>();
                userIds.addAll(sysUserService.queryJobUserIds(JobEnum.cashier.name()));
                userIds.addAll(sysUserService.queryJobUserIds(JobEnum.financialmanager.name()));
                userIds.addAll(sysUserService.queryJobUserIds(JobEnum.financialofficer.name()));
                if (CollUtil.isNotEmpty(userIds) && buffer.length() > 0) {
                    for (Long userId : userIds) {
                        MessageAddREQ addRequest = new MessageAddREQ();
                        addRequest.setFrom("系统通知");
                        addRequest.setTo(Collections.singletonList(userId));
                        addRequest.setPcurl("/cpm/collectionWriteOff");
                        addRequest.setContent("收款核销提醒");
                        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
                        addRequest.setRelation(String.format("合同编号为：%s已经过了收款时间还未核销，请尽快核销，否则可能造成逾期！", buffer));
                        addRequest.setMessageType(MessageTypeEnum.COLLECTION_NOTICE.name());
                        messageService.sendMessage(messageConver.reqToMessage(addRequest));
                    }
                }
                log.info("定时任务租金还款通知财务结束了！待核销数量为：{}", baseInfoMap.size());
            } catch (Exception e) {
                log.error("定时任务租金还款通知财务出错了，错误信息：%s", e);
            }
        } catch (Exception e) {
            log.warn("定时任务租金还款通知财务加锁失败，错误信息：%s", e);
        } finally {
            lock.unlock(LOCK_KEY);
        }
    }
}
