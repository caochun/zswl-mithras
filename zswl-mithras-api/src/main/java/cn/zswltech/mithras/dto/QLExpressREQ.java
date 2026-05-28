package cn.zswltech.mithras.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
@Data
public class QLExpressREQ {
    private Boolean isExcelFormula;

    @NotBlank(message = "脚本不能为空")
    private String express;

    private List<Data> dataList;

    @lombok.Data
    public static class Data {
        private String key;
        private Object value;
    }
}
