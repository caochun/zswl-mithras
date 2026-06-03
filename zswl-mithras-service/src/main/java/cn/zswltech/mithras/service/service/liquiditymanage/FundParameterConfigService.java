package cn.zswltech.mithras.service.service.liquiditymanage;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.*;
import cn.zswltech.mithras.liquiditymanage.enums.FundParameterConfigType;
import cn.zswltech.mithras.liquiditymanage.enums.FundParameterLevelType;
import cn.zswltech.mithras.liquiditymanage.enums.FundParameterSignType;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquiditymanage.mapper.model.FundParameterConfig;
import cn.zswltech.mithras.liquiditymanage.mapper.FundParameterConfigMapper;
import cn.zswltech.mithras.liquiditymanage.mapper.model.bo.FundParameterBaseConfigBO;
import cn.zswltech.mithras.liquiditymanage.mapper.model.bo.FundParameterIndexConfigBO;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private LiquidityDataService liquidityDataService;

    public ParameterBaseDetailRSP parameterBaseDetail(ParameterBaseDetailREQ req) {
        ParameterBaseDetailRSP rsp = new ParameterBaseDetailRSP();
        FundParameterConfig config = getByConfigCode(FundParameterConfigType.LIQUIDITY_BASE.getDisplay());
        if(config != null && config.getConfigValue() != null){
            FundParameterBaseConfigBO configBO = JSON.parseObject(config.getConfigValue(), FundParameterBaseConfigBO.class);
            rsp.setFlexibleCredit(configBO.getFlexibleCredit());
            rsp.setSaveStock(configBO.getSaveStock());
        }
        AccountBalanceBaseInfo maxTimeAccountBalance = accountBalanceBaseInfoService.getOne(Wrappers.<AccountBalanceBaseInfo>lambdaQuery()
                .select(AccountBalanceBaseInfo::getUpdateTime)
                .isNotNull(AccountBalanceBaseInfo::getActualBalanceAmount)
                .orderByDesc(AccountBalanceBaseInfo::getUpdateTime)
                .last(StringUtil.mysqlLimitOne()));
        rsp.setAccountBalanceUpdateTime(Optional.ofNullable(maxTimeAccountBalance).map(AccountBalanceBaseInfo::getUpdateTime).orElse(null));
        return rsp;
    }

    public ParameterBaseModifyRSP parameterBaseModify(ParameterBaseModifyREQ req) {
        FundParameterBaseConfigBO configBO = BeanUtil.copyProperties(req != null ? req : new ParameterBaseDetailREQ(), FundParameterBaseConfigBO.class);
        String configValue = JSON.toJSONString(configBO);
        setValueByConfigCode(FundParameterConfigType.LIQUIDITY_BASE.getDisplay() ,configValue);
        liquidityDataService.settingDataUpdate();
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
        liquidityDataService.settingDataUpdate();
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
