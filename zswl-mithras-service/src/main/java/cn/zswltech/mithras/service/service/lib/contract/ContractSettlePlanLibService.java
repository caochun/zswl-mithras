package cn.zswltech.mithras.service.service.lib.contract;

import cn.zswltech.mithras.service.mapper.lib.contract.ContractSettlePlanLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractSettlePlan;
import cn.zswltech.mithras.service.mapper.model.contract.ContractSettlePlanLib;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Service
public class ContractSettlePlanLibService extends ServiceImpl<ContractSettlePlanLibMapper, ContractSettlePlanLib> {
    public ContractSettlePlanLib getBy(Long contractId, String version) {
        LambdaQueryWrapper<ContractSettlePlanLib> query = Wrappers.lambdaQuery();
        query.eq(ContractSettlePlan::getContractId, contractId);
        query.eq(ContractSettlePlanLib::getVersion, version);
        query.orderByDesc(ContractSettlePlan::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }
}
