package cn.zswltech.mithras.document.mapper.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
* 文件模版权限配置表
* @author yangxiong
 * @TableName file_authentication_config
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("file_authentication_config")
public class FileAuthenticationConfig implements Serializable {
    private static final long serialVersionUID = 8997899156621424895L;
    /**
    * 主键ID
    */
    @NotNull(message="[主键ID]不能为空")
    @ApiModelProperty("主键ID")
    private Long id;
    /**
    * 文件名称
    */
    @NotBlank(message="[文件名称]不能为空")
    @Size(max= 64,message="编码长度不能超过64")
    @ApiModelProperty("文件名称")
    @Length(max= 64,message="编码长度不能超过64")
    private String fileName;
    /**
    * 文件类型
    */
    @NotBlank(message="[文件类型]不能为空")
    @Size(max= 64,message="编码长度不能超过64")
    @ApiModelProperty("文件类型")
    @Length(max= 64,message="编码长度不能超过64")
    private String fileType;
    /**
    * 岗位/人
    */
    @NotNull(message="[岗位/人]不能为空")
    @ApiModelProperty("岗位/人")
    private Integer ownerType;
    /**
    * 关联用户岗位
    */
    @Size(max= 64,message="编码长度不能超过64")
    @ApiModelProperty("关联用户岗位")
    @Length(max= 64,message="编码长度不能超过64")
    private String ownerPost;
    /**
    * 关联用户ID
    */
    @ApiModelProperty("关联用户ID")
    private Long ownerId;
    /**
    * 创建人ID
    */
    @NotNull(message="[创建人ID]不能为空")
    @ApiModelProperty("创建人ID")
    private Long createBy;
    /**
    * 更新人ID
    */
    @NotNull(message="[更新人ID]不能为空")
    @ApiModelProperty("更新人ID")
    private Long updateBy;
    /**
    * 创建时间
    */
    @NotNull(message="[创建时间]不能为空")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    /**
    * 更新时间
    */
    @NotNull(message="[更新时间]不能为空")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
