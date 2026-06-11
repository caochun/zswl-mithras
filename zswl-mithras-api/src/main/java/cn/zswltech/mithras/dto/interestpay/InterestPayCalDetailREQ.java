package cn.zswltech.mithras.dto.interestpay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("提交入参")
public class InterestPayCalDetailREQ extends PageReq {

    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资类型")
    private String type;

    @ApiModelProperty(value = "起始月份 yyyy-MM")
    private String startYearAndMonth;

    @ApiModelProperty(value = "结束月份 yyyy-MM")
    private String endYearAndMonth;
}
