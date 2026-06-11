package cn.zswltech.mithras.contract.versioning.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractFactoringPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPriceLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractFactoringPriceLibHandler;
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
public class ContractFactoringPriceLibService extends ServiceImpl<ContractFactoringPriceLibMapper, ContractFactoringPriceLib> {

    @Resource
    private ContractFactoringPriceLibHandler contractFactoringPriceLibHandler;

    public ContractFactoringPrice getByVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractFactoringPriceLib> query = Wrappers.lambdaQuery();
        query.eq(ContractFactoringPriceLib::getContractId, contractId);
        query.eq(ContractFactoringPriceLib::getVersion, version);
        query.orderByDesc(ContractFactoringPriceLib::getId);
        query.last(StringUtil.mysqlLimitOne());
        ContractFactoringPriceLib one = this.getOne(query);
        return ObjectUtil.isNull(one) ? null : contractFactoringPriceLibHandler.actualLib2Entity(one);
    }

    public List<ContractFactoringPriceLib> queryNewestLib(Set<Long> contractIds) {
        return baseMapper.queryNewestLib(contractIds);
    }
}
