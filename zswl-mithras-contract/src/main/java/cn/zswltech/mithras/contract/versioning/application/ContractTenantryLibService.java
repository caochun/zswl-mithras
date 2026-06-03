package cn.zswltech.mithras.contract.versioning.application;


import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryListRSP;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractTenantryLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 合同-承租人表
* @author vico
* @date 2022-08-22
*/
@Service
public class ContractTenantryLibService extends ServiceImpl<ContractTenantryLibMapper, ContractTenantryLib> {

    @Resource
    private ContractTenantryLibHandler tenantryLibHandler;

    public List<ContractTenantryListRSP> list(ContractIdListREQ req) {
        List<ContractTenantryLib> dataList = baseMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, req.getContractId())
                .eq(ContractTenantryLib::getVersion, req.getVersion())
        );
        return tenantryLibHandler.actualLib2RspList(dataList);
    }

    public List<ContractTenantryLib> newestList() {
        return baseMapper.newestList();
    }

}