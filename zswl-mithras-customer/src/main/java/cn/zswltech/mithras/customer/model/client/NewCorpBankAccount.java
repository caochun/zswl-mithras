package cn.zswltech.mithras.customer.model.client;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("new_corp_bank_account")
public class NewCorpBankAccount extends CorpBankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

//    /**
//     * 权限级别, 管护权为3，申办权为2，查看权为1
//     */
//    @TableField("level")
//    private Integer level;
//
//    /**
//     * 客户状态
//     */
//    @TableField("client_status")
//    private String clientStatus;
//
//    /**
//     * 是否已释放
//     */
//    @TableField(value = "is_released")
//    private Integer isReleased;
}
