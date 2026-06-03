package cn.zswltech.mithras.basedata.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/8/9
 * @description
 */
@Data
@TableName("account_balance_category_dictionary")
public class AccountBalanceCategoryDictionary {
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("account_no")
    private String accountNo;

    @TableField("account_name")
    private String accountName;
}
