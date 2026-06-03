package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualListRSP;
import cn.zswltech.mithras.service.convert.contract.ContractRentConvert;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.contract.versioning.application.ContractRentActualLibService;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName ContractRentEstimateLibHandle
 * @Description
 * @Author jackerhe
 * @Date 2022/8/23 7:26 下午
 * @Version 1.0
 **/
@Service
public class ContractRentReceiptLibHandle
        extends ContractLibAbstractHandler<ContractReceiptLib, ContractReceipt, ContractRentActualListRSP> {

    @Lazy
    @Autowired
    private ContractRentActualLibService contractRentActualLibService;

    @Override
    protected ContractReceiptLib entity2Lib(ContractReceipt f) {

        return BeanUtil.copyProperties(f, ContractReceiptLib.class);
    }

    @Override
    protected ContractReceipt lib2Entity(ContractReceiptLib t) {
        return BeanUtil.copyProperties(t, ContractReceipt.class);
    }

    @Override
    protected ContractRentActualListRSP lib2Rsp(ContractReceiptLib f) {
        ContractRentActualListRSP rsp = new ContractRentActualListRSP();
        List<ContractRentActualLib> contractRentActuals = contractRentActualLibService.list(Wrappers.<ContractRentActualLib>lambdaQuery()
                .eq(ContractRentActualLib::getReceiptId, f.getOriginId())
                .eq(ContractRentActualLib::getVersion, f.getVersion())
                .orderByAsc(ContractRentActual::getCashFlowPhase));
        rsp.setContractId(f.getContractId());
        rsp.setReceiptCode(f.getReceiptCode());
        if (ObjectUtil.isNotEmpty(contractRentActuals)) {
            rsp.setRentActualList(contractRentActuals.stream().map(ContractRentConvert::toContractRentActualListTableData).collect(Collectors.toList()));
        }
        rsp.setReceiptId(f.getId());
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.ACTUAL_ESTIMATE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("receiptId");
        fields.add("rentActualList");
        return fields;
    }
}
