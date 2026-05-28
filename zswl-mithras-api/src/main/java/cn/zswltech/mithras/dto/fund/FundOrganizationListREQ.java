package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 资金管理-机构表
 * @date 2022-12-13
 */
@Data
@ApiModel("资金管理-机构表列表-请求体")
public class FundOrganizationListREQ extends PageReq {

    @ApiModelProperty(value = "机构名称")
    private String organizationName;

    @ApiModelProperty(value = "机构id")
    private Long organizationId;

    @ApiModelProperty(value = "机构类型 枚举")
    private String organizationType;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间from")
    private LocalDate createDateFrom;

    @ApiModelProperty("创建时间to")
    private LocalDate createDateTo;

    @ApiModelProperty("机构简称")
    private String abbreviation;
}
