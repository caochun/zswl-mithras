package cn.zswltech.mithras.dto.riskcontrol.opinion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * @description 风控舆情监控
 * @author vico
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控编辑-请求体")
public class RiskControlOpinionMonitorModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 客户名称
    */
    @ApiModelProperty(value = "客户名称")
    private String chiName;

    /**
    * 标题
    */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
    * 情感方向
    */
    @ApiModelProperty(value = "情感方向")
    private String emotion;

    /**
    * 情感重要度
    */
    @ApiModelProperty(value = "情感重要度")
    private String importance;

    /**
    * 信息发布日期
    */
    @ApiModelProperty(value = "信息发布日期")
    private LocalDateTime infoPublDate;

    /**
    * 链接地址
    */
    @ApiModelProperty(value = "链接地址")
    private String linkAddress;

    /**
    * 新闻来源
    */
    @ApiModelProperty(value = "新闻来源")
    private String sourceName;

    /**
    * 统一社会信用代码
    */
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

}
