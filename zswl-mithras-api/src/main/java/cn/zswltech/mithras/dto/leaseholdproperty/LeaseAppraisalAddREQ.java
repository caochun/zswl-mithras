package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalAddREQ {

    @ApiModelProperty(value = "统一社会信用代码")
    @NotNull(message = "不得为空")
    private String creditCode;



}
