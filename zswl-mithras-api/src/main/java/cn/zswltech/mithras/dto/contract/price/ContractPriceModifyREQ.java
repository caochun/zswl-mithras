package cn.zswltech.mithras.dto.contract.price;

import cn.zswltech.mithras.annotation.MainIdExtract;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ContractPriceModifyREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/8/16 2:04 下午
 * @Version 1.0
 **/
@Data
@ApiModel("合同报价方案更新-请求体")
@MainIdExtract(expression = "aocPriceModifyREQ.contractId,factoringPriceModifyREQ.contractId,leasePriceModifyREQ.contractId")
public class ContractPriceModifyREQ {

    @ApiModelProperty("租赁报价方案请求体")
    private ContractLeasePriceModifyREQ leasePriceModifyREQ;

    @ApiModelProperty("债权转让请求体")
    private ContractAocPriceModifyREQ aocPriceModifyREQ;

    @ApiModelProperty("保理方案请求体")
    private ContractFactoringPriceModifyREQ factoringPriceModifyREQ;

    public Long getContractId() {
        if(leasePriceModifyREQ != null){
            return leasePriceModifyREQ.getContractId();
        }
        if(aocPriceModifyREQ != null){
            return aocPriceModifyREQ.getContractId();
        }
        if(factoringPriceModifyREQ != null){
            return factoringPriceModifyREQ.getContractId();
        }
        return null;
    }

}
