package cn.zswltech.mithras.blackgray.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @description 黑灰名单任务表
 * @author 
 * @date 2024-01-16
 */
@Data
@TableName(value = "black_gray_warehouse_task")
public class BlackGrayWarehouseTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 任务编号，唯一索引，保存时生成，机构缩写8位年月日至少3位自增数，如gdrf20230329001，理论位数应该是15位，考虑到后三位自增数极端情况下不一定够用，所以预留32位
    */
    @TableField("task_num")
    private String taskNum;

    @TableField("task_num_sequence")
    private String taskNumSequence;
    /**
    * 上层任务id
    */
    @TableField("parent_task_id")
    private Long parentTaskId;
    /**
    * 机构code
    */
    @TableField("org_code")
    private String orgCode;

    /**
     * 业务类型
     **/
    @TableField("business_source")
    private String businessSource;
    /**
    * 子任务-派发到的部门code
    */
    @TableField("sub_task_dept_code")
    private String subTaskDeptCode;
    /**
    * 数据时点，即用户提交任务时所在的月份
    */
    @TableField("time_point")
    private String timePoint;
    /**
    * 定期任务报送截止时间
    */
    @TableField("deadline")
    private Date deadline;
    /**
    * 定期任务是否超时
    */
    @TableField("overtime_flag")
    private Integer overtimeFlag;
    /**
    * 上传导入文件
    */
    @TableField("upload_file")
    private String uploadFile;
    /**
    * 关联交易任务类型 timed=定时生成任务， not_timed=非定时生成自由上报任务
    */
    @TableField("task_type")
    private String taskType;
    /**
    * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
    */
    @TableField("audit_status")
    private Integer auditStatus;
    /**
    * 子任务-退回说明
    */
    @TableField("retract_suggest")
    private String retractSuggest;
    /**
    * 子任务-关闭标记
    */
    @TableField("is_closed")
    private Integer isClosed;
    /**
    * 派发人退回标记
    */
    @TableField("is_retract")
    private Integer isRetract;
    /**
    * 审批流任务id，冗余，便于查找audit_task记录
    */
    @TableField("audit_task_id")
    private Long auditTaskId;
    /**
    * 审批流当前操作人，冗余，方便进行用户数据权限过滤
    */
    @TableField("current_operator")
    private String currentOperator;
    /**
    * 上一操作人，冗余，方便判断能否撤回
    */
    @TableField("pre_operator")
    private String preOperator;
    /**
    * 子任务-派发人
    */
    @TableField("assigner")
    private String assigner;
    /**
    * 子任务-指定接受角色
    */
    @TableField("assign_submit_role")
    private String assignSubmitRole;
    /**
    * 子任务-指定接收处理人
    */
    @TableField("assign_submit_user")
    private String assignSubmitUser;
    /**
    * 最后提交时间
    */
    @TableField("submit_time")
    private Date submitTime;
    /**
    * 子任务-派发时间
    */
    @TableField("assign_time")
    private Date assignTime;
    /**
    * 最后审批时间
    */
    @TableField("audit_time")
    private Date auditTime;
    /**
    * 1=页面录入，2=openapi对接
    */
    @TableField("source")
    private Integer source;
    /**
    * 创建人
    */
    @TableField("created_by")
    private String createdBy;
    /**
    * 创建时间
    */
    @TableField("gmt_create")
    private Date gmtCreate;
    /**
    * 更新人
    */
    @TableField("updated_by")
    private String updatedBy;
    /**
    * 更新时间
    */
    @TableField("gmt_update")
    private Date gmtUpdate;
    @TableField(exist = false)
    private String latestMsg;

    /**
     * 是否填充了所属集团信息
     */
    @TableField("is_supply_group_info")
    private Boolean isSupplyGroupInfo;
}
