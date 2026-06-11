package cn.zswltech.mithras.contract.versioning.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAocPriceLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.model.contract.ContractAocPriceLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractAocPriceLibHandler;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Service
public class ContractAocPriceLibService extends ServiceImpl<ContractAocPriceLibMapper, ContractAocPriceLib> {

    @Resource
    ContractAocPriceLibHandler contractAocPriceLibHandler;

    public ContractAocPrice getByVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractAocPriceLib> query = Wrappers.lambdaQuery();
        query.eq(ContractAocPrice::getContractId, contractId);
        query.eq(ContractAocPriceLib::getVersion, version);
        query.orderByDesc(ContractAocPriceLib::getId);
        query.last(StringUtil.mysqlLimitOne());
        ContractAocPriceLib one = this.getOne(query);
        return ObjectUtil.isNull(one) ? null : contractAocPriceLibHandler.actualLib2Entity(one);
    }

    public List<ContractAocPriceLib> queryNewestLib(Set<Long> contractIds) {
        return baseMapper.queryNewestLib(contractIds);
    }
}
