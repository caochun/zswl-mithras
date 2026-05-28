package cn.zswltech.mithras.dto.groupcreditreview.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@Accessors(chain = true)
@ApiModel("集团授信评审基本信息新增-返回体")
public class GroupCreditReviewBaseInfoAddRSP {

    @ApiModelProperty(value = "集团授信评审基本id")
    private Long id;

}
