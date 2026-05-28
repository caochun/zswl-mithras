package cn.zswltech.mithras.dto.rating.decision;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DecisionExecuteEclResult {

    @ApiModelProperty("业务流水号")
    private String traceId;

    private Map<String, OutputValue> outputMap;

    /**
     * elc
     */
    private BigDecimal ecl;

    @Data
    public static class OutputValue {
        private String flowNodeId;
        private String code;
        private String value;
        private String name;
        private Map<String, String> paramsValueMap;
    }



}
