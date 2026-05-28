package cn.zswltech.mithras.dto.flow.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 配置
 *
 * @author wangchuanhao
 * @date 2022/11/7 11:28 AM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelConfigRSP {

//    @ApiModelProperty("各类型节点的配置")
//    private Map<String, Object> nodeConfigMap;

    @ApiModelProperty("空开始事件")
    private NodeConfig startNoneEvent;

    @ApiModelProperty("用户任务")
    private NodeConfig userTask;

    @ApiModelProperty("顺序流")
    private NodeConfig sequenceFlow;

    @ApiModelProperty("空结束事件")
    private NodeConfig endNoneEvent;

    @ApiModelProperty("排他网关")
    private NodeConfig exclusiveGateway;

    /**
     * 节点配置
     * 空开始事件 startNoneEvent
     * 用户任务 userTask
     * 顺序流 sequenceFlow
     * 空结束事件 endNoneEvent
     * 排他网关 exclusiveGateway
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("节点配置")
    public static class NodeConfig {

        @ApiModelProperty("字段列表")
        private List<FieldDefinition> fieldList = new ArrayList<>();

    }

    @ApiModel("模型字段定义")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldDefinition {

        @ApiModelProperty("字段名")
        private String fieldName;

        @ApiModelProperty("字段key")
        private String fieldKey;

        @ApiModelProperty("字段类型：INPUT：输入框;SELECT：下拉框")
        private String fieldType;

        @ApiModelProperty("字段显示依赖")
        private List<DependField> dependList;

        @ApiModelProperty("下拉框配置")
        private SelectConfig selectConfig;

    }

    @ApiModel("下拉框配置")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SelectConfig {

        @ApiModelProperty("下拉数据来源：OPTION：内置;API：接口;BOOLEAN：是否（true、false）;CUSTOM：前端写死")
        private String sourceType;

        @ApiModelProperty("下拉枚举列表")
        private List<SelectOption> optionList;

        @ApiModelProperty("是否单选")
        private Boolean singleFlag;

        @ApiModelProperty("接口url")
        private String apiUrl;

    }

    @ApiModel("下拉框配置")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SelectOption {

        @ApiModelProperty("展示文本")
        private String label;

        @ApiModelProperty("值")
        private String value;

    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("字段依赖")
    public static class DependField {

        @ApiModelProperty("字段名")
        private String fieldName;

        @ApiModelProperty("字段值")
        private Object fieldValue;

    }

}
