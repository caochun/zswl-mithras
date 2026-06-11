package cn.zswltech.mithras.contract.versioning.service;

import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorListRSP;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractGuarantorLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractGuarantorLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;


/**
* @description 合同-担保措施
* @author vico
* @date 2022-08-22
*/
@Service
public class ContractGuarantorLibService extends ServiceImpl<ContractGuarantorLibMapper, ContractGuarantorLib> {

    @Resource
    private ContractGuarantorLibHandler guarantorLibHandler;

    public List<ContractGuarantorLib> listByVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractGuarantorLib> query = Wrappers.lambdaQuery();
        query.eq(ContractGuarantor::getContractId, contractId);
        query.eq(ContractGuarantorLib::getVersion, version);
        return this.list(query);
    }

    public List<ContractGuarantorListRSP> list(ContractIdListREQ req) {
        List<ContractGuarantorLib> dataList = baseMapper.selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                .eq(ContractGuarantorLib::getContractId, req.getContractId())
                .eq(ContractGuarantorLib::getVersion, req.getVersion())
        );
        return guarantorLibHandler.actualLib2RspList(dataList);
    }

    public List<ContractGuarantorLib> newestList(Collection<Long> contractIds) {
        return baseMapper.newestList(contractIds);
    }
}