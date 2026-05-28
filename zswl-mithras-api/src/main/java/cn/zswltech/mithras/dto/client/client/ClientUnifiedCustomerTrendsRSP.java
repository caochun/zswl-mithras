package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class ClientUnifiedCustomerTrendsRSP {

    @ApiModelProperty("卡片名称 DashboardCardGroupEnum")
    private String cordName;

    @ApiModelProperty("折线详情")
    private List<CustomerTrendsBody> items;

    @Data
    @Builder
    public static class CustomerTrendsBody {
        @ApiModelProperty("所属月份")
        private LocalDate belongTime;

        @ApiModelProperty("客户数量 ContractStatus")
        private Long clientSum;

    }
}
