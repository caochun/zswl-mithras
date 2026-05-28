package cn.zswltech.mithras.dto.client.contactinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author luyi
 */
@Data
public class NewCorpContactInfoListRSP extends ListBaseRSP {

    @ApiModelProperty("是否主联系人")
    private Boolean main;

    @ApiModelProperty("职务")
    private String position;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("姓名")
    private String name;

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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "权限级别, 管护权为3，申办权为2，查看权为1")
    private Integer level;

    @ApiModelProperty(value = "客户状态,新建或生效")
    private String clientStatus;
}
