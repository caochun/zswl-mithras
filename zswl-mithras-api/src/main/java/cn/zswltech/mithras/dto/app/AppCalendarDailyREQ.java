package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class AppCalendarDailyREQ {

    @ApiModelProperty("类型: 我的日历/团队日历")
    private String type;

    @ApiModelProperty("还款日期")
    private LocalDate collectionDate;

    @ApiModelProperty("客户名称")
    private String clientName;

}
