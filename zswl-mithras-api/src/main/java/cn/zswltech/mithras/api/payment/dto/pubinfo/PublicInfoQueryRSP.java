package cn.zswltech.mithras.api.payment.dto.pubinfo;

import cn.zswltech.mithras.dto.file.FileListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/10 11:26
 * @description
 */
@Data
@ApiModel(value = "公开信息-查询结果响应体")
public class PublicInfoQueryRSP {

    @ApiModelProperty(value = "表格ID")
    private Long id;

    @ApiModelProperty(value = "表格详情-确认人ID")
    private Long confirmBy;

    @ApiModelProperty(value = "表格详情-确认人名称")
    private String confirmName;

    @ApiModelProperty(value = "查询开始时间 format:yyyy-MM-dd")
    private String queryFrom;

    @ApiModelProperty(value = "查询结束时间 format:yyyy-MM-dd")
    private String queryTo;

    @ApiModelProperty(value = "担保人/承租人ID")
    private Long clientId;

    @ApiModelProperty(value = "担保人/承租人名称")
    private String clientName;

    @ApiModelProperty(value = "确认时间 format:yyyy-MM-dd HH:mm:ss")
    private String confirmTime;

    @ApiModelProperty(value = "表格内容")
    private List<RowStructure> rowList;

    @ApiModelProperty(value = "公开数据拉取时间")
    private LocalDateTime publicQueryTime;

    @Data
    @ApiModel(value = "行结构")
    public static class RowStructure {

        @ApiModelProperty(value = "行ID，文件上传的MainId")
        private Long id;

        @ApiModelProperty(value = "标题")
        private String title;

        @ApiModelProperty(value = "行Key PublicInfoRowKeyEnum#name")
        private String rowKey;

        @ApiModelProperty(value = "外部查询数据")
        private String outerQueryResult;

        @ApiModelProperty(value = "文本描述")
        private String description;

        @ApiModelProperty(value = "调查类型 InvestigationResultEnum#name")
        private String investigationType;

        @ApiModelProperty(value = "调查说明")
        private String investigationExplain;

        @ApiModelProperty(value = "结果-文件列表")
        private List<FileListRSP> resultFileList;

        @ApiModelProperty(value = "项目经理-说明")
        private String projectManagerExplain;

        @ApiModelProperty(value = "项目经理-文件列表")
        private List<FileListRSP> projectManagerFileList;
    }
}
