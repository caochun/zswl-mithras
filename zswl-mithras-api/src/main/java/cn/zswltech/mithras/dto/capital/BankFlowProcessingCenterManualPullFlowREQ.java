package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class BankFlowProcessingCenterManualPullFlowREQ {

    @ApiModelProperty(value = "开始时间")
    LocalDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    LocalDateTime endTime;

}
