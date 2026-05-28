package cn.zswltech.mithras.dto.flow.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 保存审批流模型
 *
 * @author wangchuanhao
 * @date 2022/10/24 10:03 PM
 */
@Data
public class SaveModelREQ {

    @ApiModelProperty("bpmn xml")
    @NotBlank
    private String bpmnXml;

}
