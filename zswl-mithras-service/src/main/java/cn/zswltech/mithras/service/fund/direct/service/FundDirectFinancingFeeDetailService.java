package cn.zswltech.mithras.service.fund.direct.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.fund.DirectFinancingType;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.fund.direct.converter.FundDirectFinancingFeeDetailConverter;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingFeeDetail;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingFeeDetailMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayExpenseService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingFeeDetailService
        extends ServiceImpl<FundDirectFinancingFeeDetailMapper, FundDirectFinancingFeeDetail> {
    @Resource
    private FundDirectFinancingFeeDetailConverter baseConverter;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundReceiptRepayExpenseService receiptRepayExpenseService;


    @Transactional(rollbackFor = Throwable.class)
    public void add(FundDirectFinancingFeeDetailAddREQ req) {
        FundDirectFinancingFeeDetail info = baseConverter.addReq2Entity(req);
        baseMapper.insert(info);
        fundDirectFinancingBaseInfoService.updateFinancingCost(req.getFinancingId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingFeeDetailModifyREQ req) {
        FundDirectFinancingFeeDetail originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptRepayExpense repayExpense = receiptRepayExpenseService.getOne(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                .eq(FundReceiptRepayExpense::getDirectFeeId, req.getId()));
        if (repayExpense != null && !Objects.equals(repayExpense.getWriteOffState(), CashFlowState.NO_WRITE_OFF.name())) {
            if (!Objects.equals(req.getAmount(), originalInfo.getAmount()) ||
                !Objects.equals(req.getIntermediaries(), originalInfo.getIntermediaries()) ||
                !Objects.equals(req.getExpenseType(), originalInfo.getExpenseType()) ||
                !Objects.equals(req.getPayDate(), originalInfo.getPayDate()) ||
                !Objects.equals(req.getInstitutionName(), originalInfo.getInstitutionName())) {
                throw new MithrasException("该费用项已经被核销,只允许更改支付方式、备注");
            }
        }
        FundDirectFinancingFeeDetail info = baseConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
        fundDirectFinancingBaseInfoService.updateFinancingCost(originalInfo.getFinancingId());
    }

    public FundDirectFinancingFeeDetailListRSP list(FundDirectFinancingFeeDetailListREQ req) {
        FundDirectFinancingFeeDetailListRSP rsp = new FundDirectFinancingFeeDetailListRSP();

        FundDirectFinancingBaseInfo baseInfo =
                fundDirectFinancingBaseInfoService.getById(req.getFinancingId());
        rsp.setFinancingAmount(baseInfo.getFinancingAmount());
        rsp.setAverageCouponRate(baseInfo.getAverageCouponRate());
        rsp.setFinancingMonth(baseInfo.getFinancingMonth());
        rsp.setRepayFrequency(baseInfo.getRepayFrequency());
        rsp.setRepayWay(baseInfo.getRepayWay());
        rsp.setCarryInterestTime(baseInfo.getCarryInterestTime());
        rsp.setCalculateDay(baseInfo.getCalculateDay());
        rsp.setComprehensiveFinancingCost(baseInfo.getComprehensiveFinancingCost());
        rsp.setFtpYieldRate(baseInfo.getFtpYieldRate());

        List<FundDirectFinancingFeeDetail> all = list(
                Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
                        .eq(FundDirectFinancingFeeDetail::getFinancingId, req.getFinancingId()));
        BigDecimal totalFee = all.stream().map(FundDirectFinancingFeeDetail::getAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        rsp.setTotalFee(totalFee.longValue());

        Page<FundDirectFinancingFeeDetail> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
                        .eq(FundDirectFinancingFeeDetail::getFinancingId, req.getFinancingId()));
        List<FundDirectFinancingFeeDetailRSP> rspList = baseConverter.entity2Rsp(page.getRecords());
        if(CollectionUtil.isNotEmpty(rspList)) {
            List<FundReceiptRepayExpense> repayExpenseList = receiptRepayExpenseService.list(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                    .in(FundReceiptRepayExpense::getDirectFeeId, rspList.stream().map(FundDirectFinancingFeeDetailRSP::getId).collect(Collectors.toList())));
            Map<Long, String> writeOffMap = Optional.ofNullable(repayExpenseList).map(m ->
                            m.stream().collect(Collectors.toMap(FundReceiptRepayExpense::getDirectFeeId, FundReceiptRepayExpense::getWriteOffState)))
                    .orElse(Collections.emptyMap());
            for (FundDirectFinancingFeeDetailRSP feeDetailRsp : rspList) {
                feeDetailRsp.setWriteOffStatus(writeOffMap.getOrDefault(feeDetailRsp.getId(), CashFlowState.NO_WRITE_OFF.name()));
            }
        }

        rsp.setPage(PageR.of(page, rspList));
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        FundDirectFinancingFeeDetail originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptRepayExpense repayExpense = receiptRepayExpenseService.getOne(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                .eq(FundReceiptRepayExpense::getDirectFeeId, id));
        if(repayExpense != null && !Objects.equals(repayExpense.getWriteOffState(), CashFlowState.NO_WRITE_OFF.name())){
            throw new MithrasException("该费用项已被核销,不允许删除");
        }
        baseMapper.deleteById(id);
        fundDirectFinancingBaseInfoService.updateFinancingCost(originalInfo.getFinancingId());
    }

    public void modifyProgramme(FundDirectFinancingProgrammeModifyREQ req) {
        FundDirectFinancingBaseInfo baseInfo = fundDirectFinancingBaseInfoService.getById(req.getFinancingId());
        if(Arrays.asList(DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name()).contains(baseInfo.getDirectFinancingType())) {
            if(req.getCalculateDay() == null){
                throw new MithrasException("项目类型为ABS或ABN时计算日为必填项");
            }
        }
        baseInfo.setFinancingAmount(req.getFinancingAmount());
        baseInfo.setCarryInterestTime(req.getCarryInterestTime());
        baseInfo.setFinancingMonth(req.getFinancingMonth());
        baseInfo.setRepayWay(req.getRepayWay());
        baseInfo.setRepayFrequency(req.getRepayFrequency());
        baseInfo.setCalculateDay(req.getCalculateDay());
//        if(req.getCarryInterestTime() != null && req.getFinancingMonth() != null) {
//            baseInfo.setDurationTime(baseInfo.getCarryInterestTime().plusMonths(req.getFinancingMonth()));
//        }
        fundDirectFinancingBaseInfoService.updateById(baseInfo);
    }

    public FundDirectFinancingFeeDetailRSP detail(Long id) {
        FundDirectFinancingFeeDetail feeDetail = getById(id);
        return baseConverter.entity2Rsp(feeDetail);
    }
}