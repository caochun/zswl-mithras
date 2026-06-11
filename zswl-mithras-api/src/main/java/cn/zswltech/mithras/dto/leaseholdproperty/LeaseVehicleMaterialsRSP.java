package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@Data
public class LeaseVehicleMaterialsRSP {

    @ApiModelProperty(value = "文件id")
    private Long fileId;


    @ApiModelProperty(value = "车证Id")
    private Long vehicleId;

    public LeaseVehicleMaterialsRSP(Long fileId, Long vehicleId) {
        this.fileId = fileId;
        this.vehicleId = vehicleId;
    }
}
