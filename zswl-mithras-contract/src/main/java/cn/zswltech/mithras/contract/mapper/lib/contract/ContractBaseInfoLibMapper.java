package cn.zswltech.mithras.contract.mapper.lib.contract;

import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author vico
 * @description 合同基本信息表
 * @date 2022-08-22
 */
@Repository
public interface ContractBaseInfoLibMapper extends CustomBaseMapper<ContractBaseInfoLib> {

    List<ContractBaseInfoLib> listNewestContractByPreviewIds(@Param("projReviewIds") Set<Long> projReviewIds);

    List<ContractBaseInfoLib> listNewestContractByContractIds(@Param("contractIds") Set<Long> contractIds);

    List<ContractBaseInfoLib> listNewestContractByClientIds(@Param("clientIds") Set<Long> clientIds);


}