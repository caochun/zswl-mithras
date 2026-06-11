package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportClientInfo;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.creditreport.enums.CreditApplyStatusEnum;
import cn.zswltech.mithras.creditreport.mapper.CreditReportBaseInfoMapper;
import cn.zswltech.mithras.creditreport.mapper.CreditReportClientItemMapper;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportBaseInfo;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportClientItem;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 征信报告客户表
* @author vico
* @date 2025-11-24
*/
@Service
public class CreditReportClientItemService extends ServiceImpl<CreditReportClientItemMapper, CreditReportClientItem> {

    @Resource
    private CreditReportBaseInfoMapper creditReportBaseInfoMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long creditReportBaseInfoId, List<Long> surviveIds) {
        if (ObjectUtil.isEmpty(creditReportBaseInfoId)) {
            throw new MithrasException("不允许跨申请删除");
        }
        LambdaUpdateWrapper<CreditReportClientItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(CreditReportClientItem::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(CreditReportClientItem::getCreditReportBaseInfoId, creditReportBaseInfoId);
        if (ObjectUtil.isNotEmpty(surviveIds)) {
            updateWrapper.notIn(CreditReportClientItem::getId, surviveIds);
        }
        this.update(updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addBatch(List<CreditReportClientInfo> addRecords) {
        if (ObjectUtil.isEmpty(addRecords)) {
            return;
        }
        List<CreditReportClientItem> clientItems = BeanUtil.copyToList(addRecords, CreditReportClientItem.class);
        //判断是否需要新增档案

        this.saveBatch(clientItems);
        //带入相关附件
    }

    //Map<clientId, 是否需要注册档案>
    public Map<Long, Boolean> needAddArchive(List<Long> clientIds, Long id) {
        if (ObjectUtil.isEmpty(clientIds)) {
            return MapUtil.empty();
        }
        List<CreditReportClientItem> clientItems = this.list(Wrappers.<CreditReportClientItem>lambdaQuery()
                .in(CreditReportClientItem::getClientId, clientIds)
        .ne(ObjectUtil.isNotEmpty(id), CreditReportClientItem::getCreditReportBaseInfoId, id));
        LocalDate now = LocalDate.now();
        Map<Long, LocalDate> oldReportId2Date = new HashMap<>();
        Map<Long, List<CreditReportClientItem>> clientId2ClientItemMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(clientItems)) {
            clientId2ClientItemMap.putAll(clientItems.stream().collect(Collectors.groupingBy(CreditReportClientItem::getCreditReportBaseInfoId)));
            //查询到期时间
            List<CreditReportBaseInfo> creditReportBaseInfos = creditReportBaseInfoMapper.selectList(Wrappers.<CreditReportBaseInfo>lambdaQuery()
                            .in(CreditReportBaseInfo::getId, clientItems.stream().map(CreditReportClientItem::getCreditReportBaseInfoId).collect(Collectors.toList()))
                            .eq(CreditReportBaseInfo::getApplyStatus, CreditApplyStatusEnum.PASS.name()));
            if (ObjectUtil.isNotEmpty(creditReportBaseInfos)) {
                creditReportBaseInfos.forEach(e -> {
                    //存在今天之后的才允许注册
                    if (e.getAuthorizationEndDate()!= null && e.getAuthorizationEndDate().isAfter(now)) {
                        oldReportId2Date.put(e.getId(), e.getAuthorizationEndDate());
                    }
                });
            }
        }
        //
        Map<Long, Boolean> resultMap = new HashMap<>();
        clientIds.forEach(clientId -> {
            List<CreditReportClientItem> clientItemList = clientId2ClientItemMap.get(clientId);
            if (ObjectUtil.isNotEmpty(clientItemList)) {
                Boolean need = Boolean.FALSE;
                for (CreditReportClientItem item : clientItemList) {
                    if (oldReportId2Date.get(item.getCreditReportBaseInfoId()) != null) {
                        need = Boolean.TRUE;
                    }
                }
                resultMap.put(clientId, need);
            } else {
                resultMap.put(clientId, Boolean.TRUE);
            }
        });
        return resultMap;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateBatch(List<CreditReportClientInfo> addRecords) {
        if (ObjectUtil.isEmpty(addRecords)) {
            return;
        }
        Map<Long, CreditReportClientInfo> id2Bean = addRecords.stream().collect(Collectors.toMap(CreditReportClientInfo::getId, e -> e, (a, b) -> b));
        List<CreditReportClientItem> clientItems = this.listByIds(addRecords.stream().map(CreditReportClientInfo::getId).collect(Collectors.toList()));
        if (ObjectUtil.isEmpty(clientItems)) {
            throw new MithrasException("未查询到需要更新到记录");
        }
        clientItems.forEach(e -> {
            CreditReportClientInfo info = id2Bean.get(e.getId());
            if (ObjectUtil.isNotEmpty(info)) {
                e.setClientId(info.getClientId());
                e.setClientName(info.getClientName());
                e.setCscCode(info.getCscCode());
                e.setZhongZhengCode(info.getZhongZhengCode());
                e.setSelectGoal(info.getSelectGoal());
            }
        });
        this.updateBatchById(clientItems);
    }

    public List<CreditReportClientItem> listByBaseInfoId(Long creditReportBaseInfoId) {
        return this.list(Wrappers.<CreditReportClientItem>lambdaQuery()
        .eq(CreditReportClientItem::getCreditReportBaseInfoId, creditReportBaseInfoId));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifySelectStatus(List<Long> clientItemIds, String selectStatus) {
        if (ObjectUtil.isEmpty(clientItemIds) || ObjectUtil.isEmpty(selectStatus)) {
            return;
        }
        LambdaUpdateWrapper<CreditReportClientItem> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(CreditReportClientItem::getSelectStatus, selectStatus);
        lambdaUpdateWrapper.in(CreditReportClientItem::getId, clientItemIds);
        update(lambdaUpdateWrapper);
    }



}
