package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("lpr-返回体")
public class NewFtpDetailLprPricingListRSP extends ListBaseRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "日期")
    private String lprDate;

    @ApiModelProperty(value = "收益率值")
    private String oneYear;

    @ApiModelProperty(value = "收益率值")
    private String fiveYear;

}
