package cn.zswltech.mithras.contract.mapper.lib.contract;

import cn.zswltech.mithras.contract.model.contract.ContractAocPriceLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
public interface ContractAocPriceLibMapper extends CustomBaseMapper<ContractAocPriceLib> {

    List<ContractAocPriceLib> queryNewestLib(@Param("contractIds") Set<Long> contractIds);
}
