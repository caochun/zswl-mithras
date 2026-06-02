package cn.zswltech.mithras.contract.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.contract.file.ContractTextInfoRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextInfoLib;
import cn.zswltech.mithras.contract.service.lib.contract.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2024/7/29
 * @description
 */
@Component
public class ContractTextInfoLibHandler extends ContractLibAbstractHandler<ContractTextInfoLib, ContractTextInfo, ContractTextInfoRSP> {
    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.TEXT_INFO;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected ContractTextInfoLib entity2Lib(ContractTextInfo f) {
        return BeanUtil.copyProperties(f, ContractTextInfoLib.class);
    }

    @Override
    protected ContractTextInfo lib2Entity(ContractTextInfoLib t) {
        return BeanUtil.copyProperties(t, ContractTextInfo.class);
    }

    @Override
    protected ContractTextInfoRSP lib2Rsp(ContractTextInfoLib f) {
        ContractTextInfoRSP rsp = new ContractTextInfoRSP();
        rsp.setContractId(f.getContractId());
        if (StrUtil.isNotBlank(f.getTextType())) {
            rsp.setTextTypeList(CharSequenceUtil.split(f.getTextType(), ","));
        }
        return rsp;
    }
}
