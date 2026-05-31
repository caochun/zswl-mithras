package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单任务表
 * @author 
 * @date 2024-01-16
 */
@Data
@ApiModel("黑灰名单任务表编辑-请求体")
public class BlackGrayWarehouseTaskModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
    * 机构code
    */
    @ApiModelProperty(value = "机构code")
    private String orgId;
    /**
    * 子任务-派发到的部门code
    */
    @ApiModelProperty(value = "子任务-派发到的部门code")
    private String subTaskDeptCode;
    /**
    * 数据时点，即用户提交任务时所在的月份
    */
    @ApiModelProperty(value = "数据时点，即用户提交任务时所在的月份")
    private String timePoint;
    /**
    * 定期任务报送截止时间
    */
    @ApiModelProperty(value = "定期任务报送截止时间")
    private Date deadline;
    /**
    * 定期任务是否超时
    */
    @ApiModelProperty(value = "定期任务是否超时")
    private Integer overtimeFlag;
    /**
    * 上传导入文件
    */
    @ApiModelProperty(value = "上传导入文件")
    private List<String> uploadFileList;
    /**
    * 关联交易任务类型 timed=定时生成任务， not_timed=非定时生成自由上报任务
    */
    @ApiModelProperty(value = "关联交易任务类型 timed=定时生成任务， not_timed=非定时生成自由上报任务")
    private String taskType;
    /**
    * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
    */
    @ApiModelProperty(value = "审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish")
    private Integer auditStatus;
    /**
    * 子任务-退回说明
    */
    @ApiModelProperty(value = "子任务-退回说明")
    private String retractSuggest;
    /**
    * 子任务-关闭标记
    */
    @ApiModelProperty(value = "子任务-关闭标记")
    private Integer isClosed;
    /**
    * 派发人退回标记
    */
    @ApiModelProperty(value = "派发人退回标记")
    private Integer isRetract;
    /**
    * 审批流任务id，冗余，便于查找audit_task记录
    */
    @ApiModelProperty(value = "审批流任务id，冗余，便于查找audit_task记录")
    private Long auditTaskId;
    /**
    * 审批流当前操作人，冗余，方便进行用户数据权限过滤
    */
    @ApiModelProperty(value = "审批流当前操作人，冗余，方便进行用户数据权限过滤")
    private String currentOperator;
    /**
    * 上一操作人，冗余，方便判断能否撤回
    */
    @ApiModelProperty(value = "上一操作人，冗余，方便判断能否撤回")
    private String preOperator;
    /**
    * 子任务-派发人
    */
    @ApiModelProperty(value = "子任务-派发人")
    private String assigner;
    /**
    * 子任务-指定接受角色
    */
    @ApiModelProperty(value = "子任务-指定接受角色")
    private String assignSubmitRole;
    /**
    * 子任务-指定接收处理人
    */
    @ApiModelProperty(value = "子任务-指定接收处理人")
    private String assignSubmitUser;
}
