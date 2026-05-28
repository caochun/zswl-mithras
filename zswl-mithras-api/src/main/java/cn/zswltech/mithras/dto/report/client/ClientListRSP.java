package cn.zswltech.mithras.dto.report.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 征信报送-客户表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-客户表返回值")
public class ClientListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务标识")
    private String businessKey;

    /**
     * {@link cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum}
     */
    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty("客户编号")
    private String clientCode;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty("存续状态")
    private String continuousStatus;

    @ApiModelProperty("组织机构类型")
    private String orgType;

    @ApiModelProperty("注册地址")
    private String registerAddress;

    @ApiModelProperty("行政区划")
    private String regionCode;

    @ApiModelProperty("成立日期")
    private LocalDate establishDate;

    @ApiModelProperty("营业许可证到期日")
    private LocalDate bizLicenseEndDate;

    @ApiModelProperty("业务范围")
    private String bizScope;

    @ApiModelProperty("行业分类")
    private String industryType;

    @ApiModelProperty("行业分类名称")
    private String industryTypeName;

    @ApiModelProperty("经济类型")
    private String economyType;

    @ApiModelProperty("企业规模")
    private String orgScale;

    @ApiModelProperty("注册资本币种")
    private String registerCurrencyType;

    @ApiModelProperty("注册资本")
    private String registerCapital;

    @ApiModelProperty("法人代表")
    private String corpRepresent;

    @ApiModelProperty("法人证件类型")
    private String corpCertType;

    @ApiModelProperty("法人证件号码")
    private String corpCertCode;

    @ApiModelProperty("数据更新日期")
    private LocalDate effectDate;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;
}
