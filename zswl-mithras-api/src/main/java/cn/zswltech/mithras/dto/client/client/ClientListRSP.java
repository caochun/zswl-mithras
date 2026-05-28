package cn.zswltech.mithras.dto.client.client;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author junke
 */
@ApiModel("客户列表-返回体")
@Data
public class ClientListRSP {

    @ApiModelProperty("客户id")
    private Long id;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("客户编号")
    private String clientCode;
    @ApiModelProperty("客户类型")
    private String clientType;
    @ApiModelProperty("境内or境外")
    private String domesticOrAbroad;
    @ApiModelProperty("行业分类")
    private String industryType;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("创建人id")
    private Long createBy;
    @ApiModelProperty("创建人名称")
    private String creatorName;
    @ApiModelProperty("创建人部门id")
    private Long createByDept;
    @ApiModelProperty("创建人部门")
    private String creatorDeptName;
    @ApiModelProperty("客户状态")
    private String clientStatus;
    @ApiModelProperty("流程状态")
    private String processStatus;
    @ApiModelProperty("流程状态（中文）")
    private String processStatusName;
    @ApiModelProperty("行业分类名称")
    private String industryTypeName;
    @ApiModelProperty("是否需要审批（列表请求体showApprovalFlag为true时有该字段）（流程状态为变更中有该字段）")
    private Boolean needApprovalFlag;

    //@Since V1030
    @ApiModelProperty("所属部门id")
    private Long belongDeptId;
    @ApiModelProperty("所属部门名称")
    private String belongDeptName;
    @ApiModelProperty("所属负责项目经理的id")
    private Long belongSponsorId;
    @ApiModelProperty("所属负责项目经理的名称")
    private String belongSponsorName;

    @ApiModelProperty(value = "授信金额（万元）")
    private Long applyCreditAmount;

    @ApiModelProperty("剩余本金（万元）")
    private Long lastPrincipal;

    @ApiModelProperty("存量风险敞口（万元）")
    private Long stockRiskExposure;
    @ApiModelProperty("是否被释放")
    private Integer isReleased;
    @ApiModelProperty("是否可进行删除操作")
    private Integer canDelete = 0;
    @ApiModelProperty("项目经理最高权限")
    private String maxAuthority;

    @ApiModelProperty("省")
    private String provinceCode;
    @ApiModelProperty("省名称")
    private String provinceName;
    @ApiModelProperty("市")
    private String cityCode;
    @ApiModelProperty("市名称")
    private String cityName;
    @ApiModelProperty("区")
    private String districtCode;
    @ApiModelProperty("区名称")
    private String districtName;
    @ApiModelProperty("企业性质")
    private String enterpriseNature;
    @ApiModelProperty("企业性质名称")
    private String enterpriseNatureName;
    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;
    @ApiModelProperty("风控行业分类名称")
    private String riskControlIndustryClassifyName;
    @ApiModelProperty("统一社会信用代码")
    private String uscCode;
}
