package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/4
 * @description
 */
@Data
@ApiModel("风控管理-资产分类修改客户详情-请求体")
public class AssetClassifyClientModifyREQ {
    @NotNull(message = "id不能为空")
    @ApiModelProperty("id")
    private Long id;

    /**
     * 1.调整 0不调整
     */
    @ApiModelProperty("1.调整 0不调整")
    private int suggestFlag;

    /**
     * 建议分类结果
     */
    @ApiModelProperty("建议分类结果")
    private String suggestResult;

    /**
     * 建议原因
     */
    @ApiModelProperty("建议原因")
    private String suggestReason;


    /**
     * 备注
     **/
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("拨备计提比例")
    private Long awardRatio;
}
