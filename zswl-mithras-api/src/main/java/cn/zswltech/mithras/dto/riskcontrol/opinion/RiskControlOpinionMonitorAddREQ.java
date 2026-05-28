package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @description 风控舆情监控
 * @author vico
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控新增-请求体")
public class RiskControlOpinionMonitorAddREQ {

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    /**
     * 工商舆情&基础舆情的类别
     * 1 基础舆情  2 工商舆情
     */
    @ApiModelProperty(value = "舆情类型")
    private Integer riskType;

    /**
     * 工商舆情类型：
     * 企业变更
     * 开庭公告
     * 法院公告
     * 立案信息
     */
    @ApiModelProperty(value = "工商舆情类型")
    private String newTypeOpinion;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

}
