package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description 合同抵扣租金信息
 */
@Data
@TableName("contract_deduct_rent_info")
public class ContractDeductRentInfo extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 所属合同退抵信息Id
     */
    @TableField("retreat_info_id")
    private Long retreatInfoId;


    /**
     * 现金流编号
     */
    @TableField("code")
    private String code;

    /**
     * 日期
     */
    @TableField("plan_collection_date")
    private LocalDate planCollectionDate;

    /**
     * 期项
     */
    @TableField("phase")
    private Integer phase;

    /**
     * 租金
     */
    @TableField("plan_collection_amount")
    private Long planCollectionAmount;

    /**
     * 本金
     */
    @TableField("principal")
    private Long principal;

    /**
     * 利息
     */
    @TableField("interest")
    private Long interest;

    /**
     * 已收租金
     */
    @TableField("collection_amount")
    private Long collectionAmount;

    /**
     * 未收租金
     */
    @TableField("rest_amount")
    private Long restAmount;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
