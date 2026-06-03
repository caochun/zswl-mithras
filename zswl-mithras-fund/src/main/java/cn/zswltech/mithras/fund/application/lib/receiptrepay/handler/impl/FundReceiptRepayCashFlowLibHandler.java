package cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 本金与利息一览表
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:56 PM
 */
@Component
public class FundReceiptRepayCashFlowLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayCashFlowLib, FundReceiptRepayCashFlow, FundReceiptRepayCashFlowListRSP> {

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     * 只回滚备注 其他数据不能回滚 这张表数据是从融资管理同步过来的，回滚就完蛋了
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reset(Long mainId, String version) {
        List<FundReceiptRepayCashFlow> needHandleEntityList = listNeedHandleEntity(mainId);
        List<FundReceiptRepayCashFlowLib> latestVersionLibList = listNeedHandleLib(mainId, version);
        Map<Long, FundReceiptRepayCashFlowLib> libMap = latestVersionLibList.stream().collect(Collectors.toMap(FundReceiptRepayCashFlowLib::getOriginId, c -> c));
        for (FundReceiptRepayCashFlow entity : needHandleEntityList) {
            if (libMap.containsKey(entity.getId())) {
                entity.setRemark(libMap.get(entity.getId()).getRemark());
                draftMapper.updateById(entity);
            }
        }
    }

    @Override
    protected FundReceiptRepayCashFlowLib entity2Lib(FundReceiptRepayCashFlow f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayCashFlowLib.class);
    }

    @Override
    protected FundReceiptRepayCashFlow lib2Entity(FundReceiptRepayCashFlowLib t) {
        return BeanUtil.copyProperties(t, FundReceiptRepayCashFlow.class);
    }

    @Override
    protected FundReceiptRepayCashFlowListRSP lib2Rsp(FundReceiptRepayCashFlowLib f) {
        FundReceiptRepayCashFlowListRSP rsp = BeanUtil.copyProperties(f, FundReceiptRepayCashFlowListRSP.class);
        rsp.setId(f.getOriginId());
        LocalDate lastDayOfThatMonth = f.getCreateTime().toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfThatMonth = f.getCreateTime().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
        rsp.setIsRed(false);
        if (!f.getRepayDate().isAfter(lastDayOfThatMonth) && !CashFlowState.WRITTEN_OFF.name().equals(f.getWriteOffState())) {
            // 月底前 未核销完毕数据 更新为 红色
            rsp.setIsRed(true);
        } else if (Objects.nonNull(f.getApprovalPassDate()) && !f.getApprovalPassDate().isBefore(firstDayOfThatMonth) && !f.getApprovalPassDate().isAfter(lastDayOfThatMonth)) {
            // 核销审批通过时间在本月内
            rsp.setIsRed(true);
        }
        return rsp;
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.CASH_FLOW;
    }

    /**
     * 过滤出需要处理的编辑区数据 有过滤条件的自实现
     * 默认抄全表
     * @param mainId
     * @return
     */
    @Override
    public List<FundReceiptRepayCashFlow> listNeedHandleEntity(Long mainId) {
        List<FundReceiptRepayCashFlow> draftDataList = draftMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .eq(FundReceiptRepayCashFlow::getReceiptRepayId, mainId)
                .orderByAsc(FundReceiptRepayCashFlow::getRepayDate)
        );
        return draftDataList;
    }

    /**
     * 过滤出需要处理的版本区数据 有过滤条件的自实现
     * 默认抄全表
     * @param mainId
     * @return
     */
    @Override
    public List<FundReceiptRepayCashFlowLib> listNeedHandleLib(Long mainId, String version) {
        List<FundReceiptRepayCashFlowLib> versionList = mapper.selectList(Wrappers.<FundReceiptRepayCashFlowLib>lambdaQuery()
                .eq(FundReceiptRepayCashFlowLib::getVersion, version)
                .eq(FundReceiptRepayCashFlowLib::getReceiptRepayId, mainId)
                .orderByAsc(FundReceiptRepayCashFlowLib::getRepayDate)
        );
        return versionList;
    }

}
