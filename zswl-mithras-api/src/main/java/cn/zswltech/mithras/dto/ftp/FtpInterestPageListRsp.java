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
@ApiModel("FTP计息-分页列表-返回参数")
public class FtpInterestPageListRsp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("累计计息")
    private Long totalInterestAmount;

    @ApiModelProperty("更新日期")
    private String lastUpdateDate;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty("最近一次的现金FTP")
    private Integer lastCashFtp;
}
