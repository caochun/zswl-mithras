package cn.zswltech.mithras.dto.client.bondinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author luyi
 */
@Data
@ApiModel("发债及评级信息列表-返回体")
public class CorpBondInfoListRSP extends ListBaseRSP {

    /**
     * 客户id
     */
    @ApiModelProperty("客户id")
    private Long clientId;

    /**
     * 评级日期
     */
    @ApiModelProperty("评级日期")
    private LocalDate rateDate;

    /**
     * 评级公司
     */
    @ApiModelProperty("评级公司")
    private String rateCompany;

    /**
     * 评级
     */
    @ApiModelProperty("评级")
    private String rate;

    /**
     * 评级展望
     */
    @ApiModelProperty("评级展望")
    private String rateFuture;

    /**
     * 发行总额，单位亿元
     */
    @ApiModelProperty("发行总额")
    private Long issueTotal;

    /**
     * 发行只数
     */
    @ApiModelProperty("发行只数")
    private Long issueAmount;

    /**
     * 存量规模，单位：亿元
     */
    @ApiModelProperty("存量规模")
    private Long stockScale;

    /**
     * 存量只数
     */
    @ApiModelProperty("存量只数")
    private Long stockAmount;

    /**
     * 到期规模，单位：亿元
     */
    @ApiModelProperty("到期规模，单位：亿元")
    private Long maturityScale;

    /**
     * 到期只数
     */
    @ApiModelProperty("到期只数")
    private Long maturityAmount;
}
