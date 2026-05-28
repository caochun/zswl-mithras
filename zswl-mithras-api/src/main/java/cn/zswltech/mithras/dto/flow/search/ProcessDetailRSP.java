package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 流程详情返回值
 *
 * @author wangchuanhao
 * @date 2022/8/1 5:02 PM
 */
@Data
public class ProcessDetailRSP extends ProcessListRSP {

    @ApiModelProperty("业务数据版本")
    private String businessVersion;

    @ApiModelProperty("审批操作栏目是否显示")
    private Boolean operateTabShowFlag;

    @ApiModelProperty("动态按钮列表")
    private List<DynamicButton> dynamicButtonList;

    @ApiModelProperty("抄送栏是否显示")
    private Boolean ccTabShowFlag;

    @ApiModelProperty("审批快照按钮是否显示")
    private Boolean snapshotButtonShowFlag;

    @ApiModelProperty("动态表单key列表（可以不关注，理论上流程详情页不会有动态表单展示）")
    private List<String> dynamicFormKeyList;

    @ApiModelProperty("动态表单数据（可以不关注，理论上流程详情页不会有动态表单展示）")
    private Map<String, Object> dynamicFormData;

    @ApiModelProperty("业务数据是否可编辑（当前登陆用户等于流程发起人且流程处于发起人节点时可编辑）")
    private Boolean canEditFlag;

    @ApiModelProperty("是否关注流程，1关注0不关注")
    private Integer attentionFlag;

    @ApiModelProperty("前端展示样式")
    private Integer uiVersion;

    @Data
    @Builder
    @AllArgsConstructor
    @Getter
    public static class DynamicButton {

        @ApiModelProperty("按钮key")
        private String buttonKey;

        @ApiModelProperty("按钮名称")
        private String buttonName;

        @ApiModelProperty("是否可点击")
        private Boolean clickFlag;

    }

}
