package cn.zswltech.mithras.contract.mapper.contract;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.contract.application.dto.ContractLeaseItemCountBO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Repository
public interface ContractLeaseItemMapper extends CustomBaseMapper<ContractLeaseItem> {
    @Select("select " +
            "sum(original_book_value) as originalBookValueTotal, " +
            "sum(original_book_net_value) as originalBookNetValueTotal, " +
            "sum(assessed_value) as assessedValueTotal, " +
            "sum(assessed_net_value) as assessedNetValueTotal " +
            "from contract_lease_item " +
            "where contract_id = #{contractId}"
    )
    ContractLeaseItemCountBO countByContractId(@Param("contractId") Long contractId);
}
