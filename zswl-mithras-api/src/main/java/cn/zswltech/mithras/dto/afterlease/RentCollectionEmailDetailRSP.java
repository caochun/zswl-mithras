package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 租金催收发送邮件详情页请求体
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:22 PM
 */
@Data
@ApiModel("租金催收发送邮件详情页返回体")
public class RentCollectionEmailDetailRSP {

    @ApiModelProperty("收信邮箱")
    private String receiverMail;

    @ApiModelProperty("备注")
    private String comment;

    @ApiModelProperty("邮件标题")
    private String title;

    @ApiModelProperty("银行账号")
    private Long bankId;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账号")
    private String accountNumber;

    @ApiModelProperty("支行名称")
    private String accountBank;

//    @ApiModelProperty("onlyoffice预览文件的id")
//    private Long fileId;

    @ApiModelProperty("是否曾经发送过（发送过发送界面不可操作）")
    private Boolean sendFlag;

    @ApiModelProperty("发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty("是否到达可发送时间（7天内）")
    private Boolean timeAvaliableFlag;

    @ApiModelProperty("获取可预览的html")
    private String htmlPreviewUrl;

    @ApiModelProperty("默认展示的还款账户")
    private BaseDataBankAccountListRSP defaultBankAccount;
}
