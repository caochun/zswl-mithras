package cn.zswltech.mithras.creditreport.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 征信报送表基础结构
 *
 * @author ylzhang5
 * @date 2025/12/04
 */
@Data
@Accessors(chain = true)
public class CreditBaseModel {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务主键
     * client id
     */
    @TableField("business_key")
    private String businessKey;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 还款id
     */
    @TableField("payment_id")
    private Long paymentId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public Set<String> ignoreCompareFieldNames() {
        return new HashSet<>(Arrays.asList("id", "businessKey", "contractId", "createTime", "updateTime"));
    }

}
