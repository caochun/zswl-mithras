package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询承租人/担保人信息
 * @date 2022-11-17
 */
@Data
@ApiModel("租后检查外部查询承租人/担保人信息编辑-请求体")
public class AfterLeaseCheckExternalQueryClientInfoModifyReq {

    @ApiModelProperty(value = "id")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "查询时间from")
    @NotNull(message = "检查日期区间为必填项")
    private LocalDate queryTimeFrom;

    @ApiModelProperty(value = "查询时间to")
    @NotNull(message = "检查日期区间为必填项")
    private LocalDate queryTimeTo;

    @ApiModelProperty(value = "全国企业信用信息公示系统查询")
    private String creditInfo;

    @ApiModelProperty(value = "全国法院被执行人或被纳入失信人查询")
    private String courtInfo;

    @ApiModelProperty(value = "裁判文书网")
    private String refereeNetworkInfo;

    @ApiModelProperty(value = "中登网登记及抵押登记")
    private String zhongdengInfo;

    @ApiModelProperty(value = "信用报告")
    private String creditReport;

    @ApiModelProperty(value = "其他")
    private String other;
}
