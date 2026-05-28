package cn.zswltech.mithras.dto.client.external.tyc;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 限制消费令
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("天眼查-限制消费令")
public class TycConsumptionRestrictionRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 立案时间
     */
    @ApiModelProperty("立案日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime caseCreateTime;

    /**
     * 案号
     */
    @ApiModelProperty("案号")
    private String caseCode;

    /**
     * 限制消费者名称
     */
    @ApiModelProperty("限制消费对象")
    private String xname;

    /**
     * 企业信息
     */
    @ApiModelProperty("关联限制消费对象")
    private String qyinfoAlias;

    /**
     * 申请人信息
     */
    @ApiModelProperty("申请人信息")
    private String applicant;

    /**
     * 发布日期
     */
    @ApiModelProperty("发布日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime publishDate;

    /**
     * 详情
     */
    @ApiModelProperty("详情")
    private String detailUrl;

}
