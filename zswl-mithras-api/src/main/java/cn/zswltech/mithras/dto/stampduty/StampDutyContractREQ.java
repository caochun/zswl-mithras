package cn.zswltech.mithras.dto.stampduty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author luyujie
 * @date 2026/1/26
 * @description 印花税缴纳明细表
 */
@Data
@ApiModel("印花税缴纳合同、融资合同列表-请求体")
public class StampDutyContractREQ extends PageReq {

    /**
     * 查询类型
     */
    @NotBlank
    @ApiModelProperty("查询类型（ht、rzht）")
    private String type;

    /**
     * 关联id（合同id、融资id）
     */
    @ApiModelProperty("关联id")
    private String belongId;

    /**
     * 编号
     */
    @ApiModelProperty("编号")
    private String code;
}
