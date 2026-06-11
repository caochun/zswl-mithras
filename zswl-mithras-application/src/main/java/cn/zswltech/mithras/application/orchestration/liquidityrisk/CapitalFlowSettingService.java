package cn.zswltech.mithras.application.orchestration.liquidityrisk;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.fund.application.financing.dto.FundFinancingRepayActualDTO;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingRepayActualLibMapper;
import cn.zswltech.mithras.liquidity.mapper.risk.BaseAmountSettingMapper;
import cn.zswltech.mithras.liquidity.mapper.risk.FinancingDeliverDetailSettingMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayActualLib;
import cn.zswltech.mithras.liquidity.application.liquidityrisk.CapitalInflowService;
import cn.zswltech.mithras.liquidity.model.risk.BaseAmountSetting;
import cn.zswltech.mithras.liquidity.model.risk.FinancingDeliverDetailSetting;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @create: 2023-05-17
 **/

@Slf4j
@Service
public class CapitalFlowSettingService extends ServiceImpl<FinancingDeliverDetailSettingMapper,FinancingDeliverDetailSetting> {
    @Resource
    private BaseAmountSettingMapper baseAmountSettingMapper;
    @Resource
    private FundFinancingRepayActualLibMapper fundFinancingRepayActualLibMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private CapitalInflowService capitalInflowService;

    @Transactional(rollbackFor = Exception.class)
    public void setting(CapitalFlowSettingReq req){
        BaseAmountSetting baseAmountSetting = new BaseAmountSetting();
        baseAmountSetting.setBeginCashflowAmount(req.getBeginCashflowAmount());
        baseAmountSetting.setOtherIncome(req.getOtherIncome());
        baseAmountSetting.setOtherExpenses(req.getOtherExpenses());
        baseAmountSetting.setId(1L);
        baseAmountSettingMapper.updateById(baseAmountSetting);
        remove(Wrappers.<FinancingDeliverDetailSetting>lambdaUpdate());
        List<FinancingDeliverDetailSetting> adds = new ArrayList<>();
        req2add(adds,req.getInDetail(),0);
        req2add(adds,req.getOutDetail(),1);
        if (CollUtil.isNotEmpty(adds)) {
            saveBatch(adds);
        }
    }

    private void req2add(List<FinancingDeliverDetailSetting> adds,List<CapitalFlowSettingReq.Detail> details,Integer type){
        if (CollUtil.isNotEmpty(details)) {
            for (CapitalFlowSettingReq.Detail detail : details) {
                FinancingDeliverDetailSetting financingDeliverDetailSetting = new FinancingDeliverDetailSetting();
                financingDeliverDetailSetting.setAmount(detail.getAmount());
                financingDeliverDetailSetting.setDate(detail.getDate());
                financingDeliverDetailSetting.setType(type);
                financingDeliverDetailSetting.setRemark(detail.getRemark());
                adds.add(financingDeliverDetailSetting);
            }
        }
    }

    public CapitalFlowSettingRsp detail(CapitalFlowSettingDetailReq req){
        CapitalFlowSettingRsp rsp = new CapitalFlowSettingRsp();
        BaseAmountSetting baseAmountSetting = baseAmountSettingMapper.selectOne(Wrappers.<BaseAmountSetting>query().last("limit 1"));
        if (baseAmountSetting == null){
            baseAmountSetting = new BaseAmountSetting();
        }
        rsp.setBeginCashflowAmount(LongUtil.null2zero(baseAmountSetting.getBeginCashflowAmount()));
        rsp.setOtherIncome(LongUtil.null2zero(baseAmountSetting.getOtherIncome()));
        rsp.setOtherExpenses(LongUtil.null2zero(baseAmountSetting.getOtherExpenses()));
        List<FinancingDeliverDetailSetting> detailSettings = baseMapper.selectList(Wrappers.<FinancingDeliverDetailSetting>lambdaQuery());
        List<CapitalFlowSettingRsp.Detail> indetail = new ArrayList<>();
        List<CapitalFlowSettingRsp.Detail> outdetail = new ArrayList<>();
        for (FinancingDeliverDetailSetting detailSetting : detailSettings) {
            CapitalFlowSettingRsp.Detail detail = new CapitalFlowSettingRsp.Detail();
            detail.setAmount(LongUtil.null2zero(detailSetting.getAmount()));
            detail.setDate(detailSetting.getDate());
            detail.setRemark(detailSetting.getRemark());
            if (detailSetting.getType() == 1){
                outdetail.add(detail);
            }else {
                indetail.add(detail);
            }
        }
        rsp.setInDetail(indetail);
        rsp.setOutDetail(outdetail);
        rsp.setTimeFrom(req.getTimeFrom().toLocalDate());
        rsp.setTimeTo(req.getTimeTo().toLocalDate());
        //初始值
        rsp.setReturnFinancingPrincipal(0L);
        rsp.setReturnFinancingInterest(0L);
        FundFinancingRepayActualDTO fundFinancingRepayActualDTO = new FundFinancingRepayActualDTO();
        fundFinancingRepayActualDTO.setFromTime(req.getTimeFrom());
        fundFinancingRepayActualDTO.setToTime(req.getTimeTo());
        Page<FundFinancingRepayActualLib> pageall = fundFinancingRepayActualLibMapper.stockPageList(new Page<>(1, Integer.MAX_VALUE), fundFinancingRepayActualDTO);
        List<FundFinancingRepayActualLib> fundList = pageall.getRecords();
        if (CollUtil.isNotEmpty(fundList)) {
            for (FundFinancingRepayActual repayActual : fundList) {
                rsp.setReturnFinancingPrincipal(rsp.getReturnFinancingPrincipal() + LongUtil.null2zero(repayActual.getPrincipleAmount()));
                rsp.setReturnFinancingInterest(rsp.getReturnFinancingInterest() + LongUtil.null2zero(repayActual.getInterestAmount()));
            }
        }
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .select(PaymentBaseInfo::getContractId,PaymentBaseInfo::getReceiptId)
                .in(PaymentBaseInfo::getWriteOffStatus, ListUtil.of(PaymentWriteOffStatus.WRITTEN_OFF.name(),PaymentWriteOffStatus.PART_WRITTEN_OFF.name())));
        List<Long> contractIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toList());
        List<Long> receiptIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getReceiptId).distinct().collect(Collectors.toList());
        ContractLastDateDTO dto = new ContractLastDateDTO();
        dto.setContractIds(contractIds);
        dto.setReceiptIds(receiptIds);
        dto.setTimeFrom(req.getTimeFrom());
        dto.setTimeTo(req.getTimeTo());
        Page<ContractLastDate> page = contractBaseInfoMapper.ContractRentLastDate(new Page<>(1, Integer.MAX_VALUE), dto);
        List<ContractLastDate> records = page.getRecords();
        ContractPriceDetailREQ query = new ContractPriceDetailREQ();
        rsp.setProjEarnestMoney(0L);
        for (ContractLastDate record : records) {
            query.setContractId(record.getContractId());
            ContractPriceDetailRSP price = contractPriceService.detail(query);
            rsp.setProjEarnestMoney(LongUtil.null2zero(rsp.getProjEarnestMoney())+LongUtil.null2zero(price.getEarnestMoney()));
        }
        AssetInflowDetailREQ inreq = new AssetInflowDetailREQ();
        inreq.setEstimatedOverdueRate(req.getOverdueRate());
        inreq.setTimeFrom(req.getTimeFrom());
        inreq.setTimeTo(req.getTimeTo());
        inreq.setNeedPage(false);
        AssetInflowDetailRSP assetInflowDetailRSP = capitalInflowService.inFlowDetail(inreq);
        if (ObjectUtil.isNotEmpty(req.getOverdueRate())) {
            BigDecimal subtract = new BigDecimal(1).subtract(NumberUtil.div(String.valueOf(req.getOverdueRate()), "100"));
            rsp.setRentInterestReturn(subtract.multiply(new BigDecimal(String.valueOf(LongUtil.null2zero(assetInflowDetailRSP.getPageSum().getPrincipal()) + LongUtil.null2zero(assetInflowDetailRSP.getPageSum().getInterest())))).setScale(2, BigDecimal.ROUND_HALF_UP).longValue());
        }else {
            rsp.setRentInterestReturn(LongUtil.null2zero(assetInflowDetailRSP.getPageSum().getPrincipal()) + LongUtil.null2zero(assetInflowDetailRSP.getPageSum().getInterest()));
        }
        rsp.setEarnestMoneyRevenue(LongUtil.null2zero(assetInflowDetailRSP.getPageSum().getEstimatedCashInFlowTotal())-rsp.getRentInterestReturn());
        rsp.setFinancingSum(CollUtil.isNotEmpty(rsp.getInDetail())?rsp.getInDetail().stream().filter(o -> (o.getDate().equals(req.getTimeFrom().toLocalDate()) || o.getDate().isAfter(req.getTimeFrom().toLocalDate())) && (o.getDate().isBefore(req.getTimeTo().toLocalDate()) || o.getDate().equals(req.getTimeTo().toLocalDate()))).mapToLong(CapitalFlowSettingRsp.Detail::getAmount).sum():0);
        rsp.setProjOutSum(CollUtil.isNotEmpty(rsp.getOutDetail())?rsp.getOutDetail().stream().filter(o -> (o.getDate().equals(req.getTimeFrom().toLocalDate()) || o.getDate().isAfter(req.getTimeFrom().toLocalDate())) && (o.getDate().isBefore(req.getTimeTo().toLocalDate()) || o.getDate().equals(req.getTimeTo().toLocalDate()))).mapToLong(CapitalFlowSettingRsp.Detail::getAmount).sum():0);
        rsp.setSumAmountIn(LongUtil.null2zero(rsp.getRentInterestReturn())+LongUtil.null2zero(rsp.getEarnestMoneyRevenue())+LongUtil.null2zero(rsp.getFinancingSum()));
        rsp.setSumAmountOut(LongUtil.null2zero(rsp.getReturnFinancingPrincipal())+LongUtil.null2zero(rsp.getReturnFinancingInterest())+LongUtil.null2zero(rsp.getProjEarnestMoney())+LongUtil.null2zero(rsp.getProjOutSum()));
        rsp.setTotalFundingSurplus(LongUtil.null2zero(rsp.getSumAmountIn())-LongUtil.null2zero(rsp.getSumAmountOut()));
        rsp.setPeriodCashBalance(LongUtil.null2zero(baseAmountSetting.getBeginCashflowAmount())+LongUtil.null2zero(baseAmountSetting.getOtherIncome())- LongUtil.null2zero(baseAmountSetting.getOtherExpenses())+rsp.getTotalFundingSurplus());
        return rsp;
    }
}
