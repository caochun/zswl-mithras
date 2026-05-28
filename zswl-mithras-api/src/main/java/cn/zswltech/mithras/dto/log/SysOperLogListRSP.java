package cn.zswltech.mithras.dto.log;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @description 操作日志记录
 * @author hspcadmin
 * @date 2025-09-07
 */
@Data
@ApiModel("操作日志记录列表-返回体")
public class SysOperLogListRSP {

    /**
    * 日志主键
    */
    @ApiModelProperty(value = "日志主键")
    private Long id;

    /**
    * 模块标题
    */
    @ApiModelProperty(value = "模块标题")
    private String title;

    /**
    * 业务类型（0其它 1新增 2修改 3删除）
    */
    @ApiModelProperty(value = "业务类型（0其它 1新增 2修改 3删除）")
    private Integer businessType;

    /**
    * 方法名称
    */
    @ApiModelProperty(value = "方法名称")
    private String method;

    /**
    * 请求方式
    */
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    /**
    * 操作类别（0其它 1后台用户 2手机端用户）
    */
    @ApiModelProperty(value = "操作类别（0其它 1后台用户 2手机端用户）")
    private Integer operatorType;

    /**
    * 操作人员
    */
    @ApiModelProperty(value = "操作人员")
    private String operName;

    /**
    * 部门名称
    */
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
    * 请求url
    */
    @ApiModelProperty(value = "请求url")
    private String operUrl;

    /**
    * 主机地址
    */
    @ApiModelProperty(value = "主机地址")
    private String operIp;

    /**
    * 操作地点
    */
    @ApiModelProperty(value = "操作地点")
    private String operLocation;

    /**
    * 请求参数
    */
    @ApiModelProperty(value = "请求参数")
    private String operParam;

    /**
    * 返回参数
    */
    @ApiModelProperty(value = "返回参数")
    private String jsonResult;

    /**
    * 操作状态（0正常 1异常）
    */
    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    private Integer status;

    /**
    * 错误消息
    */
    @ApiModelProperty(value = "错误消息")
    private String errorMsg;

    /**
    * 操作时间
    */
    @ApiModelProperty(value = "操作时间")
    private LocalDateTime operTime;

}
