package cn.zswltech.mithras.collection.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 票据管理表
 * @author vico
 * @date 2023-06-05
 */
@Data
public class BillManagement extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 票据id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 管理收付款主表id
    */
    @TableField("main_id")
    private Long mainId;

    /**
    * 票据类型 收款/付款
    */
    @TableField("bill_type")
    private String billType;

    /**
    * 票据code
    */
    @TableField("bill_code")
    private String billCode;

    /**
    * 票据金额
    */
    @TableField("bill_amount")
    private Long billAmount;

    /**
     * 票据买入价
     */
    @TableField("bill_buy_rate")
    private Long billBuyRate;

    /**
     * 票据买入价类型,0其他，1，同项目FTP
     */
    @TableField("bill_buy_rate_type")
    private Integer billBuyRateType;

    /**
    * 票据到期日期
    */
    @TableField("bill_expire_date")
    private LocalDate billExpireDate;

}
