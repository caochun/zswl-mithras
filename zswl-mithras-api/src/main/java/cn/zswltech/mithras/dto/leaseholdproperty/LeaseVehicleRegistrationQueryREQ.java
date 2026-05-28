package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVehicleRegistrationQueryREQ extends PageReq {

    @ApiModelProperty(value = "id")
    private List<Long> vehicleIds;

    @ApiModelProperty(value = "租赁物id")
    private Long leaseItemInfoId;

    @ApiModelProperty(value = "机动车所有人")
    private String vehicleRegistrationOwner;

    @ApiModelProperty(value = "机动车登记编号")
    private String vehicleRegistrationNumber;

    @ApiModelProperty(value = "机动车登记证书编号")
    private String registrationPageNo;

    @ApiModelProperty(value = "制造厂名称")
    private String vehicleManufacturer;

    @ApiModelProperty(value = "车辆识别代号/车架号")
    private String vehicleVin;

    @ApiModelProperty(value = "车辆识别状态")
    private String status;

    @ApiModelProperty(value = "图片张数")
    private Integer pictureCount;
}
