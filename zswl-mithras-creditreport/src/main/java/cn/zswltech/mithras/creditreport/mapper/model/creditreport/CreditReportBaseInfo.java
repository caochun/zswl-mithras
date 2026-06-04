package cn.zswltech.mithras.creditreport.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 征信报告基本信息表
 * @author vico
 * @date 2025-11-24
 */
@Data
public class CreditReportBaseInfo extends BaseModelWithLogicDelete implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 查询编号
    */
    @TableField("credit_code")
    private String creditCode;

    /**
    * 申请部门
    */
    @TableField("apply_org")
    private Long applyOrg;

    /**
    * 申请状态，同流程审批状态，枚举值：未提交、审批中、审批通过、审批拒绝、已关闭
    */
    @TableField("apply_status")
    private String applyStatus;

    /**
    * 申请通过时间
    */
    @TableField("apply_time")
    private LocalDateTime applyTime;

    /**
    * 查询状态
    */
    @TableField("select_status")
    private String selectStatus;

    /**
    * 查询完成时间
    */
    @TableField("select_time")
    private LocalDateTime selectTime;

    /**
    * 查询版本，默认展示为企业信用报告(授信机构版)
    */
    @TableField("select_version")
    private String selectVersion;

    /**
    * 信用报告封装格式，默认展示为“html格式”
    */
    @TableField("report_format")
    private String reportFormat;

    /**
    * 关联项目编号
    */
    @TableField("proj_code")
    private String projCode;

    /**
    * 关联项目名称
    */
    @TableField("proj_name")
    private String projName;

    /**
    * 关联项目id
    */
    @TableField("proj_id")
    private Long projId;

    /**
    * proj_id的数据类型
    */
    @TableField("proj_id_data_type")
    private String projIdDataType;

    /**
    * 授信开始时间
    */
    @TableField("authorization_began_date")
    private LocalDate authorizationBeganDate;

    /**
    * 授信结束时间
    */
    @TableField("authorization_end_date")
    private LocalDate authorizationEndDate;


    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
