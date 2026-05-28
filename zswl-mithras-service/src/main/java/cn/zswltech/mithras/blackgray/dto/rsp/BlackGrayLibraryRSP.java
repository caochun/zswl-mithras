package cn.zswltech.mithras.blackgray.dto.rsp;

import cn.zswltech.mithras.blackgray.excel.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库按企业汇总列表-返回体")
public class BlackGrayLibraryRSP {

    @ApiModelProperty(value = "id")
    private Long id;

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

    @ApiModelProperty(value = "黑灰类型 blackGrayTypeEnum BLACK_LIST=黑, GRAY_LIST=灰")
    @Excel(name = "黑灰标识")
    private String blackGrayType;

    /**
     * 申请原因类型
     */
    @ApiModelProperty(name = "申请原因类型")
    private List<String> applyReasonType;
    @ApiModelProperty(name = "申请原因名称")
    private List<String> applyReasonNames;

    /**
     * 申请原因 = 入库原因
     */
    @ApiModelProperty(name = "手工申请原因")
    private String applyReason;

    /**
     * 入库时间
     */
    @ApiModelProperty(name = "warehouse_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate warehouseTime;

}
