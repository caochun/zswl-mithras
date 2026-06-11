package cn.zswltech.mithras.contract.core.dto;

import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/23
 * @description
 */
@Data
public class ContractPriceHelperBO {
    LocalDate firstDate;
    Long contractAmount;
    Long consultingFee;
    Long earnestMoney;
    Long downPayment;
    Long nominalPrice;
    LocalDate lastDate;
    Integer lastPhase;
    ProjectBizType projectBizType;
}
