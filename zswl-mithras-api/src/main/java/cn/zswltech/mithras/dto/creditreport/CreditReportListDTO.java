package cn.zswltech.mithras.dto.creditreport;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("征信报告查询列表-响应体")
public class CreditReportListDTO extends ListBaseRSP {

    @ApiModelProperty("征信报告id")
    private Long id;

    @ApiModelProperty("查询编号")
    private String creditCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户名称列表")
    private List<String> clientNameList;

    @ApiModelProperty("统一社会信用代码")
    private String cscCode;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty("申请人id")
    private Long applyUser;

    @ApiModelProperty("申请部门id")
    private Long applyOrg;

    @ApiModelProperty("申请人")
    private String applyUserName;

    @ApiModelProperty("申请部门")
    private String applyOrgName;

    @ApiModelProperty("申请状态")
    private String applyStatus;

    @ApiModelProperty("申请通过时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;

    @ApiModelProperty("查询状态")
    private String selectStatus;

    @ApiModelProperty("查询完成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime selectTime;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("查询版本")
    private String selectVersion;

    @ApiModelProperty("查询目的")
    private String selectGoal;

    @ApiModelProperty("信用报告封装格式")
    private String reportFormat;

    @ApiModelProperty("征信报告文件")
    private List<Long> reportFileIds;


    @ApiModelProperty("统一社会信用代码-集")
    private List<String> cscCodeList;

    @ApiModelProperty("中征码-集")
    private List<String> zhongZhengCodeList;

    @ApiModelProperty("查询目的-集")
    private List<String> selectGoalList;

    @ApiModelProperty("征信报告id")
    private Long creditReportId;
}
