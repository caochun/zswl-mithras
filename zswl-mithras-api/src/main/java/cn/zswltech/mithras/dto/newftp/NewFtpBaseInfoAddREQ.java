package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description ftp主表
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp主表新增-请求体")
public class NewFtpBaseInfoAddREQ {

    @ApiModelProperty(value = "所属月份")
    @NotNull(message = "所属月份不能为空")
    private LocalDate month;

    /**
     * {@link cn.zswltech.mithras.ftp.newftp.enums.PricingFrequencyEnum}
     */
    @ApiModelProperty(value = "定价频率")
    @NotNull(message = "定价频率不能为空")
    private String pricingFrequency;
}
