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
public class LeaseAppraisalCompanyListRSP {

    @ApiModelProperty(value = "评估机构id")
    private Long companyId;

    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

    @ApiModelProperty(value = "是否白名单准入")
    private Integer isWhitelist;

}


