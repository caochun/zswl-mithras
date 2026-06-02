package cn.zswltech.mithras.service.service.lib.contract;

import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractMortgageLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgageLib;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractMortgageLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
* @description 合同-抵押措施
* @author vico
* @date 2022-08-22
*/
@Service
public class ContractMortgageLibService extends ServiceImpl<ContractMortgageLibMapper, ContractMortgageLib> {

    @Resource
    private ContractMortgageLibHandler mortgageLibHandler;

    public List<ContractMortgageLib> listByVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractMortgageLib> query = Wrappers.lambdaQuery();
        query.eq(ContractMortgage::getContractId, contractId);
        query.eq(ContractMortgageLib::getVersion, version);
        return this.list(query);
    }

    public List<ContractMortgageListRSP> list(ContractIdListREQ req) {
        List<ContractMortgageLib> dataList = baseMapper.selectList(Wrappers.<ContractMortgageLib>lambdaQuery()
                .eq(ContractMortgageLib::getContractId, req.getContractId())
                .eq(ContractMortgageLib::getVersion, req.getVersion())
        );
        return mortgageLibHandler.actualLib2RspList(dataList);
    }
}