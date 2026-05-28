package cn.zswltech.mithras.dto.ftp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2023/5/26
 * @description
 */
@Data
@ApiModel("FTP计息-重算-请求参数")
public class FtpInterestRecalculateReq {

    /**
     * 全量重跑的时候，这个ID不传
     */
    @ApiModelProperty("FTP计息id")
    private Long ftpInterestId;

/*    @ApiModelProperty("资金FTP")
    private Integer cashFtp;

    @ApiModelProperty("票据FTP")
    private Integer billFtp;*/

    @ApiModelProperty("计息年月")
    @NotBlank(message = "计息年月")
    private String interestDate;
}
