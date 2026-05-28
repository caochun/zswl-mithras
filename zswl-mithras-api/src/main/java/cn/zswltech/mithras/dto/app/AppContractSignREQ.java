package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
public class AppContractSignREQ {


    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("合同id")
    private Long contractId;

    private List<Object> fileInfos;

    @Data
    public static class Object {
        @ApiModelProperty(value = "文件id")
        private Long fileId;

        @ApiModelProperty(value = "业务类型")
        private String businessType;

        @ApiModelProperty(value = "资料类型")
        private String materialsType;

        @ApiModelProperty(value = "资料子类型")
        private String materialSubType;
    }

}
