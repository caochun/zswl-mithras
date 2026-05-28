package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


@Data
public class ClientUnifiedViewListRSP {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户状态 ClientStatus")
    private String clientStatus;

    private Long belongDeptId;

    @ApiModelProperty("所属部门")
    private String belongDeptName;

    @ApiModelProperty("法定代表人性别")
    private String corpGender;

    @ApiModelProperty("法定代表人")
    private String corpRepresent;

    @ApiModelProperty("注册资本")
    private Long registerCapital;

    @ApiModelProperty("成立日期")
    private LocalDate establishDate;

    @ApiModelProperty("客户类型 ClientType")
    private String clientType;

    @ApiModelProperty("统一社会信用代码")
    private String uscCode;

    @ApiModelProperty("证件类型")
    private String certType;
    @ApiModelProperty("证件号")
    private String certNumber;

    @ApiModelProperty("经营范围")
    private String bizScope;

}
