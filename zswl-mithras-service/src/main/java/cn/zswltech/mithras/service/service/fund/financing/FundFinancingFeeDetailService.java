package cn.zswltech.mithras.service.service.fund.financing;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailAddREQ;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailListREQ;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailModifyREQ;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailRSP;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.fund.financing.FundFinancingFeeDetailConverter;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingFeeDetailMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingFeeDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayExpenseService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 间融-费用明细
 * @date 2023-06-17
 */
@Service
public class FundFinancingFeeDetailService
        extends ServiceImpl<FundFinancingFeeDetailMapper, FundFinancingFeeDetail> {
    @Resource
    private FundFinancingFeeDetailConverter baseConverter;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundReceiptRepayExpenseService receiptRepayExpenseService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundFinancingFeeDetailAddREQ req) {
        FundFinancingFeeDetail info = baseConverter.addReq2Entity(req);
        baseMapper.insert(info);
        // 更新综合融资成本
        financingPlanService.updateFinancingCost(req.getFinancingId());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        SpringContextHolder.getBean(FundFinancingBaseInfoService.class).tryUpdateChangeOther(req.getFinancingId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundFinancingFeeDetailModifyREQ req) {
        FundFinancingFeeDetail originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptRepayExpense repayExpense = receiptRepayExpenseService.getOne(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                .eq(FundReceiptRepayExpense::getFeeId, req.getId()));
        if (repayExpense != null && !Objects.equals(repayExpense.getWriteOffState(), CashFlowState.NO_WRITE_OFF.name())) {
            if (!Objects.equals(req.getAmount(), originalInfo.getAmount()) ||
                !Objects.equals(req.getOrganizationId(), originalInfo.getOrganizationId()) ||
                !Objects.equals(req.getExpenseType(), originalInfo.getExpenseType()) ||
                !Objects.equals(req.getPayDate(), originalInfo.getPayDate()) ||
                !Objects.equals(req.getOrganizationName(), originalInfo.getOrganizationName())) {
                throw new MithrasException("该费用项已经被核销,只允许更改支付方式、备注");
            }
        }
        FundFinancingFeeDetail info = baseConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
        // 更新综合融资成本
        financingPlanService.updateFinancingCost(originalInfo.getFinancingId());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        SpringContextHolder.getBean(FundFinancingBaseInfoService.class).tryUpdateChangeOther(originalInfo.getFinancingId());
    }

    public PageR<FundFinancingFeeDetailRSP> list(FundFinancingFeeDetailListREQ req) {

        Page<FundFinancingFeeDetail> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundFinancingFeeDetail>lambdaQuery()
                        .eq(FundFinancingFeeDetail::getFinancingId, req.getFinancingId()));
        List<FundFinancingFeeDetailRSP> rspList = baseConverter.entity2Rsp(page.getRecords());
        if(CollectionUtil.isNotEmpty(rspList)) {
            List<FundReceiptRepayExpense> repayExpenseList = receiptRepayExpenseService.list(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                    .in(FundReceiptRepayExpense::getFeeId, rspList.stream().map(FundFinancingFeeDetailRSP::getId).collect(Collectors.toList())));
            Map<Long, String> writeOffMap = Optional.ofNullable(repayExpenseList).map(m ->
                            m.stream().collect(Collectors.toMap(FundReceiptRepayExpense::getFeeId, FundReceiptRepayExpense::getWriteOffState)))
                    .orElse(Collections.emptyMap());
            for (FundFinancingFeeDetailRSP feeDetailRsp : rspList) {
                feeDetailRsp.setWriteOffStatus(writeOffMap.getOrDefault(feeDetailRsp.getId(), CashFlowState.NO_WRITE_OFF.name()));
            }
        }
        return PageR.of(page, rspList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        FundFinancingFeeDetail originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptRepayExpense repayExpense = receiptRepayExpenseService.getOne(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                .eq(FundReceiptRepayExpense::getFeeId, id));
        if(repayExpense != null && !Objects.equals(repayExpense.getWriteOffState(), CashFlowState.NO_WRITE_OFF.name())){
            throw new MithrasException("该费用项已被核销,不允许删除");
        }
        baseMapper.deleteById(id);
        // 更新综合融资成本
        financingPlanService.updateFinancingCost(originalInfo.getFinancingId());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        SpringContextHolder.getBean(FundFinancingBaseInfoService.class).tryUpdateChangeOther(originalInfo.getFinancingId());
    }


    public FundFinancingFeeDetailRSP detail(Long id) {
        FundFinancingFeeDetail feeDetail = getById(id);
        return baseConverter.entity2Rsp(feeDetail);
    }
}