package cn.zswltech.mithras.dto.incomesharing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author yupengfei
 * @date 2024/6/12 13:19
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSharingListBO {

    /**
     * 借据id
     */
    private Long receiptId;

    /**
     * 借据编号
     */
    private String receiptCode;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 项目名称
     */
    private String projName;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 合同状态
     */
    private String contractStatus;

    /**
     * 收入分摊方式
     */
    private String incomeConfirmType;

    /**
     * 收入日期
     */
    private LocalDate incomeDate;

    /**
     * 含税确认收入
     */
    private Long income;

    /**
     * 不含税收入
     */
    private Long incomeWithoutTax;

    /**
     * 收入是否确认。0-否，1-是
     */
    private Integer isConfirmed;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;
}
