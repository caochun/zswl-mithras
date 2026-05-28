package cn.zswltech.mithras.dto.flow.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yupengfei
 * @date 2024/5/18 18:03
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentUpdateIrrRSP {

    /**
     * 最低irr
     */
    private Integer lowestIrr;
}
