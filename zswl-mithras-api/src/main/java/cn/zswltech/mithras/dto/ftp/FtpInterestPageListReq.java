package cn.zswltech.mithras.dto.ftp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("FTP计息-分页列表-请求参数")
public class FtpInterestPageListReq extends PageReq {
    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;
}
