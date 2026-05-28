package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
@ApiModel("未处理舆情列表-返回体")
public class UnresolvedClientOpinionRSP {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("客户名称")
    private String chiName;

    @ApiModelProperty("信息发布日期")
    private LocalDateTime infoPublDate;

    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]，这里显示成五角星个数")
    private Integer warnStar;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯],这里显示成红绿灯的图标")
    private Integer warnLevel;
}
