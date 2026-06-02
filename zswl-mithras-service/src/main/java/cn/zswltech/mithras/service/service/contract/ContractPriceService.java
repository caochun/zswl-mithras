package cn.zswltech.mithras.service.service.contract;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceModifyREQ;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrice;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @description 合同-租赁报价方案表
* @author vico
* @date 2022-08-12
*/
public interface ContractPriceService {

    void modify(ContractPriceModifyREQ req);

    ContractPriceDetailRSP detail(ContractPriceDetailREQ req);

    ContractPriceDetailRSP editionDetail(ContractPriceDetailREQ req);

    void add(BaseModel price);

    //合同使用金额
    Long sumApplyByContractId(List<Long> ids);

    Map<Long, ContractLeasePrice> listByContractIds(List<Long> contractIds);

    ContractPriceDetailRSP detailVersion(Long contractId, String version);

    void saveIrr(Long contractId, Integer irr);

    /**
     * 获取指定合同的最新报价
     *
     * @param contractIds 合同id
     * @return
     */
    List<ContractPrice> queryNewestPrice(Set<Long> contractIds);

    /**
     * 获取指定合同的最新IRR
     *
     * @param contractIds 合同id
     * @return contract_id : irr_percent
     */
    Map<Long, Integer> queryNewestIrr(Set<Long> contractIds);


    /**
     * 获取指定借据的最新IRR
     *
     * @param receiptIds 借据id
     * @return receipt_id : irr_percent
     */
    Map<Long, Integer> queryNewestReceiptIrr(Set<Long> receiptIds);

    /**
     * 获取指定合同的最新利率
     *
     * @param contractIds 合同id
     * @return contract_id : (contract_amount, lpr_percent + lpr_add_percent)
     */
    Map<Long, Pair<Long, Integer>> queryNewestRate(Set<Long> contractIds);

    /**
     * 获取指定合同的最新IRR
     *
     * @param contractIds 合同id
     * @return contract_id : (contract_amount, irr_percent)
     */
    Map<Long, Pair<Long, Integer>> queryNewestIrrRate(Set<Long> contractIds);

    /**
     * 获取指定合同的最新合同金额
     *
     * @param contractIds 合同id
     * @return contract_id : contract_amount
     */
    Map<Long, Long> queryNewestContractAmount(Set<Long> contractIds);

    /**
     * 项目评审下使用额度 （循环需再加上已还本金）
     **/
    Map<Long, Long> projUseApplyCreditAmount(List<Long> projReviewIds);
}