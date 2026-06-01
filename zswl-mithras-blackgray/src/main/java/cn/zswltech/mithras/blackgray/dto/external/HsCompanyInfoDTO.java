package cn.zswltech.mithras.blackgray.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class HsCompanyInfoDTO {
    private String business_major;
    private String company_name;
    private String company_type_name;
    private String credit_code;
    private String enterprise_code;
    private Integer legal_person_type;
    private String legal_person_repr;
    private String reg_addr;
    private BigDecimal reg_capital;
    private String organization_code;
    private String state;
    private String state_code;
    private String area_code;
}
