package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVehicleRegistrationCountRSP {

    @ApiModelProperty(value = "本次识别合计")
    private Integer total;

    @ApiModelProperty(value = "识别成功张数")
    private Integer succeed;


    @ApiModelProperty(value = "识别失败张数")
    private Integer fail;

    @ApiModelProperty(value = "支持对内容进行锁定")
    private Boolean canLock;
}
