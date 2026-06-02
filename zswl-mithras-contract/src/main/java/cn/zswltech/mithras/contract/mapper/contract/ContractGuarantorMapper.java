package cn.zswltech.mithras.contract.mapper.contract;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @description 合同-担保措施
* @author vico
* @date 2022-08-12
*/
@Repository
public interface ContractGuarantorMapper extends CustomBaseMapper<ContractGuarantor> {

     List<String> contractByClientList(@Param("clientIds") List<Long> clientIds, @Param("code") String code, @Param("contractId") Long contractId);


}