package cn.zswltech.mithras.foundation.persistence.tag;

import java.time.LocalDateTime;

/**
 * 版本模型接口
 * 实体类统一需要这些字段
 *
 * @author wangchuanhao
 * @date 2022/7/19 9:50 PM
 */
public interface ILib extends IEntity {

    String FIELD_ID = "id";
    String FIELD_VERSION = "version";
    String FIELD_ORIGIN_ID = "origin_id";
    String FIELD_DATA_CREATE_TIME = "data_create_time";
    String FIELD_DATA_CREATE_BY = "data_create_by";
    String FIELD_DATA_UPDATE_TIME = "data_update_time";
    String FIELD_DATA_UPDATE_BY = "data_update_by";
    String FIELD_CREATE_TIME = "create_time";
    String FIELD_CREATE_BY = "create_by";
    String FIELD_UPDATE_TIME = "update_time";
    String FIELD_UPDATE_BY = "update_by";
    String FIELD_VERSION_TYPE = "version_type";

     String getVersion();

     void setVersion(String version);

     Long getOriginId();

     void setOriginId(Long originId);

     LocalDateTime getDataCreateTime();

     void setDataCreateTime(LocalDateTime dataCreateTime);

     Long getDataCreateBy();

     void setDataCreateBy(Long dataCreateBy);

     LocalDateTime getDataUpdateTime();

     void setDataUpdateTime(LocalDateTime dataUpdateTime);

     Long getDataUpdateBy();

     void setDataUpdateBy(Long dataUpdateBy);

     Integer getVersionType();

     void setVersionType(Integer versionType);

}
