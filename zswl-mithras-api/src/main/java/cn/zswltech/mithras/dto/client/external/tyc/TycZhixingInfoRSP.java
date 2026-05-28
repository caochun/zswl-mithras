package cn.zswltech.mithras.dto.client.external.tyc;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 被执行人
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("天眼查-被执行人")
public class TycZhixingInfoRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 创建时间
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
     * 执行标的（元）
     */
    @ApiModelProperty("执行标的（元）")
    private String execMoney;

    /**
     * 执行法院
     */
    @ApiModelProperty("执行法院")
    private String execCourtName;


}
