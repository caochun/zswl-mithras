package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author yibin
 */
@Data
public class AppCalendarMonthlyRSP {



    @ApiModelProperty(value = "当天有多少笔未还款或者已还款")
    private Map<LocalDate, collectionObj> collectionMap;

    @Data
    public static class collectionObj {

        @ApiModelProperty(value = "还款数量")
        private Integer count;

        @ApiModelProperty(value = "已还/未还")
        private String type;

    }
}
