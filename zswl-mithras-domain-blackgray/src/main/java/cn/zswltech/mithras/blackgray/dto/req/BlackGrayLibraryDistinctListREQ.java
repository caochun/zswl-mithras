package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("黑灰名单库列表按企业汇总-请求体")
public class BlackGrayLibraryDistinctListREQ extends PageReq {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    @ApiModelProperty(value = "业务类型 blackGrayBusinessTypeEnum字典")
    private String businessType;

    @ApiModelProperty(value = "选中企业卡片的orgCode，全量则为ALL")
    private String cardOrgCode;

    @ApiModelProperty(value = "导出时勾选项的信用代码集合")
    private List<Long> idList;
}
