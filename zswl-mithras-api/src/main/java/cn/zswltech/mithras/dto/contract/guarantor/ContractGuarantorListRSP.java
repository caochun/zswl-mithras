package cn.zswltech.mithras.dto.contract.guarantor;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 合同-担保措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-担保措施列表-返回体")
public class ContractGuarantorListRSP extends ListBaseRSP {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty("保证合同编号")
    private String guarantorContractCode;

    /**
     * 关联合同编号
     */
    @ApiModelProperty(value = "关联合同编号信息")
    private List<String> relatContracts;

    @ApiModelProperty(value = "担保人id")
    private List<Long> guarantorIds;

    /**
     * 担保人类型id，姓名
     */
    @ApiModelProperty(value = "担保人类型id，姓名，类型")
    private List<ClientInfo> guarantorInfo;

    @ApiModelProperty(value = "担保人类型")
    private String guarantorType;

    /**
     * 担保方式-连带责任担保、一般担保
     */
    @ApiModelProperty(value = "担保方式-连带责任担保、一般担保")
    private String guaranteeMethod;

    /**
     * 是否上报征信 0不上报，1上报
     */
    @ApiModelProperty(value = "是否上报征信 0不上报，1上报")
    private Integer isReport;

    @ApiModelProperty("联保标志，SINGLE-单人保证，MULTIPLE_SEPARATE-多人分保，JOINT-联保")
    private String jointGuaranteeMark;

    @ApiModelProperty("担保金额，当联保标志为单人保证或者联保时填充该字段")
    private Long amountSingle;

    @ApiModelProperty("担保金额，当联保标志为多人分保时填充该字段")
    private List<GuaranteeAmountMultipleInfo> amountMultiple;

    @ApiModelProperty("指定联系人id")
    private Long contactId;

    @ApiModelProperty("指定联系人姓名")
    private String contactName;

    /**
     * 决议类型：股东会决议、董事会决议、股东决定、执行董事决定
     */
    @ApiModelProperty(value = "决议类型")
    private String resolutionType;

    /**
     * 章程文件id
     */
    private List<Long> constitutionFileList;
    /**
     * 决议文件
     */
    @ApiModelProperty(value = "决议文件")
    private List<Long> resolutionFileId;


}
