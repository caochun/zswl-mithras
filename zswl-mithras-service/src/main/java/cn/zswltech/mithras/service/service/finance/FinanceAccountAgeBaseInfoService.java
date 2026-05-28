package cn.zswltech.mithras.service.service.finance;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.finance.accountage.*;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.third.FinancialAccountAgeRecordStatus;
import cn.zswltech.mithras.service.enums.third.FinancialAccountAgeSendStatusStatus;
import cn.zswltech.mithras.service.mapper.finance.FinanceAccountAgeBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceAccountAgeBaseInfo;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceAccountAgeItem;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author vico
 * @description 帐龄主表
 * @date 2024-09-10
 */
@Service
public class FinanceAccountAgeBaseInfoService extends ServiceImpl<FinanceAccountAgeBaseInfoMapper, FinanceAccountAgeBaseInfo> {

    @Resource
    private FinanceAccountAgeBaseInfoMapper financeAccountAgeBaseInfoMapper;

    @Resource
    private FinanceAccountAgeItemService financeAccountAgeItemService;

    @Transactional(rollbackFor = Throwable.class)
    public Long add(FinanceAccountAgeBaseInfoAddREQ req) {
        //需要检查
        // 1. 检查“账龄截止日”在当前及之后是否有已创建且生效（状态不为“关闭”）的业务账龄表
        if (financeAccountAgeBaseInfoMapper.selectCount(Wrappers.<FinanceAccountAgeBaseInfo>lambdaQuery()
                .ge(FinanceAccountAgeBaseInfo::getDeadline, req.getDeadline())
                .ne(FinanceAccountAgeBaseInfo::getStatus, FinancialAccountAgeRecordStatus.CLOSED.name())) > 0) {
            throw new MithrasException("该账龄截止日您已创建过业务账龄表，无法创建。请关闭后续业务账龄表或请调整日期后重新创建。");
        }

        // 3. 检查“账龄截止日”之前的所有业务账龄表，必须均为“关闭”或者“完成”的状态，否则提示“该账龄截止日前存在您已创建但未完成的业务账龄表，无法创建。请全部完成或进行关闭后重新创建。”
        if (financeAccountAgeBaseInfoMapper.selectCount(Wrappers.<FinanceAccountAgeBaseInfo>lambdaQuery()
                .le(FinanceAccountAgeBaseInfo::getDeadline, req.getDeadline())
                .eq(FinanceAccountAgeBaseInfo::getStatus, FinancialAccountAgeRecordStatus.NEW.name())) > 0) {
            throw new MithrasException("该账龄截止日前存在您已创建但未完成的业务账龄表，无法创建。请全部完成或进行关闭后重新创建。");
        }

        FinanceAccountAgeBaseInfo info = new FinanceAccountAgeBaseInfo();
        info.setDeadline(req.getDeadline());
        info.setStatus(FinancialAccountAgeRecordStatus.NEW.name());
        if(ObjectUtil.isEmpty(req.getAccountancyOrganizationName())) {
            info.setAccountancyOrganizationName(FinancialConstants.RZZL_NAME);
        }
        if(ObjectUtil.isEmpty(req.getAccountancyOrganizationNumber())) {
            info.setAccountancyOrganizationNumber(FinancialConstants.RZZL_CODE);
        }
        financeAccountAgeBaseInfoMapper.insert(info);
        //这里需要创建子表信息
        financeAccountAgeItemService.accountAgeInit(info.getId());
        //补充上月手工维护数据
        //1.查询最近一次生效数据
        FinanceAccountAgeBaseInfo financeAccountAgeBaseInfo = financeAccountAgeBaseInfoMapper.selectOne(Wrappers.<FinanceAccountAgeBaseInfo>lambdaQuery()
                .le(FinanceAccountAgeBaseInfo::getDeadline, req.getDeadline())
                .eq(FinanceAccountAgeBaseInfo::getStatus, FinancialAccountAgeRecordStatus.FINISH.name())
                .orderByDesc(FinanceAccountAgeBaseInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(financeAccountAgeBaseInfo)) {
            List<FinanceAccountAgeItem> oldItem = financeAccountAgeItemService.list(Wrappers.<FinanceAccountAgeItem>lambdaQuery()
                    .eq(FinanceAccountAgeItem::getAccountAgeId, financeAccountAgeBaseInfo.getId())
                    .eq(FinanceAccountAgeItem::getSource, YesOrNoNumberEnum.YES.getCode()));
            if (ObjectUtil.isNotEmpty(oldItem)) {
                oldItem.forEach(e -> {
                    e.setId(null);
                    e.setCreateTime(null);
                    e.setCreateBy(null);
                    e.setUpdateTime(null);
                    e.setUpdateBy(null);
                    e.setSendStatus(null);
                    e.setAccountAgeId(info.getId());
                    e.setOriginalValueInitial(e.getOriginalValueFinal());
                    e.setOriginalValueIncrease(BigDecimal.ZERO);
                    e.setOriginalValueReduce(BigDecimal.ZERO);
                    e.setAgingDeadline(req.getDeadline());
                });
                financeAccountAgeItemService.saveBatch(oldItem);
            }
        }
        return info.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void close(FinanceAccountAgeBaseInfoCloseREQ req) {
        this.check(req.getId());
        FinanceAccountAgeBaseInfo originalInfo = financeAccountAgeBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (financeAccountAgeItemService.count(Wrappers.<FinanceAccountAgeItem>lambdaQuery()
                .eq(FinanceAccountAgeItem::getAccountAgeId, req.getId())
                .eq(FinanceAccountAgeItem::getSendStatus, FinancialAccountAgeSendStatusStatus.SUCCESS.name())) > 0) {
            throw new MithrasException("存在已推送苍穹数据，不能关闭");
        }
        LambdaUpdateWrapper<FinanceAccountAgeBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FinanceAccountAgeBaseInfo::getId, req.getId());
        updateWrapper.set(FinanceAccountAgeBaseInfo::getStatus, FinancialAccountAgeRecordStatus.CLOSED.name());
        financeAccountAgeBaseInfoMapper.update(null, updateWrapper);
    }

    public Page<FinanceAccountAgeBaseInfo> list(FinanceAccountAgeBaseInfoListREQ req) {
        return financeAccountAgeBaseInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FinanceAccountAgeBaseInfo>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getStatus()), FinanceAccountAgeBaseInfo::getStatus, req.getStatus())
                .orderByDesc(FinanceAccountAgeBaseInfo::getDeadline)
                .orderByDesc(FinanceAccountAgeBaseInfo::getId)
        );
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FinanceAccountAgeBaseInfoRemoveREQ req) {
        this.check(req.getId());
        FinanceAccountAgeBaseInfo originalInfo = financeAccountAgeBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaUpdateWrapper<FinanceAccountAgeBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FinanceAccountAgeBaseInfo::getId, req.getId());
        updateWrapper.set(FinanceAccountAgeBaseInfo::getDeleted, YesOrNoNumberEnum.YES.getCode());
        financeAccountAgeBaseInfoMapper.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void effect(@Valid FinanceAccountAgeBaseInfoRemoveREQ req) {
        this.check(req.getId());
        FinanceAccountAgeBaseInfo originalInfo = financeAccountAgeBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!(financeAccountAgeItemService.count(Wrappers.<FinanceAccountAgeItem>lambdaQuery()
                .eq(FinanceAccountAgeItem::getAccountAgeId, req.getId())
                .eq(FinanceAccountAgeItem::getSendStatus, FinancialAccountAgeSendStatusStatus.SUCCESS.name())) > 0)) {
            throw new MithrasException("不存在已推送苍穹数据，无法完成");
        }
        LambdaUpdateWrapper<FinanceAccountAgeBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FinanceAccountAgeBaseInfo::getId, req.getId());
        updateWrapper.set(FinanceAccountAgeBaseInfo::getStatus, FinancialAccountAgeRecordStatus.FINISH.name());
        financeAccountAgeBaseInfoMapper.update(null, updateWrapper);
    }

    public void check(Long id) {
        FinanceAccountAgeBaseInfo originalInfo = financeAccountAgeBaseInfoMapper.selectById(id);
        if (ObjectUtil.isEmpty(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ObjectUtil.equals(originalInfo.getStatus(), FinancialAccountAgeRecordStatus.NEW.name())) {
            throw new MithrasException("帐龄已完成，不可变更");
        }
    }

}