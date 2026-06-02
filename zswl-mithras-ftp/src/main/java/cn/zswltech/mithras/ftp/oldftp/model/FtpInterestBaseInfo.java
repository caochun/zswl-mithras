package cn.zswltech.mithras.ftp.oldftp.model;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description FTP计息-基本信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ftp_interest_base_info")
public class FtpInterestBaseInfo extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 借据id
     */
    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 借据编号
     */
    @TableField("receipt_code")
    private String receiptCode;

    /**
     * 项目评审id
     */
    @TableField("proj_review_id")
    private Long projReviewId;

    /**
     * 项目名称
     */
    @TableField("proj_name")
    private String projName;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 业务部门id
     */
    @TableField("biz_dept_id")
    private Long bizDeptId;

    /**
     * 项目主办id
     */
    @TableField("sponsor_user_id")
    private Long sponsorUserId;

    /**
     * 最近一次更新日期
     */
    @TableField("last_update_date")
    private LocalDate lastUpdateDate;

    /**
     * 累计计息
     */
    @TableField("total_interest_amount")
    private Long totalInterestAmount;

    /**
     * FTP计息是否结束
     * {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("finish")
    private Integer finish;

    @TableField("last_cash_ftp")
    private Integer lastCashFtp;
}
