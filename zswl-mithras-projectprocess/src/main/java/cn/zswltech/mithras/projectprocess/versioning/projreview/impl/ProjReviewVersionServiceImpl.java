package cn.zswltech.mithras.projectprocess.versioning.projreview.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.datacompare.ProjReviewPriceCompareRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionListRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewCompareREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailREQ;
import cn.zswltech.mithras.dto.version.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.datacompare.EditdataCompareApplicationService;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewCashFlowPlanConverter;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.model.projpricing.*;
import cn.zswltech.mithras.projectprocess.model.projreview.*;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.projectprocess.versioning.ProjectProcessVersionServicePort;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.ProjPricingBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.ProjPricingCashFlowPlanLibService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.ProjReviewLibAbstractHandler;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import cn.zswltech.mithras.foundation.util.CompareUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.util.VersionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description
 * @since
 */
@Service
public class ProjReviewVersionServiceImpl extends CommonVersionService<ProjReviewBaseInfo> {
    private static final String DEFAULT_USER_NAME = "未知用户";
    private static final String BUSINESS_MODULE = "PROJ_REVIEW";

    @Resource
    private List<ProjReviewLibAbstractHandler> libHandlerList;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;
    @Resource
    private ProjectProcessVersionServicePort versionServicePort;
    @Resource
    private ProjPricingBaseInfoLibService pricingBaseInfoLibService;
    @Resource
    private ProjPricingCashFlowPlanLibService pricingCashFlowPlanLibService;
    @Resource
    private EditdataCompareApplicationService editdataCompareController;

    @Transactional(rollbackFor = Exception.class)
    public void reset(Long mainId) {
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(mainId);
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

    /**
     * TODO 之后清理掉 现在所有流程结束，不管通过不通过 都要生成版本
     *
     * @param mainId
     * @param type
     * @param operatorId
     * @param processInstanceId
     */
    @Deprecated
    public void recordPricingVersion(Long mainId, VersionTypeEnum type, Long operatorId, String processInstanceId) {
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion oleCommonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getFlowType, "pricing")
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        // 重复生效不新增版本，把各版本表该版本旧数据清空 并插入新数据
        String version;
        boolean needClearLastFlag = false;
        if (ObjectUtil.isEmpty(oleCommonVersion)) {
            CommonVersion newCommonVersion = CommonVersion.builder()
                    .mainId(mainId)
                    .type(type.getType())
                    .module(BUSINESS_MODULE)
                    .version(VersionUtil.generateVersion(null))
                    .processInstanceId(processInstanceId)
                    .flowType("pricing")
                    .build();
            newCommonVersion.setCreateBy(operatorId);
            newCommonVersion.setUpdateBy(operatorId);
            commonVersionMapper.insert(newCommonVersion);
            version = newCommonVersion.getVersion();
        } else {
            LambdaUpdateWrapper<CommonVersion> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CommonVersion::getId, oleCommonVersion.getId());
            updateWrapper.set(CommonVersion::getUpdateTime, LocalDateTime.now());
            updateWrapper.set(CommonVersion::getUpdateBy, operatorId);
            commonVersionMapper.update(null, updateWrapper);
            version = oleCommonVersion.getVersion();
            needClearLastFlag = true;
        }
        // 处理抄表逻辑
        for (ProjReviewLibAbstractHandler libHandler : libHandlerList) {
            // TODO
            libHandler.flushData(version, mainId, needClearLastFlag, VersionTypeConstants.NORMAL);
        }
    }

    @Override
    public void customFlushData(ProjReviewBaseInfo reviewBaseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (ProjReviewLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, reviewBaseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(mainId);
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
        for (ProjReviewLibAbstractHandler libHandler : libHandlerList) {
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
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getModule, BUSINESS_MODULE)
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
        for (ProjReviewLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(mainId)) {
                // 跳过基本信息数据的检查
                if (libHandler instanceof ProjReviewBaseInfoLibHandler) {
                    continue;
                }
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
    public void customReset(ProjReviewBaseInfo reviewBaseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (ProjReviewLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(reviewBaseInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (ProjReviewLibAbstractHandler libHandler : libHandlerList) {
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
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, ProjReviewBaseInfo baseModel, Map<Long, String> userNameMap) {
        ProjReviewVersionListRSP rsp = BeanUtil.copyProperties(cv, ProjReviewVersionListRSP.class);
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(DEFAULT_USER_NAME));
        rsp.setProjName(baseModel.getProjName());
        return rsp;
    }

    @Override
    protected String getBusinessModuleName() {
        return BUSINESS_MODULE;
    }

    public Map<String, DiffValue> projReviewPricingBaseInfoCompare(ProjReviewCompareREQ req) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(req.getId());
        ProjPricingBaseInfo pricingBaseInfo = versionServicePort.getPricingByReview(projReviewBaseInfo);
        ProjPricingBaseInfoLib pricingBaseInfoLib = null;
        if (pricingBaseInfo != null) {
            pricingBaseInfoLib = pricingBaseInfoLibService.getEffectLatestOne(pricingBaseInfo.getId());
        }
        if (pricingBaseInfoLib != null) {
            ProjPricingBaseInfoDetailRSP pricingDetailRSP = pricingBaseInfoLibService.detail(pricingBaseInfo.getId(), pricingBaseInfoLib.getVersion());
            ProjReviewBaseInfoDetailRSP baseInfoRSP = versionServicePort.getReviewBaseInfoDetail(projReviewBaseInfo.getId(), null);
            ProjReviewBaseInfoDetailRSP oldBaseInfoRSP = BeanUtil.copyProperties(pricingDetailRSP, ProjReviewBaseInfoDetailRSP.class);
            return CompareUtil.compare(baseInfoRSP, oldBaseInfoRSP);
        } else {
            return editdataCompareController.projReviewBaseInfoEditdataCompare(new ProjReviewBaseInfoDetailREQ(req.getId(), req.getProcessInstanceId())).getData();
        }

    }

    public Map<String, DiffValue> projReviewPricingPriceCompare(ProjReviewCompareREQ req) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(req.getId());
        ProjPricingBaseInfo pricingBaseInfo = versionServicePort.getPricingByReview(projReviewBaseInfo);
        ProjPricingBaseInfoLib PricingBaseInfoLib = null;
        if (pricingBaseInfo != null) {
            // 判断对应的定价有没有已经生效的数据
            PricingBaseInfoLib = pricingBaseInfoLibService.getEffectLatestOne(pricingBaseInfo.getId());
        }
        if (PricingBaseInfoLib != null) {
            Long projId = PricingBaseInfoLib.getOriginId();
            ProjPricingPriceDetailRSP projPricingPriceDetailRSP = versionServicePort.getPricingOldPriceDetail(projId);
            if (ObjectUtil.isNotEmpty(projPricingPriceDetailRSP)) {
                if (ObjectUtil.isNotEmpty(projPricingPriceDetailRSP.getAocPriceDetailRSP())) {
                    ProjReviewAocPrice reviewAocPrice = versionServicePort.getReviewAocPrice(req.getId());
                    reviewAocPrice.setApprovedAmount(reviewAocPrice.getProjectApprovalAmount());
                    ProjReviewAocPrice reviewAocPriceOld = BeanUtil.copyProperties(projPricingPriceDetailRSP.getAocPriceDetailRSP(), ProjReviewAocPrice.class);
                    return CompareUtil.compare(reviewAocPrice, reviewAocPriceOld);
                }
                if (ObjectUtil.isNotEmpty(projPricingPriceDetailRSP.getLeasePriceDetailRSP())) {
                    ProjReviewLeasePrice reviewLeasePrice = versionServicePort.getReviewLeasePrice(req.getId());
                    reviewLeasePrice.setApprovedAmount(reviewLeasePrice.getProjectApprovalAmount());
                    ProjReviewLeasePrice reviewLeasePriceOld = BeanUtil.copyProperties(projPricingPriceDetailRSP.getLeasePriceDetailRSP(), ProjReviewLeasePrice.class);
                    return CompareUtil.compare(reviewLeasePrice, reviewLeasePriceOld);
                }
                if (ObjectUtil.isNotEmpty(projPricingPriceDetailRSP.getFactoringPriceDetailRSP())) {
                    ProjReviewFactoringPrice reviewFactoringPrice = versionServicePort.getReviewFactoringPrice(req.getId());
                    reviewFactoringPrice.setApprovedAmount(reviewFactoringPrice.getProjectApprovalAmount());
                    ProjReviewFactoringPrice reviewFactoringPriceOld = BeanUtil.copyProperties(projPricingPriceDetailRSP.getFactoringPriceDetailRSP(), ProjReviewFactoringPrice.class);
                    return CompareUtil.compare(reviewFactoringPrice, reviewFactoringPriceOld);
                }
            }
        } else {
            ProjReviewPriceCompareRSP data = editdataCompareController.projReviewPriceEditdataCompare(new ProjReviewPriceDetailREQ(req.getId(), req.getProcessInstanceId())).getData();
            if (data.getFactoringPriceDetailRSP() != null) {
                return data.getFactoringPriceDetailRSP();
            } else if (data.getLeasePriceDetailRSP() != null) {
                return data.getLeasePriceDetailRSP();
            } else if (data.getAocPriceDetailRSP() != null) {
                return data.getAocPriceDetailRSP();
            }
        }
        return new HashMap<>();

    }

    public List<Map<String, DiffValue>> projReviewPricingCashFlowPlanCompare(ProjReviewCompareREQ req) {
        List<Map<String, DiffValue>> result = new ArrayList<>();
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(req.getId());
        ProjPricingBaseInfo PricingBaseInfo = versionServicePort.getPricingByReview(projReviewBaseInfo);
        ProjPricingBaseInfoLib pricingBaseInfoLib = null;
        if (PricingBaseInfo != null) {
            pricingBaseInfoLib = pricingBaseInfoLibService.getEffectLatestOne(PricingBaseInfo.getId());
        }
        if (pricingBaseInfoLib != null) {
            ProjPricingCashFlowPlanLib lastVersion = pricingCashFlowPlanLibService.getOne(Wrappers.<ProjPricingCashFlowPlanLib>lambdaQuery().eq(ProjPricingCashFlowPlanLib::getProjectId, pricingBaseInfoLib.getOriginId())
                    .eq(ProjPricingCashFlowPlanLib::getVersionType, VersionTypeConstants.NORMAL).orderByDesc(ProjPricingCashFlowPlanLib::getVersion).last(StringUtil.mysqlLimitOne()));
            // 评审的现金流
            if(lastVersion != null) {
                List<ProjPricingCashFlowPlanLib> pricingCashFlowPlanLibList = pricingCashFlowPlanLibService.listByProjPricingIdAndVersion(pricingBaseInfoLib.getOriginId(), lastVersion.getVersion());
                if (CollectionUtils.isNotEmpty(pricingCashFlowPlanLibList)) {
                    // 本次变更定价的现金流
                    List<ProjReviewCashFlowPlan> reviewCashFlowPlanList = versionServicePort.listReviewCashFlowPlan(projReviewBaseInfo.getId(), null);
                    for (int i = 0; i < reviewCashFlowPlanList.size(); i++) {
                        ProjReviewCashFlowPlan reviewCashFlowPlan = new ProjReviewCashFlowPlan();
                        try {
                            ProjPricingCashFlowPlanLib projPricingCashFlowPlanLib = Optional.ofNullable(pricingCashFlowPlanLibList.get(i)).orElse(new ProjPricingCashFlowPlanLib());
                            reviewCashFlowPlan = BeanUtil.copyProperties(projPricingCashFlowPlanLib, ProjReviewCashFlowPlan.class);
                            reviewCashFlowPlan.setId(projPricingCashFlowPlanLib.getOriginId());
                        }catch (IndexOutOfBoundsException e){
                            // 数组越界 - 直接用空对象去比较
                        }
                        Map<String, DiffValue> compare = CompareUtil.compare(ProjReviewCashFlowPlanConverter.toCashFlowRSP(reviewCashFlowPlanList.get(i)), ProjReviewCashFlowPlanConverter.toCashFlowRSP(reviewCashFlowPlan));
                        result.add(compare);


                    }
                    return result;
                }
            }
        }
        return editdataCompareController.projReviewCashFlowPlanEditdataCompare(new ProjReviewCashFlowPlanListREQ(req.getId(), req.getProcessInstanceId())).getData();




    }
}
