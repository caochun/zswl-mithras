package cn.zswltech.mithras.contract.versioning.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* @description 合同明细-借据
* @author vico
* @date 2022-08-22
*/
@Service
public class ContractReceiptLibService extends ServiceImpl<ContractReceiptLibMapper, ContractReceiptLib> {

    public List<ContractReceiptLib> listByContractIdVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractReceiptLib> query = Wrappers.lambdaQuery();
        query.eq(ContractReceipt::getContractId, contractId);
        query.eq(ContractReceiptLib::getVersion, version);
        return this.list(query);
    }

    public ContractReceiptLib getByOriginIdAndVersion(Long originId, String version) {
        LambdaQueryWrapper<ContractReceiptLib> query = Wrappers.lambdaQuery();
        query.eq(ContractReceiptLib::getOriginId, originId);
        query.eq(ContractReceiptLib::getVersion, version);
        query.orderByDesc(ContractReceipt::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public Map<Long, ContractReceiptLib> getMapByOriginIds(List<Long> originIds) {
        LambdaQueryWrapper<ContractReceiptLib> query = Wrappers.lambdaQuery();
        query.in(ContractReceiptLib::getOriginId, originIds);
        query.orderByAsc(ContractReceipt::getId);
        List<ContractReceiptLib> list = this.list(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, ContractReceiptLib> map = new HashMap<>(list.size());
        for (ContractReceiptLib contractReceiptLib : list) {
            map.put(contractReceiptLib.getOriginId(), contractReceiptLib);
        }
        return map;
    }

    public List<ContractReceipt> listByVersionIds(List<Long> originIds, String version) {
        LambdaQueryWrapper<ContractReceiptLib> query = Wrappers.lambdaQuery();
        query.eq(ContractReceiptLib::getVersion, version);
        query.in(ContractReceiptLib::getOriginId, originIds);
        query.orderByAsc(ContractReceiptLib::getId);
        return this.list(query).stream().map(this::lib2Entity).collect(Collectors.toList());
    }

    private ContractReceipt lib2Entity(ContractReceiptLib lib) {
        return BeanUtil.copyProperties(lib, ContractReceipt.class);
    }
}
