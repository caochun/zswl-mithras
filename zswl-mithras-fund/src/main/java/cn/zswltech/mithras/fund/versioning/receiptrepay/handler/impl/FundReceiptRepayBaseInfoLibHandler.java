package cn.zswltech.mithras.fund.versioning.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.fund.versioning.receiptrepay.FundReceiptRepayBaseInfoAssembler;
import cn.zswltech.mithras.fund.versioning.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import cn.zswltech.mithras.fund.versioning.receiptrepay.FundReceiptRepayCashFlowLibService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 基本信息
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:42 PM
 */
@Component
public class FundReceiptRepayBaseInfoLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayBaseInfoLib, FundReceiptRepayBaseInfo, FundReceiptRepayBaseInfoDetailRSP> {

    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private FundReceiptRepayBaseInfoAssembler baseInfoAssembler;
    @Resource
    private FundReceiptRepayCashFlowLibService fundReceiptRepayCashFlowLibService;

    /**
     * 把临时表数据 插入版本表
     * 要记录最新版本数据
     *
     * @param version       版本
     * @param needClearLast 是否需要清空同版本旧数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void flushData(String version, Long mainId, boolean needClearLast, Integer versionType) {
        FundReceiptRepayBaseInfo baseInfo = draftMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptRepayBaseInfoLib lib = actualEntity2Lib(baseInfo, version, versionType);
        if (!"DIRECT".equals(baseInfo.getFinancingType())) {
            // 要记录融资的最新版本 理论上不会报错
            CommonVersion financingLatestVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                    .eq(CommonVersion::getMainId, baseInfo.getFinancingId())
                    .eq(CommonVersion::getModule, "FUND_FINANCING")
                    .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(CommonVersion::getVersion)
                    .last("LIMIT 1")
            );
            if (Objects.isNull(financingLatestVersion)) {
                throw new MithrasException("融资最新有效版本不存在，数据异常");
            }
            lib.setFinancingVersion(financingLatestVersion.getVersion());
        }
        mapper.insert(lib);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     * 只还原备注
     *
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reset(Long mainId, String version) {
        FundReceiptRepayBaseInfoLib newestLib = queryLatestDataByOriginId(mainId);
        if (Objects.isNull(newestLib)) {
            return;
        }
        LambdaUpdateWrapper<FundReceiptRepayBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FundReceiptRepayBaseInfo::getId, mainId);
        updateWrapper.set(FundReceiptRepayBaseInfo::getRemark, newestLib.getRemark());
        draftMapper.update(null, updateWrapper);
    }

    @Override
    protected FundReceiptRepayBaseInfoLib entity2Lib(FundReceiptRepayBaseInfo f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayBaseInfoLib.class);
    }

    @Override
    protected FundReceiptRepayBaseInfo lib2Entity(FundReceiptRepayBaseInfoLib t) {
        return BeanUtil.copyProperties(t, FundReceiptRepayBaseInfo.class);
    }

    @Override
    protected FundReceiptRepayBaseInfoDetailRSP lib2Rsp(FundReceiptRepayBaseInfoLib f) {
        FundReceiptRepayBaseInfoDetailRSP rsp = baseInfoAssembler.lib2Rsp(actualLib2Entity(f), f);
        Long totalInterest = fundReceiptRepayCashFlowLibService.list(Wrappers.<FundReceiptRepayCashFlowLib>lambdaQuery()
                .eq(FundReceiptRepayCashFlowLib::getReceiptRepayId, f.getOriginId())
                .eq(FundReceiptRepayCashFlowLib::getVersion, f.getVersion())).stream().mapToLong(s -> LongUtil.null2zero(s.getInterestAmount())).sum();
        rsp.setTotalInterest(totalInterest);
        return rsp;
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.BASE_INFO;
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

}
