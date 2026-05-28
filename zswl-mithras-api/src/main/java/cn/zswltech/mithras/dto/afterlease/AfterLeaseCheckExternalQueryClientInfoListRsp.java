package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询承租人/担保人信息
 * @date 2022-11-17
 */
@Data
@ApiModel("租后检查外部查询承租人/担保人信息列表-返回体")
public class AfterLeaseCheckExternalQueryClientInfoListRsp extends ListBaseRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "所属外部信息查询任务id")
    private Long queryId;

    @ApiModelProperty(value = "承租人/担保人id")
    private Long clientId;

    @ApiModelProperty(value = "client类型")
    private String clientType;

    @ApiModelProperty(value = "角色")
    private String clientRole;

    @ApiModelProperty(value = "承租人/担保人Name")
    private String clientName;

    @ApiModelProperty(value = "查询范围from")
    private LocalDate queryTimeFrom;

    @ApiModelProperty(value = "查询范围to")
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
