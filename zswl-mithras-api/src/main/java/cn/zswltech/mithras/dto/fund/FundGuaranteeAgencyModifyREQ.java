package cn.zswltech.mithras.dto.fund;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_guarantee_agency编辑-请求体")
public class FundGuaranteeAgencyModifyREQ {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "是否集团内关联方 1是/0否")
    private Integer relatedParty;

    @ApiModelProperty(value = "成立日期")
    private LocalDate establishDate;

    @ApiModelProperty(value = "核准日期")
    private LocalDate approvalDate;

    @ApiModelProperty(value = "营运许可证是否为长期")
    private Boolean longTimeLicense;

    @ApiModelProperty(value = "营业许可证到期日(如果许可证是非长期类型)")
    private LocalDate bizLicenseEndDate;

    @ApiModelProperty(value = "经济类型")
    private String economyType;

    @ApiModelProperty(value = "组织机构类型")
    private String orgType;

    @ApiModelProperty(value = "注册币种")
    private String registerCurrencyType;

    @ApiModelProperty(value = "注册资本")
    private Long registerCapital;

    @ApiModelProperty(value = "实收币种")
    private String realCurrencyType;

    @ApiModelProperty(value = "实收资本")
    private Long realCapital;

    @ApiModelProperty(value = "法人代表")
    private String corpRepresent;

    @ApiModelProperty(value = "业务范围")
    private String bizScope;

    @ApiModelProperty(value = "备注")
    private String remark;

}
