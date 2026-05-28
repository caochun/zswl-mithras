package cn.zswltech.mithras.dto.contract.mortgage;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 合同-抵押措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-抵押措施列表-返回体")
public class ContractMortgageListRSP extends ListBaseRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty("抵押合同编号")
    private String mortgageContractCode;

    @ApiModelProperty(value = "抵押物清单ID")
    private Long fileId;;

    @ApiModelProperty("资料名称")
    private String fileName;

    @ApiModelProperty("资料路径")
    private String filePath;

    /**
     * 关联合同编号
     */
    @ApiModelProperty(value = "关联合同编号信息")
    private List<String> relatContracts;


    @ApiModelProperty(value = "质押人类型")
    private String mortgageType;

    /**
     * 抵押人id，姓名，类型
     */
    @ApiModelProperty(value = "抵押人id")
    private List<Long> mortgageIds;

    /**
     * 抵押人id，姓名，类型
     */
    @ApiModelProperty(value = "抵押人id 姓名")
    private List<ClientInfo> mortgageInfo;

    /**
    * 抵押物描述
    */
    @ApiModelProperty(value = "抵押物描述")
    private String mortgageDescribe;

    @ApiModelProperty("是否评估，0-否，1-是")
    private Integer assess;

    @ApiModelProperty("评估日期，yyyy-MM-dd")
    private String assessDate;

    /**
     * 评估公司
     */
    @ApiModelProperty("评估公司")
    private String appraisalCompany;

    /**
     * 评估编号
     */
    @ApiModelProperty("评估编号")
    private String appraisalCode;

    @ApiModelProperty("是否最高额，0-否，1-是")
    private Integer highest;

    @ApiModelProperty("抵押物类型")
    private String mortgageItemType;

    @ApiModelProperty("抵押类型")
    private String contractMortgageType;

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
