package cn.zswltech.mithras.third.retry.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description 异常请求记录表
 * @author vico
 * @date 2023-03-31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExceptionRequestInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 平台
    */
    @TableField("platform")
    private String platform;

    /**
    * 业务唯一id
    */
    @TableField("business_id")
    private String businessId;

    /**
    * 最大重试次数
    */
    @TableField("max_retry_amount")
    private Integer maxRetryAmount;

    /**
    * 重试次数
    */
    @TableField("retry_amount")
    private Integer retryAmount;

    /**
    * 请求参数
    */
    @TableField("req_data")
    private String reqData;

    /**
     * 请求参数md5值
     */
    @TableField("req_data_md5")
    private String reqDataMd5;

    /**
    * 响应值
    */
    @TableField("response")
    private String response;

    /**
    * 重试标识 0 失败，1成功
    */
    @TableField("retry_flag")
    private Integer retryFlag;

    /**
     * 接口撤回标识 0已撤回， 1 撤回失败
     **/
    @TableField("withdraw_flag")
    private Integer withdrawFlag;

    @TableField("withdraw_fail_message")
    private String withdrawFailMessage;

    /**
     * 来源，用于标识该次请求调用模块
     **/
    @TableField("source")
    private String source;

    /**
     * 业务主建 用于寻找对应业务信息
     **/
    @TableField("business_key")
    private String businessKey;

    /**
     * 业务模块用于存放页面展示信息，用于帮助业务区分记录
     **/
    @TableField("business_title")
    private String businessTitle;

}
