package cn.zswltech.mithras.dto.client.contactinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
public class CorpContactAddInfoREQ {

    @NotNull
    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;

    @ApiModelProperty("是否主联系人")
    private Boolean main;

    @NotBlank(message = "职务不能为空")
    @ApiModelProperty("职务")
    private String position;

    @ApiModelProperty("性别")
    private String gender;

    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty("姓名")
    private String name;

    @NotBlank(message = "电话不能为空")
    @ApiModelProperty("电话")
    private String telephone;

    @ApiModelProperty("座机")
    private String landlineTelephone;

    @ApiModelProperty("邮箱")
    private String mail;

    @ApiModelProperty("证件类型")
    private String certType;

    @ApiModelProperty("证件号码")
    private String certNumber;
}
