package cn.zswltech.mithras.workflow.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author luyi
 */
@Data
@Builder
@TableName("rent_collection_month_detail")
public class RentCollectionMonthDetail extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = -9025392687670931311L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long collectionId;

    private Long pledgeId;

    private String financingType;

    private Long deptId;

    private Integer year;

    private Integer month;

    /**
     * 预备表id
     */
    private Long prepareId;

    /**
     * 客户id
     */
    private Long clientId;

    /**
     * 主办id
     */
    private Long sponsorId;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 期项
     */
    private Integer phase;

    /**
     * 租金支付日
     */
    private LocalDate repayDate;
    /**
     * 租金
     */
    private Long rent;
    /**
     * 本金
     */
    private Long principal;
    /**
     * 利息
     */
    private Long interest;
    /**
     * 银行开户账户名
     */
    private String bankAccountName;
    /**
     * 银行开户账户号
     */
    private String bankAccountNumber;
    /**
     * 开户行名称
     */
    private String bankName;
    /**
     * 最初的样子，用于比对
     */
    private String initialData;

    //


    @Override
    public void setMainId(Long id) {
        this.prepareId = id;
    }

    @Override
    public Long getMainId() {
        return this.prepareId;
    }
}
