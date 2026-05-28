package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("新的批量提交转移编辑用户负责的客户-请求体")
public class SponsorClientModifyNewBatchREQ implements Serializable {

        @ApiModelProperty("客户请求体列表")
        private List<SponsorClientModifyNewREQ> sponsorClientModifyNewBatch;

}
