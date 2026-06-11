package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/29 15:42
 */
@Data
@ApiModel("FTP基础信息详情-返回体")
public class NewFtpBaseInfoDetailRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流程状态")
    private String ftpProcessStatus;

    @ApiModelProperty(value = "月份")
    private LocalDate month;

    /**
     * {@link cn.zswltech.mithras.ftp.newftp.enums.PricingFrequencyEnum}
     */
    @ApiModelProperty(value = "定价频率")
    private String pricingFrequency;

    /**
     * FTP计价指导计算标志, 默认0, 0: 未计算过 1: 计算过
     */
    @ApiModelProperty(value = "FTP报价计算标志, 默认0, 0: 未计算过 1: 计算过")
    private Integer calculateDeductionFlag;

    /**
     * FTP报价计算标志, 默认0, 0: 未计算过 1: 计算过
     */
    @ApiModelProperty(value = "FTP计价指导计算标志, 默认0, 0: 未计算过 1: 计算过")
    private Integer calculateGuidanceFlag;

    @ApiModelProperty(value = "业务版本")
    private String ftpBusinessVersion;

}
