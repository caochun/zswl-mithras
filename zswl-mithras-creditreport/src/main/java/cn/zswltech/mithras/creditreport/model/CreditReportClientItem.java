package cn.zswltech.mithras.creditreport.model;
import cn.zswltech.mithras.creditreport.enums.CreditSearchStatusEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 征信报告客户表
 * @author vico
 * @date 2025-11-24
 */
@Data
public class CreditReportClientItem extends BaseModelWithLogicDelete implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 征信报告基本信息表id
    */
    @TableField("credit_report_base_info_id")
    private Long creditReportBaseInfoId;

    /**
    * 关联客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 客户名称
    */
    @TableField("client_name")
    private String clientName;

    /**
    * 统一社会信用代码
    */
    @TableField("csc_code")
    private String cscCode;

    /**
    * 中征码
    */
    @TableField("zhong_zheng_code")
    private String zhongZhengCode;

    /**
    * 查询目的，枚举值：贷前（保前）审查、贷后（在保）管理、贷中操作、关联查询
    */
    @TableField("select_goal")
    private String selectGoal;

    /**
     * {@link CreditSearchStatusEnum#name()}
     **/
    @TableField("select_status")
    private String selectStatus;

    /**
    * 失败原因编号
    */
    @TableField("select_error_code")
    private String selectErrorCode;

    /**
    * 失败原因
    */
    @TableField("select_error_reason")
    private String selectErrorReason;

    /**
    * 档案编号
    */
    @TableField("archive_id")
    private String archiveId;

    /**
    * 查询交易流水号
    */
    @TableField("serial_number")
    private String serialNumber;


    @Override
    public void setMainId(Long id) {
        this.creditReportBaseInfoId = id;
    }

    @Override
    public Long getMainId() {
        return this.creditReportBaseInfoId;
    }
}
