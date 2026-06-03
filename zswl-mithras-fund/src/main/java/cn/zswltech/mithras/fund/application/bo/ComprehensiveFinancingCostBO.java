package cn.zswltech.mithras.fund.application.bo;

import cn.zswltech.mithras.fund.domain.enums.OrganizationType;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


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
