package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@Data
@ApiModel("FTP计息-每日计息详情-返回参数")
public class FtpInterestDetailRsp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("日期（展示文本）")
    private String interestDateText;

    @ApiModelProperty("现金支出")
    private Long cashOut;

    @ApiModelProperty("现金收入")
    private Long cashIn;

    @ApiModelProperty("现金占用")
    private Long cashOccupy;

    @ApiModelProperty("现金FTP")
    private String cashFtp;

    @ApiModelProperty("现金FTP日利率")
    private String cashFtpDay;

    @ApiModelProperty("现金计息")
    private Long cashInterest;

    @ApiModelProperty("票据支出")
    private Long billOut;

    @ApiModelProperty("票据收入")
    private Long billIn;

    @ApiModelProperty("票据占用")
    private Long billOccupy;

    @ApiModelProperty("票据FTP")
    private String billFtp;

    @ApiModelProperty("票据FTP日利率")
    private String billFtpDay;

    @ApiModelProperty("票据计息")
    private Long billInterest;

    @ApiModelProperty("是否逾期")
    private Integer isOverdue;

    @ApiModelProperty("当年累计计息")
    private Long totalInterestThisYear;
}
