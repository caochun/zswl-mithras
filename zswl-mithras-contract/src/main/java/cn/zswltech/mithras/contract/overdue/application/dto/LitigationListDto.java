package cn.zswltech.mithras.contract.overdue.application.dto;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 10:50
 */
@Data
public class LitigationListDto {

    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "诉讼登记编号")
    private String code;
    @ApiModelProperty(value = "合同编号")
    private String contractCodes;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "诉讼登记状态")
    private String status;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "创建人id")
    private Long createBy;
    @ApiModelProperty(value = "创建人")
    private String createByName;


    public void setContractCodes(String contractCodes) {
        if (ObjectUtil.isNotEmpty(contractCodes)) {
            List<String> contractCodesList = JSON.parseArray(contractCodes, String.class);
            this.contractCodes = String.join(",", contractCodesList);
        }
    }
}
