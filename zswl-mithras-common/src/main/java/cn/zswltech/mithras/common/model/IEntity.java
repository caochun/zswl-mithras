package cn.zswltech.mithras.common.model;

import java.time.LocalDateTime;

/**
 * 数据模型 接口
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:06 PM
 */
public interface IEntity {

    Long getId();

    void setId(Long id);

    LocalDateTime getCreateTime();

    void setCreateTime(LocalDateTime createTime);

    Long getCreateBy();

    void setCreateBy(Long createBy);

    LocalDateTime getUpdateTime();

    void setUpdateTime(LocalDateTime updateTime);

    Long getUpdateBy();

    void setUpdateBy(Long updateBy);

    void setMainId(Long id);

    Long getMainId();

}
