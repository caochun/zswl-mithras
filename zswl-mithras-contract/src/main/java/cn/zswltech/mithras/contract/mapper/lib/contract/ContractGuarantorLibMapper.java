package cn.zswltech.mithras.contract.mapper.lib.contract;

import cn.zswltech.mithras.contract.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * @author vico
 * @description 合同-担保措施
 * @date 2022-08-22
 */
@Repository
public interface ContractGuarantorLibMapper extends CustomBaseMapper<ContractGuarantorLib> {

    List<ContractGuarantorLib> newestList(@Param("contractIds") Collection<Long> contractIds);

}