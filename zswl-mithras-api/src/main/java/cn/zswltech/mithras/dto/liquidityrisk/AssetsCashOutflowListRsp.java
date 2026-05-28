package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2023-05-15
 **/

@Data
public class AssetsCashOutflowListRsp {

    @ApiModelProperty("明细记录")
    private PageR<AssetsCashOutflowListRsp.Record> records;

    @ApiModelProperty("小计")
    private Sum pageSum;

    @ApiModelProperty("合计")
    private Sum sum;

    @Data
    public static class Record {
        @ApiModelProperty("项目id")
        private Long projId;

        @ApiModelProperty("数据类型")
        private String dataType;

        @ApiModelProperty("项目名称")
        private String projName;

        @ApiModelProperty("合同Id")
        private Long contractId;

        @ApiModelProperty("合同编号")
        private String contractCode;

        @ApiModelProperty("合同总金额")
        private Long contractAmount;

        @ApiModelProperty("现金流出时间")
        private LocalDate cashOutflowTime;

        @ApiModelProperty("保证金")
        private Long earnestMoney;

        @ApiModelProperty("预计流出现金流合计")
        private Long estimateCashOutflowAmount;
    }


    @Data
    public static class Sum{
        @ApiModelProperty("合同总金额合计")
        private Long contractAmount;


        @ApiModelProperty("保证金合计")
        private Long earnestMoney;

        @ApiModelProperty("预计流出现金流合计-合计")
        private Long estimateCashOutflowAmount;
    }

}
