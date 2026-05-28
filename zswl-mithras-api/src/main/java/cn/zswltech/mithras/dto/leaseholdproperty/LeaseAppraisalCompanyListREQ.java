package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalCompanyListREQ extends PageReq {

    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

}
