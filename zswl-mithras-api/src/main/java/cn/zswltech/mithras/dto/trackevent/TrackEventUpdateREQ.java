package cn.zswltech.mithras.dto.trackevent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class TrackEventUpdateREQ extends TrackEventAddREQ{

    @ApiModelProperty(value = "跟踪任务id")
    private Long id;
}


