package cn.zswltech.mithras.application.orchestration.fund;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.dto.materialsfile.FundMaterialListRSP;
import cn.zswltech.mithras.credit.creditlimit.service.CreditLimitManagerService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.fund.application.credit.convert.FundGuaranteeInfoConverter;
import cn.zswltech.mithras.document.enums.SpecialFileBusinessType;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.fund.application.credit.FundCreditGuaranteeDetailService;
import cn.zswltech.mithras.fund.mapper.FundGuaranteeInfoMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.fund.model.FundCredit;
import cn.zswltech.mithras.fund.model.FundGuaranteeAgency;
import cn.zswltech.mithras.fund.model.FundGuaranteeInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.credit.creditlimit.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Service
public class FundGuaranteeInfoService extends ServiceImpl<FundGuaranteeInfoMapper, FundGuaranteeInfo> {

    @Resource
    private FundGuaranteeInfoConverter fundGuaranteeInfoConverter;
    @Resource
    private FundGuaranteeAgencyService agencyService;
    @Resource
    private FundCreditGuaranteeDetailService guaranteeDetailService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FundGuaranteeAgencyService fundGuaranteeAgencyService;
    @Resource
    private CreditLimitManagerService creditLimitManagerService;
    @Resource
    private FundCreditService fundCreditService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Transactional(rollbackFor = Throwable.class)
    public void add(MultipartFile[] files, FundGuaranteeInfoAddREQ req) {
        Long agencyId = req.getAgencyId();
        Map<Long, Long> usedGuaranteeLimit = fundGuaranteeAgencyService.getUsedGuaranteeLimit(
                Collections.singletonList(agencyId));
        if (req.getTotalGuaranteeLimit() < usedGuaranteeLimit.getOrDefault(agencyId, 0L)) {
            throw new MithrasException("担保额度不能低于已使用额度!");
        }
        FundGuaranteeInfo info = fundGuaranteeInfoConverter.addReq2Entity(req);
        FundGuaranteeAgency agency = agencyService.getById(agencyId);
        Integer totalNow = baseMapper.selectCount(
                Wrappers.<FundGuaranteeInfo>lambdaQuery().eq(FundGuaranteeInfo::getAgencyId, agencyId));
        info.setGuaranteeCode(agency.getGuaranteeAgencyCode() + "-" + String.format("%02d", totalNow + 1));
        info.setRecyclable(YesOrNoNumberEnum.YES.getCode());
        FundGuaranteeInfo updateEntity = new FundGuaranteeInfo();
        updateEntity.setEffective(0);
        baseMapper.update(updateEntity, Wrappers.<FundGuaranteeInfo>lambdaQuery().eq(FundGuaranteeInfo::getEffective, 1)
                .eq(FundGuaranteeInfo::getAgencyId, agency.getId()));
        baseMapper.insert(info);
        if (ObjectUtil.isNotEmpty(files)) {
            for (MultipartFile file : files) {
                materialsListService.add(file, info.getId(), "DEFAULT", SpecialFileBusinessType.FUND_GUARANTEE_INFO.name());
            }
        }
        agencyService.updateGuaranteeAgency(info.getAgencyId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(MultipartFile[] addFiles, FundGuaranteeInfoModifyREQ req) {
        FundGuaranteeInfo originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可编辑担保信息");
        }

        Long agencyId = originalInfo.getAgencyId();
        Map<Long, Long> usedGuaranteeLimit = fundGuaranteeAgencyService.getUsedGuaranteeLimit(
                Collections.singletonList(agencyId));
        if (req.getTotalGuaranteeLimit() < usedGuaranteeLimit.getOrDefault(agencyId, 0L)) {
            throw new MithrasException("担保额度不能低于已使用额度!");
        }
        FundGuaranteeInfo info = fundGuaranteeInfoConverter.modifyReq2Entity(req);
        info.setAgencyId(agencyId);
        // 如果将一条信息状态变为生效，其他的生效信息要变为失效
        if (ObjectUtil.isNotEmpty(req.getEffective()) && req.getEffective() == 1) {
            FundGuaranteeInfo updateEntity = new FundGuaranteeInfo();
            updateEntity.setEffective(0);
            baseMapper.update(updateEntity, Wrappers.<FundGuaranteeInfo>lambdaQuery().eq(FundGuaranteeInfo::getEffective, 1)
                    .eq(FundGuaranteeInfo::getAgencyId, agencyId));
        }
        baseMapper.updateById(info);
        agencyService.updateGuaranteeAgency(info.getAgencyId());
        //文件管理
        if (ObjectUtil.isNotEmpty(req.getDelFileIds())) {
            materialsListService.remove(req.getDelFileIds());
        }
        if (ObjectUtil.isNotEmpty(addFiles)) {
            for (MultipartFile addFile : addFiles) {
                materialsListService.add(addFile, info.getId(), null, SpecialFileBusinessType.FUND_GUARANTEE_INFO.name());
            }
        }
    }

    public PageR<FundGuaranteeInfoListRSP> list(FundGuaranteeInfoListREQ req) {
        Page<FundGuaranteeInfo> page =
                baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                        Wrappers.<FundGuaranteeInfo>lambdaQuery()
                                .eq(FundGuaranteeInfo::getAgencyId, req.getAgencyId())
                                .orderByDesc(FundGuaranteeInfo::getEffective)
                                .orderByDesc(FundGuaranteeInfo::getId));
        if (ObjectUtil.isEmpty(page.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
//        Long usedGuaranteeLimit = guaranteeDetailService.getUsedGuaranteeLimit(req.getAgencyId());
        // 计算已使用担保需要考虑已还回去的部分
        Map<Long, Long> usedGuaranteeLimit = fundGuaranteeAgencyService.getUsedGuaranteeLimit(
                Collections.singletonList(req.getAgencyId()));
        List<FundGuaranteeInfoListRSP> rspList = new ArrayList<>();
        page.getRecords().forEach(fundGuaranteeInfo -> {
            FundGuaranteeInfoListRSP rsp = fundGuaranteeInfoConverter.entity2ListRsp(fundGuaranteeInfo);
            //查询文件
            List<MaterialsList> materialsLists = materialsListService.listBy(SpecialFileBusinessType.FUND_GUARANTEE_INFO.name(), rsp.getId());
            List<FundMaterialListRSP> fileList = materialsLists.stream().map(materialsList -> {
                FundMaterialListRSP materialsRsp = new FundMaterialListRSP();
                materialsRsp.setId(materialsList.getId());
                materialsRsp.setFileName(materialsList.getFilename());
                return materialsRsp;
            }).collect(Collectors.toList());
            rsp.setFileList(fileList);
            rsp.setUsedGuaranteeLimit(usedGuaranteeLimit.get(rsp.getAgencyId()));
            if(rsp.getTotalGuaranteeLimit() != null && rsp.getUsedGuaranteeLimit() != null) {
                rsp.setRemainingGuaranteeLimit(rsp.getTotalGuaranteeLimit() - rsp.getUsedGuaranteeLimit());
            }
            StringBuilder sb = new StringBuilder();
            if (ObjectUtil.isNotEmpty(rsp.getEffectiveTimeFrom())) {
                sb.append(rsp.getEffectiveTimeFrom().format(df2));
            }
            sb.append("-");
            if (ObjectUtil.isNotEmpty(rsp.getEffectiveTimeTo())) {
                sb.append(rsp.getEffectiveTimeTo().format(df2));
            }
            rsp.setGuaranteePeriod(sb.toString());
            rspList.add(rsp);
        });
        return PageR.of(page, rspList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FundGuaranteeInfoRemoveREQ req) {
        if (ObjectUtil.isNotEmpty(req.getIds())) {
            baseMapper.deleteBatchIds(req.getIds());
            agencyService.updateGuaranteeAgency(req.getAgencyId());
        }
    }

    public FundGuaranteeInfoDetailRSP detail(Long id) {
        FundGuaranteeInfo fundGuaranteeInfo = baseMapper.selectById(id);
        FundGuaranteeInfoDetailRSP rsp = fundGuaranteeInfoConverter.entity2DetailRsp(fundGuaranteeInfo);
        List<MaterialsList> materialsLists = materialsListService.listBy(SpecialFileBusinessType.FUND_GUARANTEE_INFO.name(), id);
        List<FundMaterialListRSP> fileList = materialsLists.stream().map(materialsList -> {
            FundMaterialListRSP materialsRsp = new FundMaterialListRSP();
            materialsRsp.setId(materialsList.getId());
            materialsRsp.setFileName(materialsList.getFilename());
            return materialsRsp;
        }).collect(Collectors.toList());
        rsp.setFileListRSP(fileList);
        rsp.setUsedGuaranteeLimit(guaranteeDetailService.getUsedGuaranteeLimit(fundGuaranteeInfo.getAgencyId()));
        rsp.setRemainingGuaranteeLimit(rsp.getTotalGuaranteeLimit() - rsp.getUsedGuaranteeLimit());
        return rsp;
    }

    public Optional<List<FundGuaranteeInfo>> selectEffectByAgencyId(Long agencyId) {
        LocalDate now = LocalDate.now();
        List<FundGuaranteeInfo> fundGuaranteeInfos = baseMapper.selectList(Wrappers.<FundGuaranteeInfo>lambdaQuery()
                .eq(FundGuaranteeInfo::getAgencyId, agencyId)
                .ge(FundGuaranteeInfo::getEffectiveTimeFrom, now)
                .le(FundGuaranteeInfo::getEffectiveTimeTo, now));
        return Optional.ofNullable(fundGuaranteeInfos);
    }


    /**
     * 查询各担保机构的剩余担保额度
     *
     * @param agencyIds 担保机构id
     * @return key:担保机构id value:剩余担保额度
     */
    public Map<Long, Long> remainingGuaranteeLimit(Set<Long> agencyIds) {
        if(CollectionUtil.isEmpty(agencyIds)){
            return Collections.emptyMap();
        }
        Map<Long, Long> remainingMap = new HashMap<>();
        //查询各担保机构的总担保额度
        Map<Long, Long> totalGuaranteeLimit = list(Wrappers.<FundGuaranteeInfo>lambdaQuery()
                .in(FundGuaranteeInfo::getAgencyId, agencyIds)
                .eq(FundGuaranteeInfo::getEffective, 1))
                .stream().collect(Collectors.toMap(FundGuaranteeInfo::getAgencyId, FundGuaranteeInfo::getTotalGuaranteeLimit));
        // 查询各机构已使用担保额度
        Map<Long, Long> usedGuaranteeLimit = fundGuaranteeAgencyService.getUsedGuaranteeLimit(new ArrayList<>(agencyIds));
        for (Long guaranteeId : agencyIds) {
            Long total = Optional.ofNullable(totalGuaranteeLimit.get(guaranteeId)).orElse(0L);
            Long used = Optional.ofNullable(usedGuaranteeLimit.get(guaranteeId)).orElse(0L);
            remainingMap.put(guaranteeId, total - used);
        }
        return remainingMap;
    }

}
