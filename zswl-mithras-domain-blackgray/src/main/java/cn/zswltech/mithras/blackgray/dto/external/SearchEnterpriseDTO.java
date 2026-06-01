package cn.zswltech.mithras.blackgray.dto.external;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchEnterpriseDTO {

    private Boolean status;

    private Integer code;

    private String message;

    private PageInfo<EnterpriseDTO> data;

    @ApiModel
    @Data
    public static class EnterpriseDTO {

        @ApiModelProperty("企业名称")
        private String enterpriseName;

        @ApiModelProperty("统一社会信用代码")
        private String uniformCreditCode;

        private String businessRegNo;

        private String legalRepr;

        @ApiModelProperty("企业名称搜索高亮")
        private String enterpriseNameFlag;

        private String establishDate;

        private String legalReprDesc;

        private String regAddr;

        private String enterpriseKey;

        private String status;

        private String changeTime;

        private String refCode;

        private String sourceFlag;

        private String soureDesc;

        private String organizationCode;
    }
}
