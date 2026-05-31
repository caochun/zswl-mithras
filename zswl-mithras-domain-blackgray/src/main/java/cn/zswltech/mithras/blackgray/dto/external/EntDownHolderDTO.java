package cn.zswltech.mithras.blackgray.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class EntDownHolderDTO {
    private String enterprise_info;
    private String invest_name;
    private BigDecimal capital_ratio;
    private String level;
    private List<DownChainNode> path_chain = new ArrayList<>();

    // 内部对path_chain解析后的多级穿透下级企业名称
    private List<String> innerDownCompanyNames = new ArrayList<>();

    @Data
    public static class DownChainNode {
        private Integer tree_no;
        private Integer enterprise_level;
        private String path;
        private BigDecimal percent_ratio;
        private BigDecimal percent_ratio_not_dilution;
    }
}
