package cn.zswltech.mithras.contract.mapper.contract;
import cn.zswltech.mithras.contract.model.contract.ContractPledge;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @description 合同-质押措施
* @author vico
* @date 2022-08-12
*/
@Repository
public interface ContractPledgeMapper extends CustomBaseMapper<ContractPledge> {
    List<String> contractByClientList(@Param("clientIds") List<Long> clientIds, @Param("code") String code, @Param("contractId") Long contractId);
}