package cn.zswltech.mithras.dto.file.ext;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileListREQCreditSearchExt {

    @ApiModelProperty("查询子类型：BUSINESS_LICENSE 营业执照复印件  LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_ENTERPRISE 法人身份证复印件(正面) LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_ENTERPRISE 法人身份证复印件(反面) CREDIT_LETTER 征信授权书" +
            "LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_HANDLER 法人身份证复印件(正面) LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_HANDLER 法人身份证复印件(反面)")
    private String queryType;

    private Long clientId;
}
