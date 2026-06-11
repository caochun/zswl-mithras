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
public class LeaseAppraisalDetailREQ {

    @ApiModelProperty(value = "评估机构id")
    private Long companyId;



}
