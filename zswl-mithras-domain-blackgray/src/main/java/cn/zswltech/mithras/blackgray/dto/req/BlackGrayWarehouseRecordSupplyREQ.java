package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @description 黑灰名单记录表
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录补充所属集团")
public class BlackGrayWarehouseRecordSupplyREQ {

    @ApiModelProperty(value = "任务编号")
    @NotEmpty(message = "任务编号不能为空")
    private String taskNum;

    @ApiModelProperty(value = "要补充所属集团的数据项")
    private List<Item> modifyItems;

    @Data
    public static class Item {
        /**
         * id
         */
        @ApiModelProperty(value = "id")
        private Long id;

        /**
         * 企业名称
         */
        @ApiModelProperty(value = "企业名称")
        private String enterpriseName;

        /**
         * 统一社会信用代码
         */
        @ApiModelProperty(value = "统一社会信用代码")
        private String unifiedSocialCreditCode;
    }
}
