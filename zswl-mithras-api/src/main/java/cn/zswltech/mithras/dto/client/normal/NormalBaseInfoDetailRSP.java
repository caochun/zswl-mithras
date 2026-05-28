package cn.zswltech.mithras.dto.client.normal;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
@ApiModel("自热人基本信息详情-返回体")
public class NormalBaseInfoDetailRSP extends ListBaseRSP {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户分类")
    private String clientType;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "客户编号")
    private String clientCode;

    /**
     * 证件类型
     */
    @ApiModelProperty(value = "证件类型", required = true)
    private String certType;

    /**
     * 证件号码
     */
    @ApiModelProperty(value = "证件号码", required = true)
    private String certNumber;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别", required = true)
    private String gender;

    /**
     * 婚姻情况
     */
    @ApiModelProperty(value = "婚姻情况", required = true)
    private String marriageType;

    /**
     * 国家
     */
    @ApiModelProperty(value = "国别", required = true)
    private String country;

    @ApiModelProperty(value = "国别名称")
    private String countryName;


    /**
     * 年龄
     */
    @ApiModelProperty("年龄")
    private Integer age;

    /**
     * 手机号
     */
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
