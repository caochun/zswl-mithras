package cn.zswltech.mithras.dto.flow.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/18 17:27
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStampDutyFormRSP {

    private List<StampDutyData> dutyFormRSPList;

    @Data
    @Builder
    public static class StampDutyData {
        /**
         * 借据编号
         */
        private String receiptCode;

        /**
         * 印花税
         */
        private Long stampDuty;
    }
}
