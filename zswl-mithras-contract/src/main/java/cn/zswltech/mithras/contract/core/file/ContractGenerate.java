package cn.zswltech.mithras.contract.core.file;

import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
public interface ContractGenerate {
    void generate(ContractBaseInfo contractBaseInfo) throws Exception;
}
