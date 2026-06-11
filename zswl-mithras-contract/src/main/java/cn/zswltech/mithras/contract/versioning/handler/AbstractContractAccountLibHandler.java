package cn.zswltech.mithras.contract.versioning.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.model.contract.ContractAccountLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/7
 * @description
 */
public abstract class AbstractContractAccountLibHandler extends ContractLibAbstractHandler<ContractAccountLib, ContractAccount, ContractAccountListRSP> {

    @Override
    protected ContractAccountLib entity2Lib(ContractAccount f) {
        return BeanUtil.copyProperties(f, ContractAccountLib.class);
    }

    @Override
    protected ContractAccount lib2Entity(ContractAccountLib t) {
        return BeanUtil.copyProperties(t, ContractAccount.class);
    }

    @Override
    protected ContractAccountListRSP lib2Rsp(ContractAccountLib f) {
        ContractAccountListRSP rsp = BeanUtil.copyProperties(f, ContractAccountListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    /**
     * 按照类型拿数据
     * @param mainId
     * @return
     */
    @Override
    public List<ContractAccount> listNeedHandleEntity(Long mainId) {
        List<ContractAccount> draftDataList = draftMapper.selectList(Wrappers.<ContractAccount>lambdaQuery()
                .eq(ContractAccount::getContractId, mainId)
                .eq(ContractAccount::getAccountUse, getAccountUseEnum().name())
        );
        return draftDataList;
    }

    /**
     * 按照类型拿数据
     * @param mainId
     * @return
     */
    @Override
    public List<ContractAccountLib> listNeedHandleLib(Long mainId, String version) {
        List<ContractAccountLib> versionList = mapper.selectList(Wrappers.<ContractAccountLib>lambdaQuery()
                .eq(ContractAccountLib::getVersion, version)
                .eq(ContractAccountLib::getContractId, mainId)
                .eq(ContractAccountLib::getAccountUse, getAccountUseEnum().name())
        );
        return versionList;
    }

    public abstract ContractAccountUseEnum getAccountUseEnum();


}
