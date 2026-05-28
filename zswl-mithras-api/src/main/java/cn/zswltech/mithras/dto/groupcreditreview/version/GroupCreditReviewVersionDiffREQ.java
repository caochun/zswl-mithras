package cn.zswltech.mithras.dto.groupcreditreview.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@ApiModel("集团授信评审版本差异比较-入参")
@Data
public class GroupCreditReviewVersionDiffREQ {
    @ApiModelProperty("版本id")
    @NotNull
    private Long id;
}
