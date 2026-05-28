package cn.zswltech.mithras.dto.flow.search;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 流程操作历史查询
 *
 * @author wangchuanhao
 * @date 2022/8/2 11:21 PM
 */
@Data
public class ProcessHistoryREQ extends PageReq {

    @ApiModelProperty("流程实例id")
    @NotBlank
    private String processInstanceId;

}
