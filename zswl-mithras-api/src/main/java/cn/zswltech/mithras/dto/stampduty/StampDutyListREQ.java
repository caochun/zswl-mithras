package cn.zswltech.mithras.dto.stampduty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author luyujie
 * @date 2026/1/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("印花税管理台账列表-请求体")
public class StampDutyListREQ extends PageReq {

    @ApiModelProperty("申报税目名称")
    private String name;

    @ApiModelProperty("业务部门名称")
    private String belongOrgName;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("融资机构")
    private String organizationName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("实际起租日（起） yyyy-MM-dd")
    private String startDateFrom;

    @ApiModelProperty("实际起租日（止） yyyy-MM-dd")
    private String startDateTo;

    @NotNull
    @ApiModelProperty("印花税明细")
    private List<Long> ids;
}
