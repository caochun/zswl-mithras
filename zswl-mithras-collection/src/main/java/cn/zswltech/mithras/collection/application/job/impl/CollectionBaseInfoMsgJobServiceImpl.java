package cn.zswltech.mithras.collection.application.job.impl;

import cn.zswltech.mithras.collection.application.job.CollectionBaseInfoMsgJobService;
import cn.zswltech.mithras.collection.application.job.CollectionNotificationPort;
import cn.zswltech.mithras.collection.application.job.CollectionRentDueContractInfo;
import cn.zswltech.mithras.collection.application.job.CollectionRentDueContractInfoPort;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import cn.zswltech.mithras.foundation.port.DeptUserResolver;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

/**
 * @author zhouning
 * @date 2024/07/08/18:51
 * @description 定时扫描租金还款表，提前30天提醒
 */
@Slf4j
@Component
public class CollectionBaseInfoMsgJobServiceImpl implements CollectionBaseInfoMsgJobService {
    private static final String LOCK_KEY = "CollectionBaseInfoJob";
    @Resource
    private RedisDistLock lock;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionNotificationPort notificationPort;
    @Resource
    private DeptUserResolver deptUserResolver;
    @Resource
    private CollectionRentDueContractInfoPort contractInfoPort;
    @Resource
    private ClientNameResolver clientNameResolver;

    @Override
    public void collectionBaseInfoMsg() {
        try {
            lock.tryLockWithoutReleaseTime(LOCK_KEY, 10000);
            try {
                log.info("定时任务收租提醒开始了");
                List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
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

                Set<Long> userIds = new HashSet<>();
                //法律合规部
                Set<Long> flhgbSet = deptUserResolver.userIdsByDeptCode("FLHGB_ZCBQ");
                //财务部
                Set<Long> jhcwbSet = deptUserResolver.userIdsByDeptCode("JHCWB");
                //资金部
                Set<Long> zjglbSet = deptUserResolver.userIdsByDeptCode("ZJGLB");
                userIds.addAll(flhgbSet);
                userIds.addAll(jhcwbSet);
                userIds.addAll(zjglbSet);

                for (CollectionBaseInfo info : list) {
                    Set<Long> newUserIds = new HashSet<>();
                    newUserIds.addAll(userIds);
                    CollectionRentDueContractInfo detail = contractInfoPort.getLatestContractInfo(info.getContractId());
                    if (detail == null) {
                        continue;
                    }
                    Map<Long, String> clientMap = clientNameResolver.clientId2Name(Collections.singleton(detail.getClientId()));
                    if (isNotNull(detail.getProjSponsorUserId())) {
                        newUserIds.add(detail.getProjSponsorUserId());
                    }
                    if (isNotNull(detail.getProjCosponsorUserIds())) {
                        newUserIds.addAll(detail.getProjCosponsorUserIds());
                    }
                    if (isNotNull(detail.getBizDeptLeaderId())) {
                        newUserIds.add(detail.getBizDeptLeaderId());
                    }
                    String relation = String.format("%s-%s第%s期租金即将于%s到期",
                            clientMap.get(detail.getClientId()), detail.getContractCode(), info.getPhase(), info.getPlanCollectionDate());
                    notificationPort.sendRentDueRemind(new ArrayList<>(newUserIds), info.getId(), info.getCode(), relation);
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
