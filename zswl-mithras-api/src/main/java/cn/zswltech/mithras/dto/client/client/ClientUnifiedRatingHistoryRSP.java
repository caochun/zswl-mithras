package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


@Data
public class ClientUnifiedRatingHistoryRSP {

    @ApiModelProperty(value = "推翻时间")
    private LocalDate effectTime;

    @ApiModelProperty(value = "评级结果")
    private String score;

}
