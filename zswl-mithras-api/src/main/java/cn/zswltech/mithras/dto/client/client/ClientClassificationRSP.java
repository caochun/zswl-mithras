package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class ClientClassificationRSP {
    @ApiModelProperty("五级分类")
    private String classification;

    @ApiModelProperty("最大逾期天数")
    private Integer maxOverdueDayCount = 0;

    @ApiModelProperty("逾期总金额")
    private Long totalAmountOverdue = 0L;

    @ApiModelProperty("逾期合同信息")
    private List<OvedueContarctRSP> contarctRSPList;

    @Data
    public static class OvedueContarctRSP {
        @ApiModelProperty("合同编号")
        private String contractCode;

        @ApiModelProperty(value = "期次")
        private String phase;

        @ApiModelProperty(value = "逾期金额")
        private Long overdueAmount;

        @ApiModelProperty(value = "逾期天数")
        private Integer overdueDays;
    }
}
