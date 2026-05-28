package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVehicleRegistrationLockREQ {
    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private List<Long> vehicleIds;

    @ApiModelProperty(value = "租赁物id")
    private Long leaseItemInfoId;

}
