package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.rent.ContractRentActualListRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractRentActualLibHandle;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractRentReceiptLibHandle;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-25
 **/
@Service("contractActualRent")
public class ContractActualRentFactory implements EditdataCompareFactory {
    @Resource
    private ContractReceiptLibMapper libMapper;
    @Resource
    private ContractRentReceiptLibHandle handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractReceipt, ContractReceiptLib, ContractRentActualListRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }

    @Service("contractActualRentChildren")
    public static class ContractActualRentChildrenFactory implements EditdataCompareFactory {

        @Resource
        private ContractRentActualLibMapper libMapper;
        @Resource
        private ContractRentActualLibHandle handler;
        @Resource
        private CommonVersionMapper commonVersionMapper;

        @Override
        public AbstractDataCompare createCompare(List rsps, String version) {
            return new DefaultDataCompare<ContractRentActual, ContractRentActualLib, ContractRentActualRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
        }
    }
}


