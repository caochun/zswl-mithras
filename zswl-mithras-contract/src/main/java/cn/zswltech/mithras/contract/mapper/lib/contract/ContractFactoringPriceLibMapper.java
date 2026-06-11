package cn.zswltech.mithras.contract.mapper.lib.contract;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPriceLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
public interface ContractFactoringPriceLibMapper extends CustomBaseMapper<ContractFactoringPriceLib> {
    List<ContractFactoringPriceLib> queryNewestLib(@Param("contractIds") Set<Long> contractIds);
}
