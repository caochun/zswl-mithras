package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.common.enums.ProjectBizType;
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
