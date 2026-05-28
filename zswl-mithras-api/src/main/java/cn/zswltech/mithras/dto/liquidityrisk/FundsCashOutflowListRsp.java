package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2023-05-15
 **/

@Data
public class FundsCashOutflowListRsp {

    @ApiModelProperty("明细记录")
    private PageR<Record> records;

    @ApiModelProperty("小计")
    private Sum pageSum;

    @ApiModelProperty("合计")
    private Sum sum;

    @Data
    public static class Record {
        @ApiModelProperty("融资机构Id")
        private List<Long> financingOrgIds;

        @ApiModelProperty("融资机构")
        private List<String> financingOrgs;

        @ApiModelProperty("融资详情Id")
        private Long financingId;

        @ApiModelProperty("融资编码")
        private String financingCode;

        @ApiModelProperty("融资总额")
        private Long financingAmount;

        @ApiModelProperty("现金流出时间")
        private LocalDate cashOutflowTime;

        @ApiModelProperty("本金")
        private Long principle;

        @ApiModelProperty("利息")
        private Long interest;

        @ApiModelProperty("预计流出现金流合计")
        private Long estimateCashOutflowAmount;

        @ApiModelProperty("来源, 直融 DIRECT，间融")
        private String financingType;
    }


    @Data
    public static class Sum{
        @ApiModelProperty("融资总额合计")
        private Long financingAmount;

        @ApiModelProperty("本金合计")
        private Long principle;

        @ApiModelProperty("利息合计")
        private Long interest;

        @ApiModelProperty("预计流出现金流合计-合计")
        private Long estimateCashOutflowAmount;

    }
}
