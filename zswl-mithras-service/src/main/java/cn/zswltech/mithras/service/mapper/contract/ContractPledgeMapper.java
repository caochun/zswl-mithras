package cn.zswltech.mithras.service.mapper.contract;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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