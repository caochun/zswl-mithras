package cn.zswltech.mithras.dto.groupcreditreview.baseinfo;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信评审基本信息详情-请求体")
public class GroupCreditReviewBaseInfoDetailREQ extends VersionBaseREQ {

    @NotNull
    @ApiModelProperty(value = "集团授信评审id")
    private Long groupCreditReviewId;

}
