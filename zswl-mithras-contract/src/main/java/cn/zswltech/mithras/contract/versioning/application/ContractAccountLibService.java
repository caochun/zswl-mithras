package cn.zswltech.mithras.contract.versioning.application;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListREQ;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAccountLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccountLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.contract.versioning.handler.AbstractContractAccountLibHandler;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractAccountZZSKLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @description 合同-收款账户表
* @author vico
* @date 2022-08-22
*/
@Service
public class ContractAccountLibService extends ServiceImpl<ContractAccountLibMapper, ContractAccountLib> {

    @Resource
    private List<AbstractContractAccountLibHandler> contractAccountLibHandlerList;

    public List<ContractAccountLib> listBy(Long contractId, String version) {
        LambdaQueryWrapper<ContractAccountLib> query = Wrappers.lambdaQuery();
        query.eq(ContractAccountLib::getContractId, contractId);
        query.eq(ContractAccountLib::getVersion, version);
        return this.list(query);
    }

    public List<ContractAccountListRSP> list(ContractAccountListREQ req) {
        List<ContractAccountLib> dataList = baseMapper.selectList(Wrappers.<ContractAccountLib>lambdaQuery()
                .eq(ContractAccountLib::getContractId, req.getContractId())
                .eq(ContractAccountLib::getVersion, req.getVersion())
                .eq(ContractAccountLib::getAccountUse, req.getAccountUse())
        );
        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }
        AbstractContractAccountLibHandler targetHandler = contractAccountLibHandlerList.stream().filter(c -> c.getAccountUseEnum().name().equals(req.getAccountUse())).findFirst().orElse(null);
        if (Objects.isNull(targetHandler)) {
            throw new MithrasException("账号处理器注册失败，请联系管理员进行处理");
        }
        return dataList.stream().map(targetHandler::actualLib2Rsp).collect(Collectors.toList());
    }

}
