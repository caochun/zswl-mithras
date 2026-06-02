package cn.zswltech.mithras.contract.service.lib.contract;

import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentEstimateLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimateLib;
import cn.zswltech.mithras.contract.service.lib.contract.handler.impl.ContractRentEstimateLibHandle;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @description 合同明细-概算租金
* @author vico
* @date 2022-08-22
*/
@Service
public class ContractRentEstimateLibService extends ServiceImpl<ContractRentEstimateLibMapper, ContractRentEstimateLib> {

    @Resource
    private ContractRentEstimateLibHandle libHandle;

    public List<ContractRentEstimate> getByVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractRentEstimateLib> query = Wrappers.lambdaQuery();
        query.eq(ContractRentEstimateLib::getContractId, contractId);
        query.eq(ContractRentEstimateLib::getVersion, version);
        query.orderByAsc(ContractRentEstimate::getCashFlowPhase);
        List<ContractRentEstimateLib> list = this.list(query);
        return list.stream().map(libHandle::actualLib2Entity).collect(Collectors.toList());
    }

}