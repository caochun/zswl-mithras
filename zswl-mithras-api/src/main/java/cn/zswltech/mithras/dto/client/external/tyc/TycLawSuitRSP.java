package cn.zswltech.mithras.dto.client.external.tyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 法律诉讼
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("天眼查-法律诉讼")
public class TycLawSuitRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 案件名称
     */
    @ApiModelProperty("案件名称")
    private String title;

    /**
     * 案由
     */
    @ApiModelProperty("案由")
    private String caseReason;

    /**
     * 在本案中身份
     */
    @ApiModelProperty("在本案中身份")
    private String identity;

    /**
     * 裁判结果
     */
    @ApiModelProperty("裁判结果")
    private String judgeResult;

    /**
     * 结果标签
     */
    @ApiModelProperty("结果标签")
    private String resultTag;

    /**
     * 案件金额
     */
    @ApiModelProperty("案件金额")
    private String caseMoney;

    /**
     * 天眼查url（web）
     * 详情
     */
    @ApiModelProperty("详情")
    private String detailUrl;

}
