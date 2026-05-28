package cn.zswltech.mithras.dto.projreview;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/30 16:12
 */
@Data
@ApiModel("评审按钮状态-返回体")
public class ProjReviewButtonStatusRsp {
    @ApiModelProperty("是否可保存，1可保存，0不可保存")
    public Integer canSaveFlag;

    @ApiModelProperty("是否可确认或提交审批，1可提交审批，0不可提交")
    public Integer canEffectFlag;
}
