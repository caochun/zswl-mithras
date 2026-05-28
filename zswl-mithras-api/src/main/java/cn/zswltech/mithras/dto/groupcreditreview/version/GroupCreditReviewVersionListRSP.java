package cn.zswltech.mithras.dto.groupcreditreview.version;

import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信评审信息版本表列表-返回体")
public class GroupCreditReviewVersionListRSP extends CommonVersionListRSP {

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("变更时间")
    private LocalDateTime gmtModify;

}
