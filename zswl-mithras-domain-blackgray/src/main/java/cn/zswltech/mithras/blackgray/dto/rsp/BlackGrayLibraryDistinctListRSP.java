package cn.zswltech.mithras.blackgray.dto.rsp;

import cn.zswltech.mithras.blackgray.excel.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库按企业汇总列表-返回体")
public class BlackGrayLibraryDistinctListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    @Excel(name = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    @Excel(name = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    @ApiModelProperty(value = "黑灰类型 blackGrayTypeEnum BLACK_LIST=黑, GRAY_LIST=灰")
    @Excel(name = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "是否上报金控 0 不上报，1上报")
    private Integer reportFlag;

}
