package cn.zswltech.mithras.service.service.dashboard;

import cn.zswltech.mithras.service.enums.dashboard.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.service.mapper.dashboard.GuanYuanDSInfoMapper;
import cn.zswltech.mithras.service.mapper.model.dashboard.GuanyuanDsInfo;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author yangxiong
 * @date 2024/5/16/11:26
 * @description
 */
@Slf4j
@Service
public class GuanYuanDSInfoService extends ServiceImpl<GuanYuanDSInfoMapper, GuanyuanDsInfo> {
    public String getGuanYuanDsId(BossDashboardGuanYuanDataSourceKeyEnum bossDashboardGuanYuanDataSourceKeyEnum) {
        LambdaQueryWrapper<GuanyuanDsInfo> query = Wrappers.lambdaQuery();
        query.eq(GuanyuanDsInfo::getBusinessKey, bossDashboardGuanYuanDataSourceKeyEnum.name());
        query.last(StringUtil.mysqlLimitOne());
        GuanyuanDsInfo guanyuanDsInfo = this.getOne(query);
        return Optional.ofNullable(guanyuanDsInfo).map(GuanyuanDsInfo::getGuanyuanDsId).orElse(null);
    }
}
