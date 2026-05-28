package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后管理-租后检查财务报表快照数据-返回体")
public class AfterLeaseCheckReportFinanceRSP extends ListBaseRSP {
    @ApiModelProperty("查询条件数据")
    private QueryData queryData;

    @ApiModelProperty("查询结果数据")
    private List<CorpSubjectItemListRSP> resultData;

    @Data
    public static class QueryData {
        @NotNull
        @ApiModelProperty(value = "客户id", required = true)
        private Long clientId;

        @NotBlank
        @ApiModelProperty("sheet类型")
        private String subjectType;

        @ApiModelProperty("报告期")
        private Integer quarter;

        @ApiModelProperty("最新")
        private Boolean latest;

        @ApiModelProperty("年-从")
        private Integer yearFrom;

        @ApiModelProperty("年-到")
        private Integer yearTo;

        @ApiModelProperty("报表类型")
        private String reportType;

        @NotNull
        @ApiModelProperty("数据显示维度；")
        private List<String> displayDimensions;

        @ApiModelProperty(value = "小数位数", notes = "0:0,0.0:1,0.00:2,0.000:3,0.0000:4")
        private Integer decimalCount = 0;

        @ApiModelProperty(value = "金额单位", notes = "元：1,万元：10000,亿元：100000000")
        private Long unit = 1L;
    }
}
