package cn.zswltech.mithras.blackgray.persistence.mapper;

import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseRuleConfigListREQ;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayWarehouseRuleConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 黑灰名单库-入库原因参数配置
* @author 
* @date 2024-01-18
*/
public interface BlackGrayWarehouseRuleConfigMapper extends BaseMapper<BlackGrayWarehouseRuleConfig> {

    List<BlackGrayWarehouseRuleConfig> myList(@Param("param") BlackGrayWarehouseRuleConfigListREQ req);

}