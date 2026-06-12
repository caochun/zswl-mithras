package cn.zswltech.mithras.blackgray.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayWarehouseRuleConfig;

import java.util.List;
import java.util.Map;

/**
* @description 黑灰名单库-入库原因参数配置
* @author 
* @date 2024-01-18
*/
public interface BlackGrayWarehouseRuleConfigService {

    void add(BlackGrayWarehouseRuleConfigAddREQ req);

    void modify(BlackGrayWarehouseRuleConfigModifyREQ req);

    BlackGrayWarehouseRuleConfig detail(Long id);

    BlackGrayWarehouseRuleConfig detail(String ruleNumber);

    PageR<BlackGrayWarehouseRuleConfig> list(BlackGrayWarehouseRuleConfigListREQ req);

    void remove(BlackGrayWarehouseRuleConfigRemoveREQ req);

    void switchConfig(BlackGrayWarehouseRuleConfigSwitchREQ req);

    String num2Name(String num);

    Map<String, String> num2NameBatch(List<String> nums);

    Map<String, BlackGrayWarehouseRuleConfig> num2BeanBatch(List<String> nums);

}