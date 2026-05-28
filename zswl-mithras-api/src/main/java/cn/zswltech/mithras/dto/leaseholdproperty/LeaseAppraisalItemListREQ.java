package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseAppraisalItemListREQ {

    @ApiModelProperty(value = "租赁物id")
    @NotNull(message = "不得为空")
    private Long leaseItemId;

}
