package cn.zswltech.mithras.service.service.assetclassify;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientWithdrawalRatioListReq;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientWithdrawalRatioListRsp;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientWithdrawalRatioModifyReq;
import cn.zswltech.mithras.dto.assetclassify.WithdrawalRatioWrapper;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClientLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 19:00
 */
@Service
public class AssetClassifyClientWithdrawalRatioService {

    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private AssetClassifyClientLibService assetClassifyClientLibService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private PaymentService paymentService;

    public List<AssetClassifyClientWithdrawalRatioListRsp> listRatios(AssetClassifyClientWithdrawalRatioListReq req) {
        AssetClassifyClient classifyClient;
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            if (ObjectUtil.equals(req.getIsAuxiliary(), Boolean.TRUE)) {
                classifyClient = assetClassifyClientAuxiliaryLibService.getOne(Wrappers.<AssetClassifyClientAuxiliaryLib>lambdaQuery()
                        .eq(AssetClassifyClientAuxiliaryLib::getOriginId, req.getId())
                        .eq(AssetClassifyClientAuxiliaryLib::getVersion, req.getVersion()));
            } else {
                classifyClient = assetClassifyClientLibService.getOne(Wrappers.<AssetClassifyClientLib>lambdaQuery()
                        .eq(AssetClassifyClientLib::getOriginId, req.getId())
                        .eq(AssetClassifyClientLib::getVersion, req.getVersion()));
            }
        } else {
            classifyClient = assetClassifyClientService.getById(req.getId());
        }
        if (ObjectUtil.isEmpty(classifyClient)) {
            throw new MithrasException("未查询到指定的分类客户或版本数据！");
        }
        String provisions = classifyClient.getProvisions();
        if (ObjectUtil.isEmpty(provisions)) {
            return Lists.emptyList();
        }
        List<WithdrawalRatioWrapper> wrappers = JSON.parseArray(provisions, WithdrawalRatioWrapper.class);
        Set<Long> contractIds = wrappers.stream()
                .map(WithdrawalRatioWrapper::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractMap = contractBaseInfoService
                .listByIds(contractIds).stream()
                .collect(Collectors.toMap(ContractBaseInfo::getId, v -> v));

        Set<Long> receiptIds = wrappers.stream()
                .map(WithdrawalRatioWrapper::getReceiptId).collect(Collectors.toSet());
        Map<Long, ContractReceipt> receipts = contractReceiptService.listByIds(receiptIds).stream()
                .collect(Collectors.toMap(ContractReceipt::getId, v -> v));

        List<AssetClassifyClientWithdrawalRatioListRsp> rspList = new ArrayList<>();
        for (WithdrawalRatioWrapper wrapper : wrappers) {
            AssetClassifyClientWithdrawalRatioListRsp rsp = BeanUtil.copyProperties(wrapper, AssetClassifyClientWithdrawalRatioListRsp.class);
            ContractBaseInfo contract = contractMap.getOrDefault(wrapper.getContractId(), new ContractBaseInfo());
            rsp.setBizType(contract.getBizType());
            rsp.setContractCode(contract.getContractCode());
            int remainingParse = contractRentActualService.count(Wrappers.<ContractRentActual>lambdaQuery()
                    .eq(ContractRentActual::getContractId, wrapper.getContractId())
                    .gt(ContractRentActual::getCashFlowDate, LocalDate.now()));
            rsp.setRemainingPhase(remainingParse);
            ContractReceipt receipt = receipts.getOrDefault(wrapper.getReceiptId(), new ContractReceipt());
            // 投放额
            Set<Long> paymentIds = paymentBaseInfoService
                    .list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                            .eq(PaymentBaseInfo::getReceiptIdFinal, receipt.getId())).stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            if (CollectionUtil.isEmpty(paymentIds)) {
                rsp.setDeliveryAmount(0L);
            } else {
                BigDecimal totalDelivery = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getPaymentId, paymentIds)
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()))
                        .stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero)
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                rsp.setDeliveryAmount(totalDelivery.longValue());
            }
            // 获取借据下的风险敞口
            Map<Long, Long> receiptStockExposureMap = paymentService.getStockRiskExposureByReceiptIds(new ArrayList<>(receiptIds));
            rsp.setStockExposure(receiptStockExposureMap.get(wrapper.getReceiptId()));
            rspList.add(rsp);
        }
        return rspList;
    }

    public void modifyRatioBatch(AssetClassifyClientWithdrawalRatioModifyReq req) {
        AssetClassifyClient classifyClient = assetClassifyClientService.getById(req.getId());
        if (ObjectUtil.isEmpty(classifyClient)) {
            throw new MithrasException("记录不存在！");
        }
        classifyClient.setProvisions(JSON.toJSONString(req.getWithdrawalRatios()));
        assetClassifyClientService.updateById(classifyClient);
    }
}
