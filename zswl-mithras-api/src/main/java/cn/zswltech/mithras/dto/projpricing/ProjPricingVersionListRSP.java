package cn.zswltech.mithras.dto.projpricing;

import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评审信息版本返回体
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("评审信息版本返回体")
public class ProjPricingVersionListRSP extends CommonVersionListRSP {

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("变更时间")
    private LocalDateTime gmtModify;

}
