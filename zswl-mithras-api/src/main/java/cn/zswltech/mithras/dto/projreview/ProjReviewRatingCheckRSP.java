package cn.zswltech.mithras.dto.projreview;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/1/15
 * @description
 */
@Data
public class ProjReviewRatingCheckRSP {
    @ApiModelProperty("是否完成了客户评级")
    private Boolean ratingClientIsDone = false;

    @ApiModelProperty("未完成客户评级的客户信息")
    private List<ClientInfo> undoRatingClientList;

    @ApiModelProperty("是否完成了债项评级")
    private Boolean ratingAmountIsDone = false;

    @AllArgsConstructor
    @Data
    public static class ClientInfo {
        private Long clientId;
        private String clientName;
    }
}
