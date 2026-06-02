package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description 合同明细-租赁物
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_lease_item")
public class ContractLeaseItem extends BaseModel implements IEntity {
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
     * 序号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 种类
     */
    @TableField("category")
    private String category;

    /**
     * 唯一识别号类型
     */
    @TableField("unique_identify_code_type")
    private String uniqueIdentifyCodeType;

    /**
     * 唯一识别号
     */
    @TableField("unique_identify_code")
    private String uniqueIdentifyCode;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 供应商
     */
    @TableField("supplier")
    private String supplier;

    /**
     * 数量
     */
    @TableField("quantity")
    private String quantity;

    /**
     * 计量单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 购置日期
     */
    @TableField("purchase_date")
    private String purchaseDate;

    /**
     * 账面原值
     */
    @TableField("original_book_value")
    private Long originalBookValue;

    /**
     * 账面净值
     */
    @TableField("original_book_net_value")
    private Long originalBookNetValue;

    /**
     * 评估原值
     */
    @TableField("assessed_value")
    private Long assessedValue;

    /**
     * 评估净值
     */
    @TableField("assessed_net_value")
    private Long assessedNetValue;

    /**
     * 发票号
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 存放地点
     */
    @TableField("storage_place")
    private String storagePlace;

    @TableField("row_data")
    private String rowData;

    @TableField(value = "lease_item_row_data_id", updateStrategy = FieldStrategy.IGNORED)
    private Long leaseItemRowDataId;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
