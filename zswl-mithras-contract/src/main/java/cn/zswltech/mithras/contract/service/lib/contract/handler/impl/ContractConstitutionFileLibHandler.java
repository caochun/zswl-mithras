package cn.zswltech.mithras.contract.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractConstitutionFile;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractConstitutionFileLib;
import cn.zswltech.mithras.contract.service.lib.contract.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author yupengfei
 * @date 2024/4/25 23:40
 */
@Service
public class ContractConstitutionFileLibHandler
        extends ContractLibAbstractHandler<ContractConstitutionFileLib, ContractConstitutionFile, ListBaseRSP> {
    @Override
    protected ContractConstitutionFileLib entity2Lib(ContractConstitutionFile f) {
        return BeanUtil.copyProperties(f, ContractConstitutionFileLib.class);
    }

    @Override
    protected ContractConstitutionFile lib2Entity(ContractConstitutionFileLib t) {
        return BeanUtil.copyProperties(t, ContractConstitutionFile.class);
    }

    @Override
    protected ListBaseRSP lib2Rsp(ContractConstitutionFileLib f) {
        //todo 此表有些特殊，暂不处理
        return new ListBaseRSP();
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        //todo 待定
        return null;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
