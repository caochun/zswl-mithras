package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单任务表
 * @author 
 * @date 2024-01-16
 */
@Data
@ApiModel("黑灰名单任务表新增-请求体")
public class BlackGrayWarehouseTaskAddREQ {

    /**
    * 任务编号，唯一索引，保存时生成，机构缩写8位年月日至少3位自增数，如gdrf20230329001，理论位数应该是15位，考虑到后三位自增数极端情况下不一定够用，所以预留32位
    */
    @ApiModelProperty(value = "任务编号")
    private String taskNum;

    /**
    * 上层任务id
    */
    @ApiModelProperty(value = "上层任务id")
    private Long parentTaskId;

    /**
    * 机构code
    */
    @ApiModelProperty(value = "机构code")
    private String orgCode;

    /**
     * 来源
     **/
    @ApiModelProperty(name = "业务来源 BlackGraySourceEnum")
    @NotNull(message = "来源不能为空")
    private String businessSource;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    /**
    * 定期任务是否超时
    */
    @ApiModelProperty(value = "定期任务是否超时，0 不超时，1超时")
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
    @NotNull
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

    /**
    * 最后提交时间
    */
    @ApiModelProperty(value = "最后提交时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /**
    * 子任务-派发时间
    */
    @ApiModelProperty(value = "子任务-派发时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date assignTime;

    /**
    * 最后审批时间
    */
    @ApiModelProperty(value = "最后审批时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /**
    * 1=页面录入，2=openapi对接, 3=系统生成
    */
    @ApiModelProperty(value = "1=页面录入，2=openapi对接")
    @NotNull
    private Integer source;

    /**
    * 创建人
    */
    @ApiModelProperty(value = "创建人")
    private String createdBy;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    /**
    * 更新人
    */
    @ApiModelProperty(value = "更新人")
    private String updatedBy;

    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtUpdate;

}
