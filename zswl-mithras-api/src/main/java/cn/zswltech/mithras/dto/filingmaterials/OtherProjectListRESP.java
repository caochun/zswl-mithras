package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("其他资料归档获取项目-返回体")
public class OtherProjectListRESP {
    private String projCode;
    private String projName;
    private String clientName;
    private Long projReviewId;

}
