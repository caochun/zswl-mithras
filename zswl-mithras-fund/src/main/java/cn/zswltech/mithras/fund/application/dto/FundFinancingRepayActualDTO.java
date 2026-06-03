package cn.zswltech.mithras.fund.application.dto;

import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingTimeLimitTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName FundFinancingRepayActualDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/5/16 4:16 下午
 * @Version 1.0
 **/
@Data
public class FundFinancingRepayActualDTO {

    private LocalDateTime fromTime;

    //结束时间
    @ApiModelProperty("结束时间")
    private LocalDateTime toTime;

    /**
     * 期限类型 {@link FundFinancingTimeLimitTypeEnum#name()}
     */
    private String timeLimitType;

}
