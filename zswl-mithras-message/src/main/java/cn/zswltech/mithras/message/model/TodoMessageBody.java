package cn.zswltech.mithras.message.model;

import cn.zswltech.mithras.message.enums.MessageType;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName TodoMessageBody
 * @Description
 * @Author jackerhe
 * @Date 2022/7/27 2:25 下午
 * @Version 1.0
 **/
@Data
public class TodoMessageBody implements MessageBody {


    /**
     * 标题
     */
    private String title;

    /**
     * 内容 存放跳转id
     */
    private String content;

    /**
     * 备注
     */
    private String remark;

    /**
     * 数据范围（deptId,roleId,userID)
     */
    private String dataScope;

    /**
     * 创建用户，发送者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改用户
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date gmtUpdate;

    /**
     * APP地址 OA
     */
    String appurl;

    /**
     * PC地址 OA
     */
    String pcurl;

    /**
     * 流程实例id
     */
    String flowid;

    /**
     * 流程类型名称 NoticeSourceENUM
     */
    String workflowname;

    /**
     * 步骤名称（节点名称）NoticeTypeEnum
     */
    String nodename;

    /**
     * 租赁自定义ID，作为消息办理依据,审批必传：taskId
     **/
    private String mithrasId;

    private MessageTypeEnum messageTypeEnum;


    @Override
    public String getSendType() {
        return MessageType.TODO.getType();
    }
}
