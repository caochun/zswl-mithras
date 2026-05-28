package cn.zswltech.mithras.dto.trackEvent;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class TrackEventUpdateREQ extends TrackEventAddREQ{

    @ApiModelProperty(value = "跟踪任务id")
    private Long id;
}


