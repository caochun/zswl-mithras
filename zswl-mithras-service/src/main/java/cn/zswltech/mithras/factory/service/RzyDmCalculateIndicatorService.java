package cn.zswltech.mithras.factory.service;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.factory.mapper.RzyDmCalculateIndicatorMapper;
import cn.zswltech.mithras.factory.model.RzyDmCalculateIndicator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 融租易内评区域模型指标值;(rzy_dm_calculate_indicator)表服务实现类
 * @author : zhaozhengkang
 * @date : 2024-5-29
 */
@Service
public class RzyDmCalculateIndicatorService extends ServiceImpl<RzyDmCalculateIndicatorMapper, RzyDmCalculateIndicator>{
    @Resource
    private RzyDmCalculateIndicatorMapper dmCalculateIndicatorMapper;

}