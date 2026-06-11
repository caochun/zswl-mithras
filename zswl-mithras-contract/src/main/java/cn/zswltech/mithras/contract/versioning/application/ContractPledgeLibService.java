package cn.zswltech.mithras.contract.versioning.application;

import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractPledgeLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractPledgeLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractPledgeLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
* @description 合同-质押措施
* @author vico
* @date 2022-08-22
*/
@Service
public class ContractPledgeLibService extends ServiceImpl<ContractPledgeLibMapper, ContractPledgeLib> {

    @Resource
    private ContractPledgeLibHandler pledgeLibHandler;

    public List<ContractPledgeListRSP> list(ContractIdListREQ req) {
        List<ContractPledgeLib> dataList = baseMapper.selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                .eq(ContractPledgeLib::getContractId, req.getContractId())
                .eq(ContractPledgeLib::getVersion, req.getVersion())
        );
        return pledgeLibHandler.actualLib2RspList(dataList);
    }
}