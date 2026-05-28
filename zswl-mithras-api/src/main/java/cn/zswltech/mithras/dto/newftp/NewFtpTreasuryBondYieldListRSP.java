package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 10年期国债收益率
 * @date 2023-05-21
 */
@Data
@ApiModel("10年期国债收益率列表-返回体")
public class NewFtpTreasuryBondYieldListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "日期")
    private LocalDate date;

    @ApiModelProperty(value = "收益率值")
    private Integer value;

}
