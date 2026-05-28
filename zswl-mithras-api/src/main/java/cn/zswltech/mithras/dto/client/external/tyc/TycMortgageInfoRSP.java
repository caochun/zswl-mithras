package cn.zswltech.mithras.dto.client.external.tyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 动产抵押
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("天眼查-动产抵押")
public class TycMortgageInfoRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 登记日期
     */
    @ApiModelProperty("登记日期")
    private String regDate;

    /**
     * 登记编号
     */
    @ApiModelProperty("登记编号")
    private String regNum;

    /**
     * 抵押权人信息
     */
    @ApiModelProperty("抵押权人信息")
    private String peopleInfo;

    /**
     * 所有权或使用归属权
     */
    @ApiModelProperty("所有权或使用归属权")
    private String belongTo;

    /**
     * 被担保债权类型
     */
    @ApiModelProperty("被担保债权类型")
    private String type;

    /**
     * 被担保债权数额
     */
    @ApiModelProperty("被担保债权数额")
    private String amount;

    /**
     * 债务人履行债务的期限
     */
    @ApiModelProperty("债务人履行债务的期限")
    private String term;

    /**
     * 登记机关
     */
    @ApiModelProperty("登记机关")
    private String regDepartment;

}
