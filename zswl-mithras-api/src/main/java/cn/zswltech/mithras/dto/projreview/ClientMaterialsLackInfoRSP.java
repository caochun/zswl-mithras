package cn.zswltech.mithras.dto.projreview;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/10/22
 * @description
 */
@Data
public class ClientMaterialsLackInfoRSP {
    @ApiModelProperty("是否存在材料缺少，0-否，1-是")
    private Integer isLack;
    @ApiModelProperty("缺少材料明细列表")
    private List<LackInfo> detailList;

    @Data
    public static class LackInfo {
        @ApiModelProperty("客户id")
        private Long clientId;
        @ApiModelProperty("客户名称")
        private String clientName;
        @ApiModelProperty("缺失材料")
        private List<String> lackMaterialsList;
    }
}
