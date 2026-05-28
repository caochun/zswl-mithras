package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/10/23 09:46
 * @description
 */
@Data
@ApiModel(value = "本年租赁业务投放收益率情况表-响应体")
public class CurrentYearBusinessPayReceiptRateListRSP {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Month {
        @ApiModelProperty(value = "月份 format: yyyy-MM")
        private String month;

        @ApiModelProperty(value = "产业收益率")
        private ValueUnitDTO industryPayReceiptRate;

        @ApiModelProperty(value = "公用事业收益率")
        private ValueUnitDTO publicPayReceiptRate;
    }

    @ApiModelProperty(value = "全年平均")
    private ValueUnitDTO average;

    @ApiModelProperty(value = "月份列表")
    private List<Month> monthList;
}
