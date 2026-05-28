package cn.zswltech.mithras.dto.groupcreditreview.baseinfo;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信评审基本信息表新增-请求体")
public class GroupCreditReviewBaseInfoAddREQ {

    @ApiModelProperty(value = "集团授信立项ID")
    @NotNull(message = "集团授信立项Id为null")
    private Long groupCreditEstablishId;

}
