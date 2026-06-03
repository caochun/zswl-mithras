package cn.zswltech.mithras.factory.mapper;

import cn.zswltech.mithras.factory.model.RzyDmCalculateIndicator;
import cn.zswltech.mithras.factory.model.result.AreaModelResult;
import cn.zswltech.mithras.factory.model.result.CityModelResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* 融租易内评区域模型指标值;(rzy_dm_calculate_indicator)表数据库访问层
*/
@Mapper
public interface RzyDmCalculateIndicatorMapper extends BaseMapper<RzyDmCalculateIndicator> {

   CityModelResult selectCityModelInfo(@Param("area_uni_code") Long areaUniCode);

   AreaModelResult selectAreaModelInfo(@Param("area_uni_code") Long areaUniCode);
}