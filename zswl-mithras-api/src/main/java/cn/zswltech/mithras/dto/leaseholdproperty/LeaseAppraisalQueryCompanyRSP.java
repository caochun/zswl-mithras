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
public class LeaseAppraisalQueryCompanyRSP {

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

}


