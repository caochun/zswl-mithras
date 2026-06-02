package cn.zswltech.mithras.ftp.model;

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
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ftp_interest_detail_record")
public class FtpInterestDetailRecord extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * FTP计息基本信息id
     */
    @TableField("ftp_interest_id")
    private Long ftpInterestId;

    /**
     * FTP计息表条目展示文本（通常是日期，每年的1.1会使用“期初余额”替代日期）
     */
    @TableField("item_text")
    private String itemText;

    /**
     * 计息日期
     */
    @TableField("interest_date")
    private LocalDate interestDate;

    /**
     * 现金支出
     */
    @TableField("cash_out")
    private Long cashOut;

    /**
     * 现金收入
     */
    @TableField("cash_in")
    private Long cashIn;

    /**
     * 资金占用
     */
    @TableField("cash_occupy")
    private Long cashOccupy;

    /**
     * 现金FTP
     */
    @TableField("cash_ftp")
    private Integer cashFtp;

    /**
     * 资金计息
     */
    @TableField("cash_interest")
    private Long cashInterest;

    /**
     * 票据支出
     */
    @TableField("bill_out")
    private Long billOut;

    /**
     * 票据收入
     */
    @TableField("bill_in")
    private Long billIn;

    /**
     * 票据占用
     */
    @TableField("bill_occupy")
    private Long billOccupy;

    /**
     * 票据FTP
     */
    @TableField("bill_ftp")
    private Integer billFtp;

    /**
     * 票据计息
     */
    @TableField("bill_interest")
    private Long billInterest;

    /**
     * 是否逾期
     * {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("is_overdue")
    private Integer isOverdue;

    /**
     * 当年累计计息
     */
    @TableField("total_interest_this_year")
    private Long totalInterestThisYear;

    /**
     * 累计逾期金额
     */
    @TableField("total_overdue")
    private Long totalOverdue;

    /**
     * 融资实际还款金额
     */
    @TableField("financing_repay")
    private Long financingRepay;

    /**
     * 自有资金占用余额
     */
    @TableField("own_occupy_balance")
    private Long ownOccupyBalance;

    /**
     * 轧差标志 默认为0， 但是重算已经确认的FTP的数据时，该标志置为1
     */
    @TableField("rolling_difference_mark")
    private Integer rollingDifferenceMark;

    /**
     * FTP价格逾期调整
     */
    @TableField("ftp_overdue_adjust")
    private Integer ftpOverdueAdjust;

    /**
     * 备注说明
     */
    @TableField("remark")
    private String remark;
}
