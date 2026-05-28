package cn.zswltech.mithras.dto.groupcreditreview;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @description 创建合同之前的授信评审接口返回体
 * @author wangchuanhao
 * @date 2022-11-11
 */
@ApiModel("创建合同之前的授信评审接口返回体")
@Data
public class GroupCreditReviewVagueListRSP {

    @ApiModelProperty("集团授信评审Id")
    private Long id;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("客户名称")
    private List<String> clientNames;

//    @ApiModelProperty("剩余授信额度")
//    private Long remainCreditAmount;

}
