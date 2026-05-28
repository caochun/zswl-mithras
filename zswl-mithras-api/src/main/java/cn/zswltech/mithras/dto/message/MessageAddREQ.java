package cn.zswltech.mithras.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName MessageREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/9/13 11:23 上午
 * @Version 1.0
 **/
@Data
@ApiModel("消息通知-基础请求体")
public class MessageAddREQ {

    @ApiModelProperty("发送人")
    private String from;//来自谁

    @ApiModelProperty("接收人")
    @NotNull(message = "接收人不能为空")
    private List<Long> to;//消息传递给谁(被通知对象)

    /**
     * 流程实例id
     */
    @ApiModelProperty("实例id")
    @NotNull(message = "实例id不能为空, 流程ID，合同ID均可")
    private String flowid;

    //拼接标题使用
    @ApiModelProperty("表单名称,如公司名称，项目名称，合同名称等，详情参考 https://eyvwt4pfvp.feishu.cn/sheets/shtcnGBjbrJN0Y4IIFoHUttYhHh")
    private String relation;

    //租赁消息内容为某流程详情或调整页面，暂定放调整id之类数据，用于点击跳转
    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty(value = "是否发送oa", example = "true")
    private boolean needQa;
    private boolean needOa;


    @ApiModelProperty("通知来源 NoticeSourceENUM")
    @NotBlank(message = "通知来源不能为空")
    private String noticeSource;

    @ApiModelProperty("通知类型 MessageTypeEnum")
    @NotBlank(message = "通知类型不能为空")
    private String messageType;

    /**
     * APP地址 建议使用MessageUrlEnum 统一维护
     */
    @ApiModelProperty(value = "APP地址由配置项baseUrl + appurl拼接而成,  没有app地址置为null", example = "/approval/1990927?diff=processInstanceId")
    String appurl;

    /**
     * PC地址 建议使用MessageUrlEnum 统一维护
     */
    @ApiModelProperty(value = "PC地址配置项baseUrl + pcurl拼接而成", example = "/process/receive/detail/1990927?typeId=approval")
    String pcurl;

    //用于查询
    @ApiModelProperty("待办-任务ID taskId ")
    private String taskId;

    @ApiModelProperty("业务ID，如，立项ID， 评审ID，合同ID")
    private String businessId;

}
