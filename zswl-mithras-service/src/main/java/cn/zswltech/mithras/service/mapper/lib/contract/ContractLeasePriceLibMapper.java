package cn.zswltech.mithras.service.mapper.lib.contract;

import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


/**
 * @author vico
 * @description 合同-租赁报价方案表
 * @date 2022-08-22
 */
@Repository
public interface ContractLeasePriceLibMapper extends CustomBaseMapper<ContractLeasePriceLib> {

    List<ContractLeasePriceLib> queryNewestLib(@Param("contractIds") Set<Long> contractIds);
}