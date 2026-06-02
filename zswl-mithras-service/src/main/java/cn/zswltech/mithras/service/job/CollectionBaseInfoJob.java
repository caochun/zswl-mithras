package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.archive.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.message.MessageService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

/**
 * @author zhouning
 * @date 2024/07/08/18:51
 * @description 定时扫描租金还款表，提前30天提醒
 */
@Slf4j
@Component
public class CollectionBaseInfoJob {
    private static final String LOCK_KEY = "CollectionBaseInfoJob";
    @Resource
    private RedisDistLock lock;
    @Resource
    private MessageService messageService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoLibHandler baseInfoLibHandler;
    @Resource
    private Id2NameService id2NameService;

    @XxlJob(value = "collectionBaseInfoMsg")
    public void collectionBaseInfoMsg() {
        try {
            lock.tryLockWithoutReleaseTime(LOCK_KEY, 10000);
            try {
                log.info("定时任务收租提醒开始了");
                List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
                        .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.UNCOLLECTION.name()));
                if (collectionBaseInfoList.isEmpty()) {
                    log.info("定时任务收租数量为: 0");
                    return;
                }
                List<CollectionBaseInfo> list = new ArrayList<>();
                LocalDate currentDate = LocalDate.now();
                for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                    if (currentDate.plusDays(30).equals(collectionBaseInfo.getPlanCollectionDate())) {
                        list.add(collectionBaseInfo);
                    }
                }
                if (list.isEmpty()) {
                    return;
                }

                Map<String, List<CollectionBaseInfo>> baseInfoMap = Optional.ofNullable(list).orElse(Collections.emptyList()).stream()
                        .collect(Collectors.groupingBy(CollectionBaseInfo::getContractCode));
                Set<Long> userIds = new HashSet<>();
                //法律合规部
                Set<Long> flhgbSet = sysUserService.getUserByDeptCode("FLHGB_ZCBQ").stream().map(UserDO::getId).collect(Collectors.toSet());
                //财务部
                Set<Long> jhcwbSet = sysUserService.getUserByDeptCode("JHCWB").stream().map(UserDO::getId).collect(Collectors.toSet());
                //资金部
                Set<Long> zjglbSet = sysUserService.getUserByDeptCode("ZJGLB").stream().map(UserDO::getId).collect(Collectors.toSet());
                userIds.addAll(flhgbSet);
                userIds.addAll(jhcwbSet);
                userIds.addAll(zjglbSet);

                for (CollectionBaseInfo info : list) {
                    Set<Long> newUserIds = new HashSet<>();
                    newUserIds.addAll(userIds);
                    ContractBaseInfoLib detail = baseInfoLibHandler.queryLatestDataByOriginId(info.getContractId());
                    Map<Long, String> clientMap = id2NameService.clientId2Name(Collections.singleton(detail.getClientId()));
                    if (isNotNull(detail.getProjSponsorUserId())) {
                        newUserIds.add(detail.getProjSponsorUserId());
                    }
                    if (isNotNull(detail.getProjCosponsorUserIds())) {
                        List<Long> cosponsorUserIds = JSONUtil.toList(detail.getProjCosponsorUserIds(), Long.class);
                        newUserIds.addAll(cosponsorUserIds);
                    }
                    if (isNotNull(detail.getBizDeptLeaderId())) {
                        newUserIds.add(detail.getBizDeptLeaderId());
                    }
                    MessageAddREQ addRequest = new MessageAddREQ();
                    addRequest.setFrom("系统通知");
                    addRequest.setTo(new ArrayList<>(newUserIds));
                    addRequest.setPcurl(StringUtils.format("/cpm/collectionWriteOff/detail/%s", info.getId()));
                    addRequest.setContent(info.getCode());
                    addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
                    addRequest.setNeedOa(false);
                    addRequest.setRelation(String.format("%s-%s第%s期租金即将于%s到期",
                            clientMap.get(detail.getClientId()), detail.getContractCode(), info.getPhase(), info.getPlanCollectionDate()));
                    addRequest.setMessageType(MessageTypeEnum.RENT.name());
                    addRequest.setNoticeSource(NoticeSourceENUM.RENT.name());
                    messageService.sendMessage(messageConver.reqToMessage(addRequest));
                }
                log.info("定时任务收租提醒结束了！待核销数量为：{}", list.size());
            } catch (Exception e) {
                log.error("定时任务收租提醒出错了，错误信息：%s", e);
            }
        } catch (Exception e) {
            log.warn("定时任务收租提醒加锁失败，错误信息：%s", e);
        } finally {
            lock.unlock(LOCK_KEY);
        }
    }
}
