package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 租金催收发送邮件
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:22 PM
 */
@Data
@ApiModel("租金催收发送邮件请求体")
public class RentCollectionEmailSendREQ {

    @ApiModelProperty("收款id")
    @NotNull
    private Long collectionId;

    @ApiModelProperty("收信邮箱")
    @NotBlank
    private String receiverMail;

    @ApiModelProperty("备注")
    private String comment;

    @ApiModelProperty("邮件标题")
    @NotBlank
    private String title;

    @ApiModelProperty("我方银行账号id")
    @NotNull
    private Long bankId;

}
