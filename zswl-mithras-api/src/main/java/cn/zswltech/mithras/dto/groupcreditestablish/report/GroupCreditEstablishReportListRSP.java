package cn.zswltech.mithras.dto.groupcreditestablish.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangchuanhao
 * @date 2022/9/21
 * @description
 */
@Data
@ApiModel("报告清单列表-返回体")
public class GroupCreditEstablishReportListRSP {
    @ApiModelProperty("报告文件id")
    private Long id;
    @ApiModelProperty("报告名称")
    private String typeName;
    @ApiModelProperty("材料类型")
    private String materialsType;
    @ApiModelProperty("文档名称")
    private String fileName;
    @ApiModelProperty("上传人")
    private String creator;
    @ApiModelProperty("上传时间")
    private String createTime;
    @ApiModelProperty("排序优先级")
    private int sort;
}
