package cn.zswltech.mithras.dto.client.external.tyc;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 股权出质
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("股权出质")
public class TycEquityInfoRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 股权出质设立登记日期
     */
    @ApiModelProperty("登记日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;

    /**
     * 登记编号
     */
    @ApiModelProperty("登记编号")
    private String regNumber;

    /**
     * 出质人
     */
    @ApiModelProperty("出质人")
    private String pledgor;

    /**
     * 出质股权标的企业
     */
    @ApiModelProperty("出质股权标的企业")
    private String targetCompany;

    /**
     * 质权人
     */
    @ApiModelProperty("质权人")
    private String pledgee;

    /**
     * 出质股权数额
     */
    @ApiModelProperty("出质股权数额")
    private String equityAmount;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String state;

}
