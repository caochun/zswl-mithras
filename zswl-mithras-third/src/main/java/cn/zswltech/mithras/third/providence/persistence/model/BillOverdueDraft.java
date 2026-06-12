package cn.zswltech.mithras.third.providence.persistence.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/10/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("bill_overdue_draft")
public class BillOverdueDraft extends BillOverdue {
}
