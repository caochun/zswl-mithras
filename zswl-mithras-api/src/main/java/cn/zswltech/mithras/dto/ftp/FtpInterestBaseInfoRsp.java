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
@ApiModel("FTP计息-基本信息-返回参数")
public class FtpInterestBaseInfoRsp {
    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;
}
