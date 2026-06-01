package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

/**
 * @author dingqi
 * @date 2023/9/21
 * @description
 */
@Data
public class ContractLeaseItemCountBO {
    private Long originalBookValueTotal;

    private Long originalBookNetValueTotal;

    private Long assessedValueTotal;

    private Long assessedNetValueTotal;
}
