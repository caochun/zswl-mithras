package cn.zswltech.mithras.third.baorong.infrastructure.client.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
/**
 * 银企历史明细列表查询==流水查询接口
 * serviceCode = CWGS001
 * serviceNo = 10001001
 **/
@Data
public class BRFlowHistoryReq {
    private CwgsHead cwgsHead;
    private BRFlowHistoryReqBody body;
    private CwgsApiAppUser cwgsApiAppUser;

    @Data
    public class BRFlowHistoryReqBody {
        private String tradedate;
        @JsonProperty("_pageSize")
        private Integer _pageSize;
        @JsonProperty("_pageIndex")
        private Integer _pageIndex;
    }

}