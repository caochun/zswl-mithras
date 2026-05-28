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
public class LeaseVehicleRegistrationRemoveREQ {

    @ApiModelProperty(value = "车证首页id")
    private List<Long> vehicleIds;

    @ApiModelProperty(value = "车证变更记录文件id")
    private List<Long> changeRecordIds;

    @ApiModelProperty("操作类型")
    private String operateType;

    @ApiModelProperty("租赁物id")
    @NotNull(message = "租赁物id不能为空")
    private Long leaseItemInfoId;
}
