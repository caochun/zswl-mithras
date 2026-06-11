package cn.zswltech.mithras.fund.application.receiptrepay;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseModifyREQ;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundReceiptRepayExpenseConverter;
import cn.zswltech.mithras.fund.application.receiptrepay.port.FundReceiptFlowDetailAmountPort;
import cn.zswltech.mithras.fund.application.receiptrepay.port.FundReceiptRepayStateUpdatePort;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayExpenseMapper;
import cn.zswltech.mithras.fund.mapper.lib.receiptrepay.FundReceiptRepayExpenseLibMapper;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayExpenseLib;
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
 * @description 费用一览表
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayExpenseService
        extends ServiceImpl<FundReceiptRepayExpenseMapper, FundReceiptRepayExpense> {

    @Resource
    private FundReceiptRepayExpenseLibMapper baseLibMapper;
    @Resource
    private FundReceiptRepayExpenseConverter baseConverter;
    @Resource
    private FundReceiptRepayStateUpdatePort fundReceiptRepayStateUpdatePort;
    @Resource
    private FundReceiptFlowDetailAmountPort fundReceiptFlowDetailAmountPort;

    public List<FundReceiptRepayExpense> listByReceiptRepayIds(Collection<Long> receiptRepayIds) {
        LambdaQueryWrapper<FundReceiptRepayExpense> query = Wrappers.lambdaQuery();
        query.in(FundReceiptRepayExpense::getReceiptRepayId, receiptRepayIds);
        return this.list(query);
    }

    public List<FundReceiptRepayExpense> listByReceiptRepayId(Long receiptRepayId) {
        LambdaQueryWrapper<FundReceiptRepayExpense> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayExpense::getReceiptRepayId, receiptRepayId);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<FundReceiptRepayExpenseModifyREQ> req) {
        if (ObjectUtil.isEmpty(req)) {
            throw new MithrasException("编辑数据不能为空");
        }
        List<FundReceiptRepayExpense> entityList = baseConverter.modifyReq2Entity(req);
        updateBatchById(entityList);
        FundReceiptRepayExpense one = baseMapper.selectById(req.get(0).getId());
        fundReceiptRepayStateUpdatePort.modifyUpdateProcessState(one.getReceiptRepayId());
    }

    public PageR<FundReceiptRepayExpenseListRSP> list(FundReceiptRepayExpenseListREQ req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            Page<FundReceiptRepayExpenseLib> libs = baseLibMapper.selectPage(
                    new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<FundReceiptRepayExpenseLib>lambdaQuery()
                            .eq(FundReceiptRepayExpenseLib::getVersion, req.getVersion())
                            .eq(FundReceiptRepayExpense::getReceiptRepayId, req.getReceiptRepayId()));
            return PageR.of(libs, baseConverter.lib2ListRsp(libs.getRecords()));
        }
        Page<FundReceiptRepayExpense> page = page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                        .eq(FundReceiptRepayExpense::getReceiptRepayId, req.getReceiptRepayId()));
        return PageR.of(page, baseConverter.entity2ListRsp(page.getRecords()));
    }

    //维护核销状态
    @Transactional(rollbackFor = Throwable.class)
    public void doWriteOffStateModify(Long receiptRepayId, String cashFlowCode) {
        LambdaQueryWrapper<FundReceiptRepayExpense> queryExpense = Wrappers.lambdaQuery();
        queryExpense.eq(FundReceiptRepayExpense::getReceiptRepayId, receiptRepayId);
        queryExpense.eq(FundReceiptRepayExpense::getCashFlowCode, cashFlowCode);
        FundReceiptRepayExpense fundReceiptRepayExpense = this.getOne(queryExpense);
        if (ObjectUtil.isEmpty(fundReceiptRepayExpense)) {
            return;
        }
        long hasVerifyAmountExpense = fundReceiptFlowDetailAmountPort.sum(fundReceiptRepayExpense.getReceiptRepayId(), fundReceiptRepayExpense.getCashFlowCode());
        long remainingAmountExpense = fundReceiptRepayExpense.getTotalAmount() - hasVerifyAmountExpense;

        if (hasVerifyAmountExpense <= 0) {
            fundReceiptRepayExpense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
        } else if (remainingAmountExpense < 0) {
            fundReceiptRepayExpense.setWriteOffState(CashFlowState.BEYOND_WRITTEN_OFF.name());
        } else if (remainingAmountExpense != 0) {
            fundReceiptRepayExpense.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayExpense.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        // 变更已付总费用
        fundReceiptRepayExpense.setTotalPaidAmount(hasVerifyAmountExpense);
        this.updateById(fundReceiptRepayExpense);
    }


}
