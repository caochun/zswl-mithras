package cn.zswltech.mithras.service.service.lib.contract;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractLeasePriceLibHandler;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


/**
 * @author vico
 * @description 合同-租赁报价方案表
 * @date 2022-08-22
 */
@Service
public class ContractLeasePriceLibService extends ServiceImpl<ContractLeasePriceLibMapper, ContractLeasePriceLib> {

    @Autowired
    private ContractLeasePriceLibHandler leasePriceLibHandler;

    public ContractLeasePrice getLatestLib(Long contractId) {
        LambdaQueryWrapper<ContractLeasePriceLib> query = Wrappers.lambdaQuery();
        query.eq(ContractLeasePriceLib::getContractId, contractId);
        query.eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(ContractLeasePriceLib::getId);
        query.last(StringUtil.mysqlLimitOne());
        return leasePriceLibHandler.actualLib2Entity(this.getOne(query));
    }

    public ContractLeasePrice getByVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractLeasePriceLib> query = Wrappers.lambdaQuery();
        query.eq(ContractLeasePrice::getContractId, contractId);
        query.eq(ContractLeasePriceLib::getVersion, version);
        query.orderByDesc(ContractLeasePriceLib::getId);
        query.last(StringUtil.mysqlLimitOne());
        ContractLeasePriceLib one = this.getOne(query);
        return ObjectUtil.isNull(one) ? null : leasePriceLibHandler.actualLib2Entity(one);
    }

    public List<ContractLeasePriceLib> queryNewestLib(Set<Long> contractIds) {
        return baseMapper.queryNewestLib(contractIds);
    }
}