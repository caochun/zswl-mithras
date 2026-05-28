package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 10年期国债收益率
 * @date 2023-05-21
 */
@Data
@ApiModel("10年期国债收益率列表-返回体")
public class NewFtpDetailTreasuryBondYieldListRSP extends ListBaseRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "收益率值")
    private Integer value;


}
