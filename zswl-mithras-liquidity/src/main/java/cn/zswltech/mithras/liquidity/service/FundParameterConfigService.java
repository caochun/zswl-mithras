package cn.zswltech.mithras.liquidity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.*;
import cn.zswltech.mithras.liquidity.enums.FundParameterConfigType;
import cn.zswltech.mithras.liquidity.persistence.model.FundParameterConfig;
import cn.zswltech.mithras.liquidity.persistence.mapper.FundParameterConfigMapper;
import cn.zswltech.mithras.liquidity.bo.FundParameterBaseConfigBO;
import cn.zswltech.mithras.liquidity.bo.FundParameterIndexConfigBO;
import cn.zswltech.mithras.liquidity.application.port.FundParameterConfigSupportPort;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 资金基础参数配置 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-12-12
 */
@Service
public class FundParameterConfigService extends ServiceImpl<FundParameterConfigMapper, FundParameterConfig> {

    @Resource
    private FundParameterConfigSupportPort fundParameterConfigSupportPort;

    public ParameterBaseDetailRSP parameterBaseDetail(ParameterBaseDetailREQ req) {
        ParameterBaseDetailRSP rsp = new ParameterBaseDetailRSP();
        FundParameterConfig config = getByConfigCode(FundParameterConfigType.LIQUIDITY_BASE.getDisplay());
        if(config != null && config.getConfigValue() != null){
            FundParameterBaseConfigBO configBO = JSON.parseObject(config.getConfigValue(), FundParameterBaseConfigBO.class);
            rsp.setFlexibleCredit(configBO.getFlexibleCredit());
            rsp.setSaveStock(configBO.getSaveStock());
        }
        rsp.setAccountBalanceUpdateTime(fundParameterConfigSupportPort.getAccountBalanceUpdateTime());
        return rsp;
    }

    public ParameterBaseModifyRSP parameterBaseModify(ParameterBaseModifyREQ req) {
        FundParameterBaseConfigBO configBO = BeanUtil.copyProperties(req != null ? req : new ParameterBaseDetailREQ(), FundParameterBaseConfigBO.class);
        String configValue = JSON.toJSONString(configBO);
        setValueByConfigCode(FundParameterConfigType.LIQUIDITY_BASE.getDisplay() ,configValue);
        fundParameterConfigSupportPort.settingDataUpdate();
        return null;
    }


    public List<ParameterIndexDetailRSP> parameterIndexDetail(ParameterIndexDetailREQ req) {
        List<ParameterIndexDetailRSP> rsp = new ArrayList<>();
        FundParameterConfig config = getByConfigCode(FundParameterConfigType.LIQUIDITY_INDEX.getDisplay());
        if(config != null && config.getConfigValue() != null){
            List<FundParameterIndexConfigBO> configBOList = JSON.parseArray(config.getConfigValue(), FundParameterIndexConfigBO.class);
            rsp = configBOList.stream().map(item -> BeanUtil.copyProperties(item, ParameterIndexDetailRSP.class)).collect(Collectors.toList());
        }
        return rsp;
    }

    public ParameterIndexModifyRSP parameterIndexModify(List<ParameterIndexModifyREQ> req) {
        List<FundParameterIndexConfigBO> configBO = BeanUtil.copyToList(req != null ? req : new ArrayList<>(), FundParameterIndexConfigBO.class);
        String configValue = JSON.toJSONString(configBO);
        setValueByConfigCode(FundParameterConfigType.LIQUIDITY_INDEX.getDisplay() ,configValue);
        fundParameterConfigSupportPort.settingDataUpdate();
        return null;
    }


    public FundParameterConfig getByConfigCode(String configCode){
        LambdaQueryWrapper<FundParameterConfig> wrapper = Wrappers.<FundParameterConfig>lambdaQuery();
        wrapper.eq(FundParameterConfig::getConfigCode, configCode);
        wrapper.last(StringUtil.mysqlLimitOne());
        return this.getOne(wrapper);
    }

    public void setValueByConfigCode(String configCode, String configValue){
        LambdaUpdateWrapper<FundParameterConfig> wrapper = Wrappers.<FundParameterConfig>lambdaUpdate();
        wrapper.eq(FundParameterConfig::getConfigCode, configCode);
        wrapper.set(FundParameterConfig::getConfigValue, configValue);
        this.update(wrapper);
    }





}
