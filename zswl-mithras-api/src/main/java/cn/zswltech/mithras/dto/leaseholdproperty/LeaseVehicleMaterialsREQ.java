package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.InputStream;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@Data
public class LeaseVehicleMaterialsREQ {

    @ApiModelProperty(value = "车证id")
    private Long vehicleId;

    @ApiModelProperty(value = "文件流")
    private InputStream inputStream;

    @ApiModelProperty(value = "车证Id")
    private String fileName;

    public LeaseVehicleMaterialsREQ(Long belongId, InputStream inputStream, String fileName) {
        this.vehicleId = belongId;
        this.inputStream = inputStream;
        this.fileName = fileName;
    }
}
