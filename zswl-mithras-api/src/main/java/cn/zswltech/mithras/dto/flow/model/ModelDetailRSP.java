package cn.zswltech.mithras.dto.flow.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 保存审批流模型
 *
 * @author wangchuanhao
 * @date 2022/10/24 10:03 PM
 */
@Data
public class ModelDetailRSP {

    @ApiModelProperty("bpmn xml")
    private String bpmnXml;

}
