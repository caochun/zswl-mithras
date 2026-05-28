package cn.zswltech.mithras.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName MessageListRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/9/14 3:34 下午
 * @Version 1.0
 **/
@Data
public class MessageListRSP{

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 1通知，2公告，3待办任务
     */
    private Short type;

    /**
     * 0正常，1关闭
     */
    private Short status;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date gmtCreate;

    /**
     * 修改用户
     */
    private String updateBy;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date gmtUpdate;

    /**
     * 处理用户列表，ALL则为所有用户*  逗号 分割
     */
    private String dealUser;

    /**
     * 业务模块信息
     */
    private String bizInfo;

    /**
     * Redis_stream_offset
     */
    private String recordId;
    /**
     * 是否超时，1超时，0未超时
     **/
    private Integer overtimeFlag;

    private static final long serialVersionUID = 1L;


    /**
     * 是否已读 true 已读
     */
    private boolean readFlag;
}
