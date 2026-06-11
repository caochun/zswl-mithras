package cn.zswltech.mithras.document.mapper;


import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @create: 2022-07-21
 **/
public interface MaterialsListMapper extends CustomBaseMapper<MaterialsList> {

    List<MaterialsList> queryNeedSignFile(@NotNull @Param("contractId") Long contractId);
}
