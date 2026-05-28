package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAccountLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractAccountBLHKLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@Service("contractAccountBLHK")
public class ContractAccountBLHKFactory implements EditdataCompareFactory {
    @Resource
    private ContractAccountLibMapper libMapper;
    @Resource
    private ContractAccountBLHKLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractAccount, ContractAccountLib, ContractAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.CONTRACT.name(), version);
    }
}
