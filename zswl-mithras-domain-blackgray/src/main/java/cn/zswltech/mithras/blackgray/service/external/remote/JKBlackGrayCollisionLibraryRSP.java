package cn.zswltech.mithras.blackgray.service.external.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description 黑灰名单撞库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单撞库-返回体")
public class JKBlackGrayCollisionLibraryRSP extends JKBaseRSP {

    private JKBlackGrayCollisionLibraryRSP.JKBlackGrayCollisionLibraryBody data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JKBlackGrayCollisionLibraryBody {
        /**
         * 企业名称
         */
        @ApiModelProperty(value = "企业名称")
        private String enterpriseName;


        @ApiModelProperty(value = "状态代码")
        private String enterpriseStatusCode;

        @ApiModelProperty(value = "名称")
        private String enterpriseStatusName;

        /**
         * 入库时间
         */
        @ApiModelProperty(value = "入库时间")
        private String warehouseTime;

        /**
         * 申请原因 = 入库原因
         */
        @ApiModelProperty(value = "入库原因")
        private String applyReason;

        @ApiModelProperty(value = "入库原因类型")
        private List<String> applyReasonType;
        @ApiModelProperty(value = "入库原因类型描述")
        private List<String> applyReasonTypeName;
    }

}
