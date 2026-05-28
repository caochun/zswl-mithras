package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalRelationRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "评估机构id")
    private Long appraisalCompanyId;

    @ApiModelProperty(value = "评估机构")
    private String appraisalCompanyName;

    /**
     * LeaseAppraisalPurposeEnum
     */
    @ApiModelProperty(value = "用途")
    private String purpose;

    /**
     * LeaseAppraisalSelectEnum
     */
    @ApiModelProperty(value = "是否被选中")
    private String selected;
}


