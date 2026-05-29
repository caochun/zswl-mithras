package cn.zswltech.mithras.service.mapper;


import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @create: 2022-07-21
 **/
public interface MaterialsListMapper extends CustomBaseMapper<MaterialsList> {

    List<MaterialsList> queryNeedSignFile(@NotNull @Param("contractId") Long contractId);
}
