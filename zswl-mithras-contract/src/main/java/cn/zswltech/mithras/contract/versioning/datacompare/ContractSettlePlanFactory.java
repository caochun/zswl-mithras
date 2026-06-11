package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-29
 **/
@Service("contractSettlePlan")
public class ContractSettlePlanFactory implements EditdataCompareFactory {
//    @Resource
//    private ContractSettlePlanLibMapper libMapper;
//    @Resource
//    private ContractSettlePlanLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
//        return new DefaultDataCompare<ContractSettlePlan, ContractSettlePlanLib, ContractAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT");
        return null;
    }
}