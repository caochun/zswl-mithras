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
public class LeaseVehicleRegistrationUpdateREQ {

    @ApiModelProperty(value = "租赁物id")
    @NotNull(message = "租赁物id不能为空")
    private Long leaseholdId;

    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "车证首页ids")
    private List<Long> ids;

    @ApiModelProperty("是否变更")
    private Boolean isChange;

    @ApiModelProperty("文件id")
    private Long fileId;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("机动车登记证书编号")
    private String registrationPageNo;

    @ApiModelProperty(value = "机动车所有人")
    private String vehicleRegistrationOwner;

    @ApiModelProperty(value = "机动车登记编号")
    private String vehicleRegistrationNumber;

    @ApiModelProperty(value = "制造厂名称")
    private String vehicleManufacturer;

    @ApiModelProperty(value = "车辆识别代号/车架号")
    private String vehicleVin;

    @ApiModelProperty(value = "变更记录")
    private VehicleChangeRecordData changeRecordData;
}
