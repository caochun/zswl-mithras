package cn.zswltech.mithras.dto.contract.pledge;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 合同-质押措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-质押措施列表-返回体")
public class ContractPledgeListRSP extends ListBaseRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 所属合同id
     */
    @ApiModelProperty(value = "所属合同id")
    private Long contractId;

    @ApiModelProperty("质押合同编号")
    private String pledgeContractCode;

    /**
     * 关联合同编号
     */
    @ApiModelProperty(value = "关联合同编号信息")
    private List<String> relatContracts;

    /**
    * 所属合同编号
    */
    @ApiModelProperty(value = "所属合同编号")
    private String contractCode;

    @ApiModelProperty(value = "质押人类型")
    private String pledgeType;

    /**
     * 质押人id
     */
    @ApiModelProperty(value = "质押人id")
    private List<Long> pledgeIds;

    /**
     * 质押人姓名
     */
    @ApiModelProperty(value = "质押人姓名")
    private List<ClientInfo> pledgeInfo;


    /**
     * 质押物描述
     */
    @ApiModelProperty(value = "质押物描述")
    private String pledgeDescribe;

    @ApiModelProperty("是否最高额担保，0-否，1-是")
    private Integer highest;

    @ApiModelProperty("质押物清单文件名称")
    private String fileName;

    @ApiModelProperty("质押物清单文件id")
    private Long fileId;

    @ApiModelProperty("质押类型")
    private String contractPledgeType;

    /*@ApiModelProperty("抵质押文件名称")
    private List<String> mortgagePledgeFileNames;

    @ApiModelProperty("抵质押文件id")
    private List<Long> mortgagePledgeFileIds;*/

    @ApiModelProperty("抵质押文件")
    private List<MortgagePledgeObj> mortgagePledgeObjList;

    @Data
    public static class MortgagePledgeObj {
        @ApiModelProperty(value = "抵押物清单ID")
        private Long fileId;;

        @ApiModelProperty("资料名称")
        private String fileName;

        @ApiModelProperty("资料路径")
        private String filePath;
    }

}
