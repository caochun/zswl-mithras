package cn.zswltech.mithras.fund.application.receiptrepay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositModifyREQ;
import cn.zswltech.mithras.fund.application.receiptrepay.port.FundReceiptFlowDetailAmountPort;
import cn.zswltech.mithras.fund.application.receiptrepay.port.FundReceiptRepayStateUpdatePort;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundReceiptRepayCashDepositConverter;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.persistence.mapper.receiptrepay.FundReceiptRepayCashDepositMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.receiptrepay.FundReceiptRepayCashDepositLibMapper;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashDeposit;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashDepositLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 保证金明细
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayCashDepositService
        extends ServiceImpl<FundReceiptRepayCashDepositMapper, FundReceiptRepayCashDeposit> {
    @Resource
    private FundReceiptRepayCashDepositLibMapper baseLibMapper;
    @Resource
    private FundReceiptRepayCashDepositConverter baseConverter;
    @Resource
    private FundReceiptRepayStateUpdatePort fundReceiptRepayStateUpdatePort;
    @Resource
    private FundReceiptFlowDetailAmountPort fundReceiptFlowDetailAmountPort;

    public List<FundReceiptRepayCashDeposit> listByReceiptRepayIds(Collection<Long> receiptRepayIds) {
        LambdaQueryWrapper<FundReceiptRepayCashDeposit> query = Wrappers.lambdaQuery();
        query.in(FundReceiptRepayCashDeposit::getReceiptRepayId, receiptRepayIds);
        return this.list(query);
    }

    public PageR<FundReceiptRepayCashDepositListRSP> list(FundReceiptRepayCashDepositListREQ req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            Page<FundReceiptRepayCashDepositLib> libPage = baseLibMapper.selectPage(
                    new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<FundReceiptRepayCashDepositLib>lambdaQuery()
                            .eq(FundReceiptRepayCashDepositLib::getReceiptRepayId, req.getReceiptRepayId())
                            .eq(FundReceiptRepayCashDepositLib::getVersion, req.getVersion()));
            return PageR.of(libPage, baseConverter.lib2ListRsp(libPage.getRecords()));
        }
        Page<FundReceiptRepayCashDeposit> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundReceiptRepayCashDeposit>lambdaQuery()
                        .eq(FundReceiptRepayCashDeposit::getReceiptRepayId, req.getReceiptRepayId()));
        return PageR.of(page, baseConverter.entity2ListRsp(page.getRecords()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<FundReceiptRepayCashDepositModifyREQ> req) {
        if (ObjectUtil.isEmpty(req)) {
            throw new MithrasException("编辑数据不能为空");
        }
        for (FundReceiptRepayCashDepositModifyREQ modifyReq : req) {
            modify(modifyReq);
        }
        FundReceiptRepayCashDeposit one = baseMapper.selectById(req.get(0).getId());
        fundReceiptRepayStateUpdatePort.modifyUpdateProcessState(one.getReceiptRepayId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundReceiptRepayCashDepositModifyREQ req) {
        FundReceiptRepayCashDeposit originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptRepayCashDeposit info = BeanUtil.copyProperties(req, FundReceiptRepayCashDeposit.class);
        baseMapper.updateById(info);
    }

    //维护核销状态
    @Transactional(rollbackFor = Throwable.class)
    public void doWriteOffStateModify(Long receiptRepayId, String cashFlowCode) {
        LambdaQueryWrapper<FundReceiptRepayCashDeposit> queryDeposit = Wrappers.lambdaQuery();
        queryDeposit.eq(FundReceiptRepayCashDeposit::getReceiptRepayId, receiptRepayId);
        queryDeposit.eq(FundReceiptRepayCashDeposit::getCashFlowCode, cashFlowCode);
        FundReceiptRepayCashDeposit fundReceiptRepayCashDeposit = this.getOne(queryDeposit);
        if (ObjectUtil.isEmpty(fundReceiptRepayCashDeposit)) {
            return;
        }
        long hasVerifyAmountDeposit = fundReceiptFlowDetailAmountPort.sum(fundReceiptRepayCashDeposit.getReceiptRepayId(), fundReceiptRepayCashDeposit.getCashFlowCode());
        long remainingAmountDeposit = fundReceiptRepayCashDeposit.getAmount() - hasVerifyAmountDeposit;
        if (hasVerifyAmountDeposit <= 0) {
            fundReceiptRepayCashDeposit.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
        } else if (remainingAmountDeposit < 0) {
            fundReceiptRepayCashDeposit.setWriteOffState(CashFlowState.BEYOND_WRITTEN_OFF.name());
        } else if (remainingAmountDeposit != 0) {
            fundReceiptRepayCashDeposit.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayCashDeposit.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        this.updateById(fundReceiptRepayCashDeposit);
    }

}
