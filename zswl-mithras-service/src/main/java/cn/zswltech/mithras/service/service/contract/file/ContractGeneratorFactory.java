package cn.zswltech.mithras.service.service.contract.file;

import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.file.generator.*;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.projestablish.FactoringType.*;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
public class ContractGeneratorFactory {
    public static ContractGenerate getInstance(ContractBaseInfo contractBaseInfo) {
        if (ProjectBizType.BL.name().equals(contractBaseInfo.getBizType())) {
            if (wzmbl.name().equals(contractBaseInfo.getFactoringType())) {
                //无追
                return getBean(BLWZContractGenerator.class);
            }
            if (equalsAny(contractBaseInfo.getFactoringType(), yzmbl.name(), yzabl.name())) {
                //有追
                return getBean(BLYZContractGenerator.class);
            }
            throw new MithrasException("暂未支持该保理类型的合同自动生成合同文本");
        } else if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            if (LeaseType.hui_zu.name().equals(contractBaseInfo.getLeaseType())) {
                return getBean(ZLHZContractGenerator.class);
            } else if (LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
                return getBean(ZLZZContractGenerator.class);
            } else {
                return getBean(ZLJYXContractGenerator.class);
            }
        } else {
            throw new MithrasException("暂未支持该类型的合同自动生成合同文本");
        }
    }
}
