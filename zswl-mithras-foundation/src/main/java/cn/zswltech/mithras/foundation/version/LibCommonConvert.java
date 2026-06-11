package cn.zswltech.mithras.foundation.version;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;

/**
 *
 * @author: jackerhe
 * @date: 2022/12/19 9:54 上午
 * 用于统一管理版本表与业务表转换逻辑，使用同一方法转换，避免因业务表变动导致两边转换不一致问题
 **/
public interface LibCommonConvert<ENTITY extends IEntity, RSP extends ListBaseRSP> {

    RSP entity2RSP(ENTITY t);

}
