package cn.zswltech.mithras.dto.contract.text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/11/18 14:56
 * @description
 */
@Data
@ApiModel(value = "合同文本管理-未签合同详情响应参数")
public class ContractTextManageUnSignDetailRSP {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "资料名称")
    private String materialName;

    @ApiModelProperty(value = "文本签约方式")
    private String textSigningWay;

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "文件类型")
    private String modelType;

    @ApiModelProperty(value = "文本签约状态 ContractSignStatusEnum#name")
    private String textSigningStatus;

    @ApiModelProperty(value = "推送时间")
    private String pushTime;

    @ApiModelProperty(value = "签约方列表")
    private List<Signer> signerList;

    @ApiModelProperty(value = "是否可批量用印")
    private Boolean isBatchPrint;

    @JsonIgnore
    private int sort;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Signer {

        @ApiModelProperty(value = "id")
        private Long id;

        @ApiModelProperty(value = "签约方id")
        private Long signerId;

        @ApiModelProperty(value = "签约方名称")
        private String signerName;

        @ApiModelProperty(value = "签约方式")
        private String signingWay;

        @ApiModelProperty(value = "签约状态")
        private String signingStatus;

        @ApiModelProperty(value = "签约完成时间")
        private String signingCompleteTime;

        @ApiModelProperty(value = "实名认证状态")
        private String realNameAuthStatus;
    }
}
