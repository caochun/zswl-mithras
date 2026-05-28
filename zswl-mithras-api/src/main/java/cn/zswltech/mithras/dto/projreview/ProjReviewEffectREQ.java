package cn.zswltech.mithras.dto.projreview;

import cn.zswltech.mithras.dto.process.modify.remark.ProcessModifyRemarkAddREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel(value = "立项生效-请求体")
public class ProjReviewEffectREQ {
    @NotNull
    @ApiModelProperty("立项Id")
    public Long id;

    @NotNull
    @ApiModelProperty("只是检验是否能提交审批")
    private Boolean onlyCheck = false;

    @ApiModelProperty("变更说明")
    private ProcessModifyRemarkAddREQ remarkAddREQ;

}
