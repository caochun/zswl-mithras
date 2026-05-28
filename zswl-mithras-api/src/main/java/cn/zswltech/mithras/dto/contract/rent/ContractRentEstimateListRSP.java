package cn.zswltech.mithras.dto.contract.rent;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("概算租金表-返回体")
public class ContractRentEstimateListRSP extends ListBaseRSP {
    @ApiModelProperty("计划起租日")
    private String planStartDate;

    @ApiModelProperty("概算租金表表格数据")
    private List<TableData> rentEstimateList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @ApiModel("概算租金表表格数据-返回体")
    public static class TableData extends ListBaseRSP {
        @ApiModelProperty("日期")
        private String date;

        @ApiModelProperty("期项")
        private Integer phase;

        @ApiModelProperty("租金（毫厘）")
        private Long rent;

        @ApiModelProperty("本金（毫厘）")
        private Long principal;

        @ApiModelProperty("利息（毫厘）")
        private Long interest;

        @ApiModelProperty("剩余本金（毫厘）")
        private Long remainingPrincipal;
    }
}
