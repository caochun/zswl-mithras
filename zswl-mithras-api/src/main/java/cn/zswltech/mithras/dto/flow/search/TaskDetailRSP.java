package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 任务详情
 *
 * @author wangchuanhao
 * @date 2022/8/1 5:02 PM
 */
@Data
public class TaskDetailRSP extends TaskListRSP {

    @ApiModelProperty("业务数据版本")
    private String businessVersion;

    @ApiModelProperty("审批操作栏目是否显示")
    private Boolean operateTabShowFlag;

    @ApiModelProperty("动态按钮列表")
    private List<DynamicButton> dynamicButtonList;

    @ApiModelProperty("抄送栏是否显示")
    private Boolean ccTabShowFlag;

    @ApiModelProperty("动态表单key列表")
    private List<String> dynamicFormKeyList;

    @ApiModelProperty("动态表单数据")
    private Map<String, Object> dynamicFormData;

    @ApiModelProperty("多级回退下拉框是否显示（是否在发起人的后一节点）")
    private Boolean multiBackOptionFlag;

    @ApiModelProperty("可退回节点列表（如果该节点有退回指定节点的按钮，就应该有该列表）")
    private List<ProcessNodeRSP> canBackNodeList;

    @ApiModelProperty("业务数据是否可编辑（当前登陆用户等于流程发起人且流程处于发起人节点时可编辑）")
    private Boolean canEditFlag;

    @ApiModelProperty("是否关注流程，1关注0不关注")
    private Integer attentionFlag;

    @ApiModelProperty("前端展示样式")
    private Integer uiVersion;

    @ApiModelProperty(value = "是否协同，用于展示文件上传按钮")
    private Boolean collaborateFlag;

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

        private int sort;

    }


    @ApiModelProperty(value = "抄送人是否只读")
    private Boolean ccTabReadOnlyFlag;

    @ApiModelProperty("抄送人")
    private List<Long> ccUerList;

    @ApiModelProperty("返回的提示信息")
    private String returnMsg;

}
