package cn.zswltech.mithras.dto.client.lifecycle;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 14:35
 */
@ApiModel("客户全周期列表请求体")
@Data
public class ClientLifeCycleListReq extends PageReq {

    @ApiModelProperty("客户id")
    private String clientName;

    @ApiModelProperty("主办id")
    private Long sponsorId;

    @ApiModelProperty("创建日期From")
    private LocalDate createDateFrom;

    @ApiModelProperty("创建日期To")
    private LocalDate createDateTo;

    @ApiModelProperty("所属部门id")
    private Long deptId;

    @ApiModelProperty("创建人id")
    private Long createBy;

    @ApiModelProperty("所选卡片, 可选值 TOTAL, EXISTING, SETTLED, OVERDUE")
    private String cardName;

    @ApiModelProperty("页码")
    private Integer page = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;

    @ApiModelProperty("省")
    private String provinceCode;

    @ApiModelProperty("市")
    private String cityCode;

    @ApiModelProperty("区")
    private String districtCode;

    @ApiModelProperty("企业性质")
    private String enterpriseNature;

    @ApiModelProperty("国标行业分类")
    private String industryType;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;
}
