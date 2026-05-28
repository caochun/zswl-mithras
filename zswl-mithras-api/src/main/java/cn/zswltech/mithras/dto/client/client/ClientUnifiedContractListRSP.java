package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ClientUnifiedContractListRSP {

    private Long id;

    @ApiModelProperty("项目名称")
    private String contractCode;

    @ApiModelProperty("授信金额")
    private Long applyCreditAmount;

    @ApiModelProperty("合同状态 ContractStatus")
    private String contractStatus;

    @ApiModelProperty("所属部门")
    private Long belongDeptId;

    @ApiModelProperty("所属部门名称")
    private String belongDeptName;

}
