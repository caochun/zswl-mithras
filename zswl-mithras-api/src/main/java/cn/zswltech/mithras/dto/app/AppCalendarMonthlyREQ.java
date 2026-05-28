package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class AppCalendarMonthlyREQ {


    @ApiModelProperty("还款月yyyy-mm")
    private String collectionMonth;

    @ApiModelProperty("类型: 我的日历/团队日历")
    private String type;

}
