package cn.zswltech.mithras.dto.client.external.tyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 行政处罚
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("天眼查-行政处罚")
public class TycPunishmentInfoRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 日期
     */
    @ApiModelProperty("处罚日期")
    private String decisionDate;

    /**
     * 决定⽂书号
     */
    @ApiModelProperty("决定文书号")
    private String punishNumber;

    /**
     * 处罚事由/违法行为类型
     */
    @ApiModelProperty("处罚事由/违法行为类型")
    private String reason;

    /**
     * 处罚结果/内容
     */
    @ApiModelProperty("处罚结果/内容")
    private String content;

    /**
     * 处罚单位
     */
    @ApiModelProperty("处罚单位")
    private String departmentName;

    /**
     * 数据来源
     */
    @ApiModelProperty("数据来源")
    private String source;

}
