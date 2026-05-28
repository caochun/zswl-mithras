package cn.zswltech.mithras.dto.file;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.CommonFileSortWeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName FileListRSP
 * @Description 文件列表查询
 * @Author jackerhe
 * @Date 2022/11/19 11:08 上午
 * @Version 1.0
 **/
@Data
@ApiModel("文件列表-返回体")
public class FileListRSP extends CommonFileSortWeight {

    @ApiModelProperty("文件")
    private Long id;

    /**
     * 归属id
     */
    @ApiModelProperty("归属id")
    private Long belongId;

    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    private String businessType;

    /**
     * 资料类型
     */
    @ApiModelProperty("资料类型")
    private String materialsType;

    /**
     * 资料类型名称
     */
    @ApiModelProperty("资料类型名称")
    private String materialsTypeName;

    /**
     * 资料子类型
     */
    @ApiModelProperty("资料子类型")
    private String materialSubType;

    /**
     * 资料子类型名称
     */
    @ApiModelProperty("资料子类型名称")
    private String materialSubTypeName;

    /**
     * oss上传文件名
     */
    @ApiModelProperty("oss上传文件名")
    private String ossFilename;

    /**
     * 附件名
     */
    @ApiModelProperty("附件名")
    private String filename;

    /**
     * 文件名后缀
     */
    @ApiModelProperty("文件名后缀")
    private String suffix;


    @ApiModelProperty("文件路径")
    private String filePath;

    private Integer systemGenerate;

    @ApiModelProperty("来源业务key")
    private String sourceBusinessKey;

    @ApiModelProperty("上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty("上传人id")
    private Long createBy;

    @ApiModelProperty("上传人姓名")
    private String createByName;

    @ApiModelProperty("修改人id")
    private Long updateBy;

    @ApiModelProperty("修改人姓名")
    private String updateByName;

    @ApiModelProperty(value = "上传人岗位")
    private List<String> uploadByPostList;

    @ApiModelProperty("上传地点")
    private String location;

    @ApiModelProperty("是否被编辑")
    private Integer isEdit;

    @ApiModelProperty("预览类型")
    private String previewType;

    @ApiModelProperty("预览地址")
    private String previewUrl;

    //@ApiModelProperty("扩展参数")
    //private Map<String, Object> ext;

    @Override
    protected String sortKey() {
        return filename;
    }

    @Override
    public long createTimestamp() {
        return Optional.ofNullable(createTime).map(LocalDateTimeUtil::toEpochMilli).orElse(Long.MAX_VALUE);
    }
}
