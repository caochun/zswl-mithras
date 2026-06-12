package cn.zswltech.mithras.application.orchestration.fund.receiptrepay;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingModifyREQ;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundReceiptRepayBorrowingConverter;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.persistence.mapper.receiptrepay.FundReceiptRepayBorrowingMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.receiptrepay.FundReceiptRepayBorrowingLibMapper;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBorrowingLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
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
 * @description 借款流入
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayBorrowingService
        extends ServiceImpl<FundReceiptRepayBorrowingMapper, FundReceiptRepayBorrowing> {
    @Resource
    private FundReceiptRepayBorrowingLibMapper baseLibMapper;
    @Resource
    private FundReceiptRepayBorrowingConverter baseConverter;
    @Resource
    private FundReceiptRepayStateService fundReceiptRepayStateService;

    public List<FundReceiptRepayBorrowing> listByReceiptRepayIds(Collection<Long> receiptRepayIds) {
        LambdaQueryWrapper<FundReceiptRepayBorrowing> query = Wrappers.lambdaQuery();
        query.in(FundReceiptRepayBorrowing::getReceiptRepayId, receiptRepayIds);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<FundReceiptRepayBorrowingModifyREQ> req) {
        if (ObjectUtil.isEmpty(req)) {
            throw new MithrasException("编辑数据不能为空");
        }
        FundReceiptRepayBorrowing one = baseMapper.selectById(req.get(0).getId());
        List<FundReceiptRepayBorrowing> tobeModify = baseConverter.modifyReq2Entity(req);
        updateBatchById(tobeModify);
        fundReceiptRepayStateService.modifyUpdateProcessState(one.getReceiptRepayId());
    }

    public PageR<FundReceiptRepayBorrowingListRSP> list(FundReceiptRepayBorrowingListREQ req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            Page<FundReceiptRepayBorrowingLib> libs = baseLibMapper.selectPage(new Page<>(
                            req.getPage(), req.getPageSize()),
                    Wrappers.<FundReceiptRepayBorrowingLib>lambdaQuery()
                            .eq(FundReceiptRepayBorrowingLib::getReceiptRepayId, req.getReceiptRepayId())
                            .eq(FundReceiptRepayBorrowingLib::getVersion, req.getVersion()));
            return PageR.of(libs, baseConverter.lib2ListRsp(libs.getRecords()));
        }
        Page<FundReceiptRepayBorrowing> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundReceiptRepayBorrowing>lambdaQuery()
                        .eq(FundReceiptRepayBorrowing::getReceiptRepayId, req.getReceiptRepayId()));
        return PageR.of(page, baseConverter.entity2ListRsp(page.getRecords()));
    }

    public FundReceiptRepayBorrowing getOneByReceiptRepayId(Long receiptRepayId) {
        LambdaQueryWrapper<FundReceiptRepayBorrowing> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayBorrowing::getReceiptRepayId, receiptRepayId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    //维护核销状态
    @Transactional(rollbackFor = Throwable.class)
    public void doWriteOffStateModify(Long receiptRepayId, String cashFlowCode) {
        LambdaQueryWrapper<FundReceiptRepayBorrowing> queryBorrowing = Wrappers.lambdaQuery();
        queryBorrowing.eq(FundReceiptRepayBorrowing::getReceiptRepayId, receiptRepayId);
        queryBorrowing.eq(FundReceiptRepayBorrowing::getCashFlowCode, cashFlowCode);
        FundReceiptRepayBorrowing fundReceiptRepayBorrowing = this.getOne(queryBorrowing);
        if (ObjectUtil.isEmpty(fundReceiptRepayBorrowing)) {
            return;
        }
        long hasVerifyAmountBorrowing = SpringContextHolder.getBean(FundReceiptFlowDetailService.class).sum(fundReceiptRepayBorrowing.getReceiptRepayId(), fundReceiptRepayBorrowing.getCashFlowCode());
        long remainingAmountBorrowing = fundReceiptRepayBorrowing.getPrincipal() - hasVerifyAmountBorrowing;
        if (hasVerifyAmountBorrowing <= 0 ) {
            fundReceiptRepayBorrowing.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
        } else if (remainingAmountBorrowing < 0) {
            fundReceiptRepayBorrowing.setWriteOffState(CashFlowState.BEYOND_WRITTEN_OFF.name());
        } else if (remainingAmountBorrowing != 0) {
            fundReceiptRepayBorrowing.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayBorrowing.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        this.updateById(fundReceiptRepayBorrowing);
    }
}