package cn.zswltech.mithras.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigDetailREQ;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigDetailRSP;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigSaveREQ;
import cn.zswltech.mithras.system.mapper.UserCustomConfigMapper;
import cn.zswltech.mithras.system.mapper.model.UserCustomConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/2/11
 * @description
 */
@Slf4j
@Service
public class UserCustomConfigService extends ServiceImpl<UserCustomConfigMapper, UserCustomConfig> {
    public void save(UserCustomConfigSaveREQ req) {
        // 先查询一下是否已有数据
        Long userId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<UserCustomConfig> query = Wrappers.lambdaQuery();
        query.eq(UserCustomConfig::getUserId, userId);
        query.eq(UserCustomConfig::getConfigKey, req.getConfigKey());
        UserCustomConfig exist = this.getOne(query);
        if (Objects.isNull(exist)) {
            // 没数据新增
            UserCustomConfig newConfig = new UserCustomConfig();
            newConfig.setUserId(userId);
            newConfig.setConfigKey(req.getConfigKey());
            newConfig.setConfigValue(req.getConfigValue());
            newConfig.setMetadataType(req.getMetadataType());
            this.save(newConfig);
        } else {
            // 有数据更新
            exist.setConfigValue(req.getConfigValue());
            exist.setMetadataType(req.getMetadataType());
            this.getBaseMapper().updateAnnotationIncludeNullById(exist);
        }
    }

    public List<UserCustomConfigDetailRSP> query(UserCustomConfigDetailREQ req) {
        Long userId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<UserCustomConfig> query = Wrappers.lambdaQuery();
        query.eq(UserCustomConfig::getUserId, userId);
        if (StrUtil.isNotBlank(req.getConfigKey())) {
            query.eq(UserCustomConfig::getConfigKey, req.getConfigKey());
        }
        List<UserCustomConfig> dbList = this.list(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        return dbList.stream().map(e -> {
            UserCustomConfigDetailRSP rsp = new UserCustomConfigDetailRSP();
            rsp.setUserId(userId);
            rsp.setConfigKey(e.getConfigKey());
            rsp.setConfigValue(e.getConfigValue());
            rsp.setMetadataType(e.getMetadataType());
            return rsp;
        }).collect(Collectors.toList());
    }
}
