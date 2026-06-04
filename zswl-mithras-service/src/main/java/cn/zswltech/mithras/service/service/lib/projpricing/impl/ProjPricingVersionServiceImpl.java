package cn.zswltech.mithras.projectprocess.service.lib.projpricing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.datacompare.ProjReviewPriceCompareRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingCompareREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailREQ;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionListRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.associationreport.constant.MithrasConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.controller.datacompare.EditdataCompareController;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingBaseInfoConverter;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingCashFlowPlanConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlanLib;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.ProjPricingLibAbstractHandler;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewCashFlowPlanLibService;
import cn.zswltech.mithras.service.service.projpricing.*;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.util.CompareUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description
 * @since
 */
@Service
public class ProjPricingVersionServiceImpl extends CommonVersionService<ProjPricingBaseInfo> {
    @Resource
    private List<ProjPricingLibAbstractHandler> libHandlerList;
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;
    @Resource
    private ProjPricingBaseInfoService pricingBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService ProjReviewBaseInfoService;
    @Resource
    private ProjReviewCashFlowPlanLibService reviewCashFlowPlanLibService;
    @Resource
    private ProjPricingCashFlowPlanService pricingCashFlowPlanService;
    @Resource
    private ProjPricingBaseInfoConverter baseInfoConverter;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private EditdataCompareController editdataCompareController;

    @Transactional(rollbackFor = Exception.class)
    public void reset(Long mainId) {
        ProjPricingBaseInfo baseInfo = projPricingBaseInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaQueryWrapper<CommonVersion> qw = Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getModule, getBusinessModule())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL);
        qw.orderByDesc(CommonVersion::getVersion).last("LIMIT 1");
        CommonVersion commonVersion = commonVersionMapper.selectOne(qw);
        if (Objects.nonNull(commonVersion)) {
            customReset(baseInfo, commonVersion);
        }
    }

    public CommonVersion findNewestVersionWithPriceApproval(Long mainId) {
        return commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .ne(CommonVersion::getVersionType, VersionTypeConstants.INVALID)
                .eq(CommonVersion::getModule, getBusinessModule().name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
    }

    @Override
    public void customFlushData(ProjPricingBaseInfo reviewBaseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (ProjPricingLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, reviewBaseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        ProjPricingBaseInfo baseInfo = projPricingBaseInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = findNewestVersion(mainId);
        if (Objects.isNull(newestVersion)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        for (ProjPricingLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(mainId)) {
                ChangeDTO moduleChangeDTO = libHandler.checkActualChange(newestVersion);
                if (Boolean.TRUE.equals(moduleChangeDTO.getNeedApprovalChangeFlag())) {
                    // 快速返回
                    changeDTO.setChangeFlag(true);
                    changeDTO.setNeedApprovalChangeFlag(true);
                    return changeDTO;
                } else if (Boolean.TRUE.equals(moduleChangeDTO.getChangeFlag())) {
                    // 还需要找到最坏情况
                    changeDTO.setChangeFlag(true);
                }
            }
        }
        return changeDTO;
    }

    public ChangeDTO checkPricingChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        ProjPricingBaseInfo baseInfo = projPricingBaseInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getModule, BusinessModuleEnum.PROJ_PRICING.name())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
//                .eq(CommonVersion::getFlowType, "pricing")
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));

        if (Objects.isNull(newestVersion)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        for (ProjPricingLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(mainId)) {
//                // 跳过基本信息数据的检查
//                if (libHandler instanceof ProjPricingBaseInfoLibHandler) {
//                    continue;
//                }
                ChangeDTO moduleChangeDTO = libHandler.checkActualChange(newestVersion);
                if (Boolean.TRUE.equals(moduleChangeDTO.getNeedApprovalChangeFlag())) {
                    // 快速返回
                    changeDTO.setChangeFlag(true);
                    changeDTO.setNeedApprovalChangeFlag(true);
                    return changeDTO;
                } else if (Boolean.TRUE.equals(moduleChangeDTO.getChangeFlag())) {
                    // 还需要找到最坏情况
                    changeDTO.setChangeFlag(true);
                }
            }
        }
        return changeDTO;
    }

    @Override
    public void customReset(ProjPricingBaseInfo reviewBaseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (ProjPricingLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(reviewBaseInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        ProjPricingBaseInfo baseInfo = projPricingBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (ProjPricingLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(baseInfo.getId())) {
                CommonVersionDiffBO commonVersionDiffBO = libHandler.libCompareLib(newVersion, oldVersion);
                oldData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getBeforeData());
                newData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getAfterData());
                moduleChanged.put(libHandler.getSubModule().name(), commonVersionDiffBO.getModuleChanged());
            }
        }
        versionDiffRSP.setOldData(oldData);
        versionDiffRSP.setNewData(newData);
        versionDiffRSP.setModuleChanged(moduleChanged);
        return versionDiffRSP;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, ProjPricingBaseInfo baseModel, Map<Long, String> userNameMap) {
        ProjReviewVersionListRSP rsp = BeanUtil.copyProperties(cv, ProjReviewVersionListRSP.class);
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(MithrasConstants.DEFAULT_USER_NAME));
        rsp.setProjName(baseModel.getProjName());
        return rsp;
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.PROJ_PRICING;
    }

    public Map<String, DiffValue> projPricingReviewBaseInfoCompare(ProjPricingCompareREQ req) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isEmpty(projPricingBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ProjReviewBaseInfo reviewBaseInfo = ProjReviewBaseInfoService.getReviewByPricing(projPricingBaseInfo);
        ProjReviewBaseInfoLib reviewBaseInfoLib = null;
        if (reviewBaseInfo != null) {
            reviewBaseInfoLib = SpringContextHolder.getBean(ProjReviewBaseInfoLibService.class).getEffectLatestOne(reviewBaseInfo.getId());
        }
        if (reviewBaseInfoLib != null) {
            ProjReviewBaseInfoDetailRSP reviewBaseInfoDetailRSP = SpringContextHolder.getBean(ProjReviewBaseInfoLibService.class).detail(reviewBaseInfo.getId(), reviewBaseInfoLib.getVersion());
            ProjPricingBaseInfoDetailRSP baseInfoRSP = SpringContextHolder.getBean(ProjPricingBaseInfoService.class).detail(projPricingBaseInfo.getId(), null);
            ProjPricingBaseInfoDetailRSP oldBaseInfoRSP = BeanUtil.copyProperties(reviewBaseInfoDetailRSP, ProjPricingBaseInfoDetailRSP.class);
            return CompareUtil.compare(baseInfoRSP, oldBaseInfoRSP);
        } else {
            return editdataCompareController.projPricingBaseInfoEditdataCompare(new ProjPricingBaseInfoDetailREQ(req.getId(), req.getProcessInstanceId())).getData();
        }
    }

    public Map<String, DiffValue> projPricingReviewPriceCompare(ProjPricingCompareREQ req) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(req.getId());
        ProjReviewBaseInfo reviewBaseInfo = ProjReviewBaseInfoService.getReviewByPricing(projPricingBaseInfo);
        ProjReviewBaseInfoLib reviewBaseInfoLib = null;
        if (reviewBaseInfo != null) {
            reviewBaseInfoLib = SpringContextHolder.getBean(ProjReviewBaseInfoLibService.class).getEffectLatestOne(reviewBaseInfo.getId());
        }
        if (reviewBaseInfoLib != null) {
            Long projId = reviewBaseInfoLib.getOriginId();
            ProjReviewPriceDetailRSP projReviewPriceDetailRSP = SpringContextHolder.getBean(ProjReviewPriceService.class).oldDetail(projId);
            if (ObjectUtil.isNotEmpty(projReviewPriceDetailRSP)) {
                if (ObjectUtil.isNotEmpty(projReviewPriceDetailRSP.getAocPriceDetailRSP())) {
                    ProjPricingAocPrice pricingAocPrice = SpringContextHolder.getBean(ProjPricingAocPriceService.class).getByProjectId(req.getId());
                    ProjPricingAocPrice aocPriceOld = BeanUtil.copyProperties(projReviewPriceDetailRSP.getAocPriceDetailRSP(), ProjPricingAocPrice.class);
                    return CompareUtil.compare(pricingAocPrice, aocPriceOld);
                }
                if (ObjectUtil.isNotEmpty(projReviewPriceDetailRSP.getLeasePriceDetailRSP())) {
                    ProjPricingLeasePrice pricingLeasePrice = SpringContextHolder.getBean(ProjPricingLeasePriceService.class).getByProjectId(req.getId());
                    ProjPricingLeasePrice leasePriceOld = BeanUtil.copyProperties(projReviewPriceDetailRSP.getLeasePriceDetailRSP(), ProjPricingLeasePrice.class);
                    return CompareUtil.compare(pricingLeasePrice, leasePriceOld);
                }
                if (ObjectUtil.isNotEmpty(projReviewPriceDetailRSP.getFactoringPriceDetailRSP())) {
                    ProjPricingFactoringPrice pricingFactoringPrice = SpringContextHolder.getBean(ProjPricingFactoringPriceService.class).getByProjectId(req.getId());
                    ProjPricingFactoringPrice pricingFactoringPriceOld = BeanUtil.copyProperties(projReviewPriceDetailRSP.getFactoringPriceDetailRSP(), ProjPricingFactoringPrice.class);
                    return CompareUtil.compare(pricingFactoringPrice, pricingFactoringPriceOld);
                }
            }
        } else {
            ProjReviewPriceCompareRSP data = editdataCompareController.projPricingPriceEditdataCompare(new ProjPricingPriceDetailREQ(req.getId(), req.getProcessInstanceId())).getData();
            if (data.getLeasePriceDetailRSP() != null) {
                return data.getLeasePriceDetailRSP();
            } else if (data.getAocPriceDetailRSP() != null) {
                return data.getAocPriceDetailRSP();
            } else if (data.getFactoringPriceDetailRSP() != null) {
                return data.getFactoringPriceDetailRSP();
            }
        }
        return new HashMap<>();

    }

    public List<Map<String, DiffValue>> projPricingReviewCashFlowPlanCompare(ProjPricingCompareREQ req) {
        List<Map<String, DiffValue>> result = new ArrayList<>();
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(req.getId());
        ProjReviewBaseInfo reviewBaseInfo = ProjReviewBaseInfoService.getReviewByPricing(projPricingBaseInfo);
        ProjReviewBaseInfoLib reviewBaseInfoLib = null;
        if (reviewBaseInfo != null) {
            reviewBaseInfoLib = SpringContextHolder.getBean(ProjReviewBaseInfoLibService.class).getEffectLatestOne(reviewBaseInfo.getId());
        }
        if (reviewBaseInfoLib != null) {
            ProjReviewCashFlowPlanLib lastVersion = reviewCashFlowPlanLibService.getOne(Wrappers.<ProjReviewCashFlowPlanLib>lambdaQuery().eq(ProjReviewCashFlowPlanLib::getProjectId, reviewBaseInfoLib.getOriginId())
                    .eq(ProjReviewCashFlowPlanLib::getVersionType, VersionTypeConstants.NORMAL).orderByDesc(ProjReviewCashFlowPlanLib::getVersion).last(StringUtil.mysqlLimitOne()));
            // 评审的现金流
            if(lastVersion != null) {
                List<ProjReviewCashFlowPlanLib> reviewCashFlowPlanLibList = reviewCashFlowPlanLibService.listByProjReviewIdAndVersion(reviewBaseInfoLib.getOriginId(), lastVersion.getVersion());
                if (CollectionUtils.isNotEmpty(reviewCashFlowPlanLibList)) {
                    // 本次变更定价的现金流
                    List<ProjPricingCashFlowPlan> pricingCashFlowPlanList = pricingCashFlowPlanService.listByProjPricingId(projPricingBaseInfo.getId(), null);
                    for (int i = 0; i < pricingCashFlowPlanList.size(); i++) {
                        ProjPricingCashFlowPlan pricingCashFlowPlan = new ProjPricingCashFlowPlan();
                        try {
                            ProjReviewCashFlowPlanLib projReviewCashFlowPlanLib = Optional.ofNullable(reviewCashFlowPlanLibList.get(i)).orElse(new ProjReviewCashFlowPlanLib());
                            pricingCashFlowPlan = BeanUtil.copyProperties(projReviewCashFlowPlanLib, ProjPricingCashFlowPlan.class);
                            pricingCashFlowPlan.setId(projReviewCashFlowPlanLib.getOriginId());
                        }catch (IndexOutOfBoundsException e){
                            // 数组越界 - 直接用空对象去比较
                        }
                        Map<String, DiffValue> compare = CompareUtil.compare(ProjPricingCashFlowPlanConverter.toCashFlowRSP(pricingCashFlowPlanList.get(i)), ProjPricingCashFlowPlanConverter.toCashFlowRSP(pricingCashFlowPlan));
                        result.add(compare);
                    }
                    return result;
                }
            }
        }
        return editdataCompareController.projPricingCashFlowPlanEditdataCompare(new ProjPricingCashFlowPlanListREQ(req.getId(), req.getProcessInstanceId())).getData();



    }


}
