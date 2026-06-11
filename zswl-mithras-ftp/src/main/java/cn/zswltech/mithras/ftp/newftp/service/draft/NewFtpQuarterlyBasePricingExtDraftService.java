package cn.zswltech.mithras.ftp.newftp.service.draft;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingExtDraftDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingExtDraftModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.ftp.newftp.enums.ParamCategory;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpQuarterlyBasePricingExtDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpParameterSettingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingExtDraft;
import cn.zswltech.mithras.ftp.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpParameterSettingConfigService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/7/16
 * @description
 */
@Slf4j
@Service
public class NewFtpQuarterlyBasePricingExtDraftService extends ServiceImpl<NewFtpQuarterlyBasePricingExtDraftMapper, NewFtpQuarterlyBasePricingExtDraft> {
    @Resource
    private NewFtpBaseInfoService baseInfoService;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;

    public NewFtpQuarterlyBasePricingExtDraft findByFtpId(Long ftpId) {
        LambdaQueryWrapper<NewFtpQuarterlyBasePricingExtDraft> query = Wrappers.lambdaQuery();
        query.eq(NewFtpQuarterlyBasePricingExtDraft::getFtpId, ftpId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public void add(Long ftpId) {
        NewFtpQuarterlyBasePricingExtDraft draft = new NewFtpQuarterlyBasePricingExtDraft();
        draft.setFtpId(ftpId);
        // 查询配置参数
        Map<String, NewFtpParameterSettingConfig> map = SpringUtil.getBean(NewFtpParameterSettingConfigService.class).params(ParamCategory.COLLABORATIVE_MINIMUM_RATE.name());
        if (CollectionUtil.isNotEmpty(map)) {
            // 从参数取值
            draft.setThreeYear(Optional.ofNullable(map.get("3年内（含）")).map(NewFtpParameterSettingConfig::getValue).orElse(null));
            draft.setThreeToFiveYear(Optional.ofNullable(map.get("3-5年（含）")).map(NewFtpParameterSettingConfig::getValue).orElse(null));
            draft.setMoreThanFiveYear(Optional.ofNullable(map.get("5年以上")).map(NewFtpParameterSettingConfig::getValue).orElse(null));
        }
        this.save(draft);
    }

    public void modify(NewFtpQuarterlyBasePricingExtDraftModifyREQ req) {
        NewFtpQuarterlyBasePricingExtDraft originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ProcessResp relatedProcess = baseInfoService.findRelatedProcess(originalInfo.getFtpId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(relatedProcess);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
        NewFtpQuarterlyBasePricingExtDraft update = new NewFtpQuarterlyBasePricingExtDraft();
        update.setId(req.getId());
        update.setThreeYear(req.getThreeYear());
        update.setThreeToFiveYear(req.getThreeToFiveYear());
        update.setMoreThanFiveYear(req.getMoreThanFiveYear());
        baseMapper.updateById(update);
        NewFtpBaseInfo baseInfo = baseInfoService.getById(originalInfo.getFtpId());
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
    }

    public NewFtpQuarterlyBasePricingExtDraftDetailRSP detail(Long ftpId) {
        NewFtpQuarterlyBasePricingExtDraft one = getOne(
                Wrappers.<NewFtpQuarterlyBasePricingExtDraft>lambdaQuery()
                        .eq(NewFtpQuarterlyBasePricingExtDraft::getFtpId, ftpId)
                        .last("limit 1")
        );
        NewFtpQuarterlyBasePricingExtDraftDetailRSP rsp = new NewFtpQuarterlyBasePricingExtDraftDetailRSP();
        rsp.setId(one.getId());
        rsp.setFtpId(one.getFtpId());
        rsp.setThreeYear(one.getThreeYear());
        rsp.setThreeToFiveYear(one.getThreeToFiveYear());
        rsp.setMoreThanFiveYear(one.getMoreThanFiveYear());
        return rsp;
    }
}
