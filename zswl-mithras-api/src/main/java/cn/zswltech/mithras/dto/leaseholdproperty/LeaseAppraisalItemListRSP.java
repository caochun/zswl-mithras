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
public class LeaseAppraisalItemListRSP {

    @ApiModelProperty(value = "评估机构id")
    private Long companyId;

    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

    /**
     * LeaseAppraisalPurposeEnum
     */
    @ApiModelProperty(value = "用途")
    private String purpose;

    /**
     * LeaseAppraisalSelectEnum
     */
    @ApiModelProperty(value = "是否被选中")
    private String selectType;

    @ApiModelProperty(value = "是否白名单准入")
    private Integer isWhitelist;
}


