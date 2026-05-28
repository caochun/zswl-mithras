package cn.zswltech.mithras.blackgray.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.enums.BlackGrayOrgEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRuleConfigMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRuleConfig;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRuleConfigService;
import cn.zswltech.mithras.blackgray.service.RedisService;
import cn.zswltech.mithras.blackgray.utils.StringUtils;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 黑灰名单库-入库原因参数配置
* @author 
* @date 2024-01-18
*/
@Service
@Slf4j
public class BlackGrayWarehouseRuleConfigServiceImpl implements BlackGrayWarehouseRuleConfigService {

    @Resource
    private BlackGrayWarehouseRuleConfigMapper blackGrayWarehouseRuleConfigMapper;
    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisService redisService;
    private final static String BLACK_GRAY_RULE_NUM_LOCK = "BLACK_GRAY_RULE_NUM_LOCK";

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(BlackGrayWarehouseRuleConfigAddREQ req) {
        BlackGrayWarehouseRuleConfig info = BeanUtil.copyProperties(req, BlackGrayWarehouseRuleConfig.class, "suitBusiness", "suitOrg");
        Long userId = AccountUtil.getLoginInfo().getId();
        info.setOrgCode(BlackGrayOrgEnum.ZSZL.name());
        getRuleNumber(info, 3);
        info.setCreateBy(userId);
        info.setUpdateBy(userId);
        if(ObjectUtil.isNotEmpty(req.getSuitBusiness())){
            info.setSuitBusiness(JSONUtil.toJsonStr(req.getSuitBusiness()));
        }
        if(ObjectUtil.isNotEmpty(req.getSuitOrg())){
            info.setSuitOrg(JSONUtil.toJsonStr(req.getSuitOrg()));
        }
        blackGrayWarehouseRuleConfigMapper.insert(info);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(BlackGrayWarehouseRuleConfigModifyREQ req) {
        BlackGrayWarehouseRuleConfig originalInfo = blackGrayWarehouseRuleConfigMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException("记录不存在");
        }
        BlackGrayWarehouseRuleConfig info = BeanUtil.copyProperties(req, BlackGrayWarehouseRuleConfig.class, "suitBusiness", "suitOrg");
        if(ObjectUtil.isNotEmpty(req.getSuitBusiness())){
            info.setSuitBusiness(JSONUtil.toJsonStr(req.getSuitBusiness()));
        }
        if(ObjectUtil.isNotEmpty(req.getSuitOrg())){
            info.setSuitOrg(JSONUtil.toJsonStr(req.getSuitOrg()));
        }
        blackGrayWarehouseRuleConfigMapper.updateById(info);
    }
    @Override
    public BlackGrayWarehouseRuleConfig detail(Long id) {
        return blackGrayWarehouseRuleConfigMapper.selectById(id);
    }

    @Override
    public BlackGrayWarehouseRuleConfig detail(String ruleNumber) {
        return  blackGrayWarehouseRuleConfigMapper.selectOne(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .eq(BlackGrayWarehouseRuleConfig::getRuleNumber, ruleNumber));
    }

    @Override
    public PageR<BlackGrayWarehouseRuleConfig> list(BlackGrayWarehouseRuleConfigListREQ req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayWarehouseRuleConfig> blackGrayWarehouseRuleConfigs = blackGrayWarehouseRuleConfigMapper.myList(req);
        PageInfo<BlackGrayWarehouseRuleConfig> blackGrayWarehouseRuleConfigPageInfo = new PageInfo<>(blackGrayWarehouseRuleConfigs);
        return PageR.of(blackGrayWarehouseRuleConfigs, blackGrayWarehouseRuleConfigPageInfo.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(BlackGrayWarehouseRuleConfigRemoveREQ req) {
        List<BlackGrayWarehouseRuleConfig> blackGrayWarehouseRuleConfigs = blackGrayWarehouseRuleConfigMapper.selectBatchIds(req.getIds());
        if (ObjectUtil.isEmpty(blackGrayWarehouseRuleConfigs)) {
            for (BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig : blackGrayWarehouseRuleConfigs) {
                if (blackGrayWarehouseRecordMapper.countByApplyReasonType(blackGrayWarehouseRuleConfig.getRuleNumber()) > 0) {
                    throw new MithrasException(blackGrayWarehouseRuleConfig.getRuleNumber() + "已经本使用，不可删除");
                }
            }
        }
        blackGrayWarehouseRuleConfigMapper.deleteBatchIds(req.getIds());
    }

    @Override
    public void switchConfig(BlackGrayWarehouseRuleConfigSwitchREQ req) {
        LambdaUpdateWrapper<BlackGrayWarehouseRuleConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(BlackGrayWarehouseRuleConfig::getStatus, req.getStatus());
        updateWrapper.in(BlackGrayWarehouseRuleConfig::getId, req.getIds());
        blackGrayWarehouseRuleConfigMapper.update(null, updateWrapper);
    }

    @Override
    public String num2Name(String num) {
        BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig = blackGrayWarehouseRuleConfigMapper.selectOne(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .eq(BlackGrayWarehouseRuleConfig::getRuleNumber, num));
        return blackGrayWarehouseRuleConfig == null ? null : blackGrayWarehouseRuleConfig.getRuleName();
    }

    @Override
    public Map<String, String> num2NameBatch(List<String> nums) {
        List<BlackGrayWarehouseRuleConfig> blackGrayWarehouseRuleConfigs = blackGrayWarehouseRuleConfigMapper.selectList(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(nums), BlackGrayWarehouseRuleConfig::getRuleNumber, nums));
        if(ObjectUtil.isEmpty(blackGrayWarehouseRuleConfigs)){
            return MapUtil.empty();
        } else {
            return blackGrayWarehouseRuleConfigs.stream().collect(Collectors.toMap(BlackGrayWarehouseRuleConfig::getRuleNumber, BlackGrayWarehouseRuleConfig::getRuleName, (a, b) -> b));
        }
    }

    @Override
    public Map<String, BlackGrayWarehouseRuleConfig> num2BeanBatch(List<String> nums) {
        List<BlackGrayWarehouseRuleConfig> blackGrayWarehouseRuleConfigs = blackGrayWarehouseRuleConfigMapper.selectList(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(nums), BlackGrayWarehouseRuleConfig::getRuleNumber, nums)
                .eq(BlackGrayWarehouseRuleConfig::getStatus, 1));
        if(ObjectUtil.isEmpty(blackGrayWarehouseRuleConfigs)){
            return MapUtil.empty();
        } else {
            return blackGrayWarehouseRuleConfigs.stream().collect(Collectors.toMap(BlackGrayWarehouseRuleConfig::getRuleNumber, e -> e, (a, b) -> b));
        }
    }

    //任务编号，外部名单E/内部名单 +黑名单B/灰名单G +6位自增序号
    private String getRuleNumber(BlackGrayWarehouseRuleConfig info, int tryNum) {
        BlackGrayWarehouseRuleConfig record = blackGrayWarehouseRuleConfigMapper.selectOne(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .eq(BlackGrayWarehouseRuleConfig::getSource, info.getSource())
                .eq(BlackGrayWarehouseRuleConfig::getBlackGrayType, info.getBlackGrayType())
                .orderByDesc(BlackGrayWarehouseRuleConfig::getRuleSequence)
                .last(StringUtils.mysqlLimitOne()));
        Integer ruleSequence = record == null ? 0 : record.getRuleSequence();
        ++ruleSequence;
        String ruleNum = String.format("%s%s%s%06d", info.getSource().charAt(0), info.getBlackGrayType().charAt(0), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), ruleSequence);
        String value = redisService.get(BLACK_GRAY_RULE_NUM_LOCK + ruleNum);
        if (StringUtil.isBlank(value)) {
            // 当前线程占用该taskNum，在redis中上锁，最长时间60秒，任务完成后解锁
            redisService.set(BLACK_GRAY_RULE_NUM_LOCK + ruleNum, ruleNum, 60);
            info.setRuleNumber(ruleNum);
            info.setRuleSequence(ruleSequence);
            return ruleNum;
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("getRuleNumber获取锁睡眠任务被打断", e);
                Thread.currentThread().interrupt();
            }
            getRuleNumber(info, --tryNum);
        }
        throw new MithrasException("任务生成繁忙，请稍后重试");
    }

}