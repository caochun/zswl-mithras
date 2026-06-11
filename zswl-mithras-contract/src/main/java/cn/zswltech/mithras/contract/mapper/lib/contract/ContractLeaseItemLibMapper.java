package cn.zswltech.mithras.contract.mapper.lib.contract;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItemLib;
import cn.zswltech.mithras.contract.core.dto.ContractLeaseItemCountBO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


/**
* @description 合同明细-租赁物清单
* @author vico
* @date 2022-08-22
*/
@Repository
public interface ContractLeaseItemLibMapper extends CustomBaseMapper<ContractLeaseItemLib> {
    @Select("select " +
            "sum(original_book_value) as originalBookValueTotal, " +
            "sum(original_book_net_value) as originalBookNetValueTotal, " +
            "sum(assessed_value) as assessedValueTotal, " +
            "sum(assessed_net_value) as assessedNetValueTotal " +
            "from contract_lease_item_lib " +
            "where contract_id = #{contractId} and version = #{version}"
    )
    ContractLeaseItemCountBO countByContractIdVersion(@Param("contractId") Long contractId, @Param("version") String version);
}