package cn.zswltech.mithras.dto.client.external.tyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 司法协助
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("天眼查-司法协助")
public class TycJudicialRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 公示日期
     */
    @ApiModelProperty("公示日期")
    private String publicityDate;

    /**
     * 执行通知书文号
     */
    @ApiModelProperty("执行通知书文号")
    private String executeNoticeNum;

    /**
     * 被执行人
     */
    @ApiModelProperty("被执行人")
    private String executedPerson;

    /**
     * 股权被执行的企业
     */
    @ApiModelProperty("股权被执行的企业")
    private String stockExecutedCompany;

    /**
     * 股权数额
     */
    @ApiModelProperty("股权数额")
    private String equityAmount;

    /**
     * 执行法院
     */
    @ApiModelProperty("执行法院")
    private String executiveCourt;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String typeState;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

}
