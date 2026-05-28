package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 */
@Data
public class FtpCalculateInterestREQ {

    @ApiModelProperty(value = "ftp计息ID")
    @NotNull(message = "ftp计息ID不能为空")
    private Integer ftpInterestId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计息开始日期")
    @NotBlank(message = "计息开始日期不能为空")
    private String interestStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计息结束日期")
    @NotBlank(message = "计息结束日期不能为空")
    private String interestEndDate;
}