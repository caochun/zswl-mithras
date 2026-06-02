package cn.zswltech.mithras.contract.mapper.model.contract;

/**
 * @description: 抽象的合同报价方案接口，用于获取合同报价方案的公共字段
 * @author: zhaozhengkang
 * @date: 2023/6/5 19:28
 */
public interface ContractPrice {

    Long getContractId();

    Integer getIrrPercent();

    Integer getLprPercent();

    Integer getLprAddPercent();

    Integer getCreditAmountLoop();

    Long getContractAmount();
}
