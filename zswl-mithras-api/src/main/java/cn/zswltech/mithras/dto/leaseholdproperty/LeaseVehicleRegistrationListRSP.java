package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
public class LeaseVehicleRegistrationListRSP {

    @ApiModelProperty(value = "车证id")
    private Long id;

    @ApiModelProperty(value = "机动车登记证书编号")
    private String registrationPageNo;

    @ApiModelProperty(value = "机动车所有人")
    private String vehicleRegistrationOwner;

    @ApiModelProperty(value = "机动车登记编号")
    private String vehicleRegistrationNumber;

    @ApiModelProperty(value = "制造厂名称")
    private String vehicleManufacturer;

    @ApiModelProperty(value = "车辆识别代号/车架号")
    private String vehicleVin;

    @ApiModelProperty(value = "锁定内容不支持修改")
    private Boolean locked;

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "车证识别状态")
    private String status;

    @ApiModelProperty(value = "图片张数")
    private Integer pictureCount;

    @ApiModelProperty(value = "变更记录")
    private List<VehicleChangeRecordData> changeRecordRspList;
}
