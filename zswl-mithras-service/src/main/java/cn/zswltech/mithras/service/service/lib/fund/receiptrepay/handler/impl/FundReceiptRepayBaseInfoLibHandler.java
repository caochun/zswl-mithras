package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.fund.receiptrepay.FundReceiptRepayConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.service.FundReceiptRepayCashFlowLibService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundReceiptRepayConverter fundReceiptRepayConverter;
    @Resource
    private FundReceiptRepayCashFlowLibService fundReceiptRepayCashFlowLibService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundOrganizationService organizationService;

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
                    .eq(CommonVersion::getModule, BusinessModuleEnum.FUND_FINANCING.name())
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
        SingleFinancingIdREQ singleFinancingIdReq = new SingleFinancingIdREQ();
        singleFinancingIdReq.setFinancingId(f.getFinancingId());
        singleFinancingIdReq.setVersion(f.getFinancingVersion());
        FundReceiptRepayBaseInfoDetailRSP rsp;
        if ("DIRECT".equals(f.getFinancingType())) {
            FundDirectFinancingBaseInfoDetailRSP directFinancingBaseInfoDetailRsp =
                    fundDirectFinancingBaseInfoService.detail(singleFinancingIdReq.getFinancingId());
            rsp = fundReceiptRepayConverter.joinDirectDetailRsp(actualLib2Entity(f), directFinancingBaseInfoDetailRsp);
        } else {
            FundFinancingBaseInfoDetailRSP financingBaseInfo = fundFinancingBaseInfoService.detail(singleFinancingIdReq);
            FundFinancingPlanDetailRSP financingPlan = fundFinancingPlanService.detail(singleFinancingIdReq);
            rsp = fundReceiptRepayConverter.joinDetailRsp(actualLib2Entity(f), financingBaseInfo, financingPlan);
            List<FundOrganization> organizationList = organizationService.getByFinancingId(f.getFinancingId());
            rsp.setFinancingOrgName(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
        }


        // 查询收付款对应的现金流计算总利息
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
