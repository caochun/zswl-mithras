package cn.zswltech.mithras.service.mapper.lib.contract;

import cn.zswltech.mithras.service.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author vico
 * @description 合同明细-借据
 * @date 2022-08-22
 */
@Repository
public interface ContractReceiptLibMapper extends CustomBaseMapper<ContractReceiptLib> {

    List<ContractReceiptLib> newsetContractReceiptLib(@Param("contractIds") Set<Long> contractIds);
}