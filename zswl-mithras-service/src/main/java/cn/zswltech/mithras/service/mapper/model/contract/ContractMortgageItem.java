package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description 抵押-抵押物明细表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_mortgage_item")
public class ContractMortgageItem extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("contract_id")
    private Long contractId;

    /**
     * 抵押措施ID
     */
    @TableField("mortgage_id")
    private Long mortgageId;

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
     * 唯一识别号
     */
    @TableField("unique_identify_code")
    private String uniqueIdentifyCode;

    /**
     * 唯一识别号类型
     */
    @TableField("unique_identify_code_type")
    private String uniqueIdentifyCodeType;

    /**
     * 设备名称
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

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
