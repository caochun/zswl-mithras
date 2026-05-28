package cn.zswltech.mithras.service.service.newftp.service.drift;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.newftp.RegionalClassify;
import cn.zswltech.mithras.service.enums.newftp.TermRange;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.newftp.convert.NewFtpMonthlyGuidanceExtDraftConverter;
import cn.zswltech.mithras.service.service.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.service.service.newftp.mapper.draft.NewFtpMonthlyGuidanceExtDraftMapper;
import cn.zswltech.mithras.service.service.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyGuidanceExtDraft;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.service.util.FlowUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description ftp指导报价扩展表（下半部分）
 * @date 2023-05-21
 */
@Service
public class NewFtpMonthlyGuidanceExtDraftService
        extends ServiceImpl<NewFtpMonthlyGuidanceExtDraftMapper, NewFtpMonthlyGuidanceExtDraft> {
    @Resource
    private NewFtpMonthlyGuidanceDraftService monthlyGuidanceDraftService;
    @Resource
    private NewFtpMonthlyGuidanceExtDraftConverter baseConverter;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;
    @Resource
    private NewFtpBaseInfoService baseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(LocalDate thisMonth, Long mainId) {
        NewFtpMonthlyGuidanceExtDraft info = new NewFtpMonthlyGuidanceExtDraft();
        info.setFtpId(mainId);
        info.setSellingPrice(22000);
        info.setOneYear(40000);
        info.setOneToThreeYear(44000);
        info.setMoreThanThreeYear(45500);
        info.setBuyingPrice("同项目FTP");
        //docCalculate(info);
        baseMapper.insert(info);
    }

    public void calculate(Long mainId) {
        ProcessResp relatedProcess = baseInfoService.findRelatedProcess(mainId);
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(relatedProcess);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
        NewFtpMonthlyGuidanceExtDraft info = getOne(Wrappers.<NewFtpMonthlyGuidanceExtDraft>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceExtDraft::getFtpId, mainId).last("limit 1"));
        if (ObjectUtil.isNull(info)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        docCalculate(info);
        baseMapper.updateById(info);
    }

    private void docCalculate(NewFtpMonthlyGuidanceExtDraft info) {
        //产业类鼓励介入类支持鼓励类地区对应期限三项
        // +产业类适度支持类支持鼓励类地区对应期限三项
        // +产业类谨慎支持类支持鼓励类地区对应期限三项）
        // /9
        Map<String, List<NewFtpMonthlyGuidanceDraft>> termMap = monthlyGuidanceDraftService
                .list(Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, info.getFtpId())
                        .isNotNull(NewFtpMonthlyGuidanceDraft::getAssetIndustryClassify)
                        .eq(NewFtpMonthlyGuidanceDraft::getRegionalClassify, RegionalClassify.ENCOURAGE.name()))
                .stream().collect(Collectors.groupingBy(NewFtpMonthlyGuidanceDraft::getTermRange));

        info.setOneYear(termMap.get(TermRange.ONE_YEAR.name()).stream()
                .map(NewFtpMonthlyGuidanceDraft::getValue)
                .map(LongUtil::null2zero)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(9), 0, RoundingMode.HALF_UP)
                .intValue());
        info.setOneToThreeYear(termMap.get(TermRange.ONE_TO_THREE_YEARS.name()).stream()
                .map(NewFtpMonthlyGuidanceDraft::getValue)
                .map(LongUtil::null2zero)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(9), 0, RoundingMode.HALF_UP)
                .intValue());
        info.setMoreThanThreeYear(termMap.get(TermRange.MORE_THAN_THREE_YEARS.name()).stream()
                .map(NewFtpMonthlyGuidanceDraft::getValue)
                .map(LongUtil::null2zero)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(9), 0, RoundingMode.HALF_UP)
                .intValue());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(NewFtpMonthlyGuidanceExtDraftModifyREQ req) {
        NewFtpMonthlyGuidanceExtDraft originalInfo = baseMapper.selectById(req.getId());
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

        NewFtpMonthlyGuidanceExtDraft info = baseConverter.modifReq2Entity(req);
        baseMapper.updateById(info);

        NewFtpBaseInfo baseInfo = baseInfoService.getById(originalInfo.getFtpId());
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
    }

    public NewFtpMonthlyGuidanceExtDraftDetailRSP detail(Long mainId) {
        NewFtpMonthlyGuidanceExtDraft one = getOne(Wrappers.<NewFtpMonthlyGuidanceExtDraft>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceExtDraft::getFtpId, mainId).last("limit 1"));
        return baseConverter.entity2DetailRsp(one);
    }

}