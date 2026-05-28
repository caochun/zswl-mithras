package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.DirectFinancingType;
import cn.zswltech.mithras.service.enums.fund.OrganizationType;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


/**
 * @author chenyifei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprehensiveFinancingCostBO {
    /**
     * 机构类型 {@link OrganizationType#name()}
     * 间融才有此类型
     */
    private String organizationType;

    /**
     * 融资类型 {@link FinancingTypeEnum#name()}
     */
    @NotNull
    private String financingType;

    /**
     * 融资id
     */
    @NotNull
    private Long financingId;


}
