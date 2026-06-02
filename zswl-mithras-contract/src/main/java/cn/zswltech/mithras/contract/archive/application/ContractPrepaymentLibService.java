package cn.zswltech.mithras.contract.archive.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractPrepaymentLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrepayment;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrepaymentLib;
import cn.zswltech.mithras.contract.archive.handler.impl.ContractPrepaymentLibHandler;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName ContractPrepaymentLibService
 * @Description
 * @Author jackerhe
 * @Date 2022/10/21 2:35 下午
 * @Version 1.0
 **/
@Service
public class ContractPrepaymentLibService extends ServiceImpl<ContractPrepaymentLibMapper, ContractPrepaymentLib> {

    @Resource
    private ContractPrepaymentLibHandler libHandler;

    public ContractPrepayment getByVersion(Long contractId, String version) {
        LambdaQueryWrapper<ContractPrepaymentLib> query = Wrappers.lambdaQuery();
        query.eq(ContractPrepaymentLib::getContractId, contractId);
        query.eq(ContractPrepaymentLib::getVersion, version);
        query.orderByDesc(ContractPrepaymentLib::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        ContractPrepaymentLib one = this.getOne(query);
        return ObjectUtil.isNull(one) ? null : libHandler.actualLib2Entity(one);
    }
}
