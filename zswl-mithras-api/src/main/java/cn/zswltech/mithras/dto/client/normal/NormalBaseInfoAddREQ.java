package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("自然人基本信息新增-请求体")
public class NormalBaseInfoAddREQ {
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;


    /**
     * 证件类型
     */
    @NotBlank
    @ApiModelProperty(value = "证件类型", required = true)
    private String certType;

    /**
     * 证件号码
     */
    @NotBlank
    @ApiModelProperty(value = "证件号码", required = true)
    private String certNumber;

    /**
     * 性别
     */
    @NotBlank
    @ApiModelProperty(value = "性别", required = true)
    private String gender;

    /**
     * 婚姻情况
     */
    @NotBlank
    @ApiModelProperty(value = "婚姻情况", required = true)
    private String marriageType;

    /**
     * 国家
     */
    @NotBlank
    @ApiModelProperty(value = "国别", required = true)
    private String country;

    /**
     * 年龄
     */
    @ApiModelProperty("年龄")
    private Integer age;

    /**
     * 手机号
     */
//    @NotBlank
    @ApiModelProperty(value = "手机号码", required = true)
    private String mobileNumber;

    /**
     * 家庭地址
     */
    @ApiModelProperty("家庭地址")
    private String homeAddress;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String mail;
}
