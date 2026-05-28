package cn.zswltech.mithras.dto.client.external.tyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 经营异常
 *
 * @author wangchuanhao
 * @date 2022/6/21 9:57 AM
 */
@Data
@ApiModel("天眼查-经营异常")
public class TycAbnormalRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 列入日期
     */
    @ApiModelProperty("列入日期")
    private String putDate;

    /**
     * 列入异常名录原因
     */
    @ApiModelProperty("列入异常名录原因")
    private String putReason;

    /**
     * 决定列⼊异常名录部⻔(作出决定机关)
     */
    @ApiModelProperty("决定列⼊异常名录部⻔(作出决定机关)")
    private String putDepartment;

    /**
     * 移出日期
     */
    @ApiModelProperty("移出日期")
    private String removeDate;

    /**
     * 移除异常名录原因
     */
    @ApiModelProperty("移除异常名录原因")
    private String removeReason;

    /**
     * 移出部⻔
     */
    @ApiModelProperty("移出部⻔")
    private String removeDepartment;

}
