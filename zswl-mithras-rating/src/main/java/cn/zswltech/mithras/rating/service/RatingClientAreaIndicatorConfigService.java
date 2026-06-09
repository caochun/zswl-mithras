package cn.zswltech.mithras.rating.service;

import cn.zswltech.mithras.rating.mapper.RatingClientAreaIndicatorConfigMapper;
import cn.zswltech.mithras.rating.model.RatingClientAreaIndicatorConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/19
 * @description
 */
@Slf4j
@Service
public class RatingClientAreaIndicatorConfigService extends ServiceImpl<RatingClientAreaIndicatorConfigMapper, RatingClientAreaIndicatorConfig> {
    public List<RatingClientAreaIndicatorConfig> listByCategoryCode(String categoryCode) {
        LambdaQueryWrapper<RatingClientAreaIndicatorConfig> query = Wrappers.lambdaQuery();
        query.eq(RatingClientAreaIndicatorConfig::getCategoryCode, categoryCode);
        query.orderByAsc(RatingClientAreaIndicatorConfig::getIndicatorSort);
        query.orderByAsc(RatingClientAreaIndicatorConfig::getId);
        return this.list(query);
    }
}
