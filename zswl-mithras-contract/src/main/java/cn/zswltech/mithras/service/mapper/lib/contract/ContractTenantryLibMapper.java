package cn.zswltech.mithras.service.mapper.lib.contract;

import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author vico
 * @description 合同-承租人表
 * @date 2022-08-22
 */
@Repository
public interface ContractTenantryLibMapper extends CustomBaseMapper<ContractTenantryLib> {

    @Select("SELECT p.* FROM contract_tenantry_lib p \n" +
            "INNER JOIN (SELECT main_id AS id, MAX(version) AS version FROM common_version t1 \n" +
            "WHERE t1.module = 'CONTRACT' AND t1.version_type = 1 \n" +
            "GROUP BY main_id) m ON p.contract_id = m.id AND p.version = m.version")
    List<ContractTenantryLib> newestList();

}