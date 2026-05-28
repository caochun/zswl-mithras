package cn.zswltech.mithras.service.service.newftp.service.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterDTO;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingConfigListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingListREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingModifyREQ;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.newftp.mapper.config.NewFtpParameterSettingConfigMapper;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpParameterSettingConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【new_ftp_parameter_setting_config(ftp参数设定配置表)】的数据库操作Service实现
 * @createDate 2024-03-22 14:00:38
 */
@Service
public class NewFtpParameterSettingConfigService extends ServiceImpl<NewFtpParameterSettingConfigMapper, NewFtpParameterSettingConfig> {

    public List<NewFtpParameterSettingConfig> list(NewFtpParameterSettingListREQ req) {
        return baseMapper.selectList(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery());
    }

    public Map<String, NewFtpParameterSettingConfig> params(String category) {
        return list(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery()
                .eq(NewFtpParameterSettingConfig::getCategory, category)).stream()
                .collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (k1, k2) -> {
                    throw new MithrasException("参数key重复！");
                }));
    }

    public List<NewFtpParameterSettingConfig> req2Domain(List<NewFtpParameterSettingModifyREQ> reqs){
        if(CollUtil.isEmpty(reqs)){
            return Collections.emptyList();
        }
        List<NewFtpParameterSettingConfig> data = new LinkedList<>();
        reqs.forEach(dtp->{
            NewFtpParameterSettingConfig config = new NewFtpParameterSettingConfig();
            config.setId(dtp.getId());
            config.setFormula(JSONUtil.toJsonStr(dtp.getFormula()));
            config.setCategory(dtp.getCategory());
            config.setParamName(dtp.getParamName());
            config.setValue(dtp.getValue());
            config.setCategoryDisplay(dtp.getCategoryDisplay());
            config.setParamOtherName(dtp.getParamOtherName());
            config.setUpdateBy(AccountUtil.getLoginInfo().getId());
            data.add(config);
        });
        return data;
    }

    public List<NewFtpParameterSettingConfigListRSP> domain2Rsp(List<NewFtpParameterSettingConfig> reqs){
        if(CollUtil.isEmpty(reqs)){
            return Collections.emptyList();
        }
        List<NewFtpParameterSettingConfigListRSP> data = new LinkedList<>();
        reqs.forEach(dtp->{
            NewFtpParameterSettingConfigListRSP config = new NewFtpParameterSettingConfigListRSP();
            config.setId(dtp.getId());
            config.setFormula(JSONUtil.toList(dtp.getFormula(), NewFtpParameterDTO.class));
            config.setCategory(dtp.getCategory());
            config.setParamName(dtp.getParamName());
            config.setValue(dtp.getValue());
            config.setCategoryDisplay(dtp.getCategoryDisplay());
            config.setParamOtherName(dtp.getParamOtherName());
            data.add(config);
        });
        return data;
    }

}




