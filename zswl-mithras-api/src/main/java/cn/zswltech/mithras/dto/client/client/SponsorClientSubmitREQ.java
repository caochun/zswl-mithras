package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("提交转移指定用户负责的客户-请求体")
public class SponsorClientSubmitREQ implements Serializable {

    @Valid
    @NotEmpty(message = "您还未选择要移除的客户")
    @ApiModelProperty("移交客户列表")
    private List<TransferClient> transferClientList;

    @NotNull
    @ApiModelProperty("移至部门id")
    private Long toDeptId;

    @NotNull
    @ApiModelProperty("移至经理id")
    private Long toSponsorId;

    @ApiModelProperty("移至协办经理id列表")
    private List<Long> toCosponsorIds;

    @NotNull
    @ApiModelProperty("正式移交日期")
    private LocalDate transferDate;


    @Data
    public static class TransferClient {
        @NotNull
        private Long clientId;

        private String clientCode;

        @NotBlank
        private String clientName;

        @NotBlank
        private String clientType;

        @ApiModelProperty("当前所属部门id")
        private Long belongDeptId;
        @NotNull
        @ApiModelProperty("当前所属经理id")
        private Long belongSponsorId;


    }

}
