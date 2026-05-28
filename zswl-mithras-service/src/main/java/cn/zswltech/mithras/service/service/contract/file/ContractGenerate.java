package cn.zswltech.mithras.service.service.contract.file;

import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
public interface ContractGenerate {
    void generate(ContractBaseInfo contractBaseInfo) throws Exception;
}
