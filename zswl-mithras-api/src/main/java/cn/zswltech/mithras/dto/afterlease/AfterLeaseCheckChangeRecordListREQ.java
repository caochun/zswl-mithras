package cn.zswltech.mithras.dto.afterlease;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 租后检查计划-基本信息表
 * @author vico
 * @date 2024-04-22
 */
@Data
@ApiModel("租后检查计划-基本信息表列表-请求体")
public class AfterLeaseCheckChangeRecordListREQ extends PageReq {


    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id")
    private String planId;

    /**
     * 计划-客户id
     */
    @ApiModelProperty(value = "计划-客户id")
    private String checkPlanClientId;

}
