package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 1年期SHIBOR利率
 * @date 2023-05-21
 */
@Data
@ApiModel("1年期SHIBOR利率列表-返回体")
public class NewFtpShiborInterestRateListRSP {

    @ApiModelProperty(value = "id")
    private Long id;
    
    @ApiModelProperty(value = "日期")
    private LocalDate date;

    @ApiModelProperty(value = "SHIBOR利率值")
    private Integer value;

}
