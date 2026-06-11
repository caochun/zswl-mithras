package cn.zswltech.mithras.leaseholdproperty.application.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.leaseholdproperty.mapper.LeaseItemInfoMapper;
import cn.zswltech.mithras.leaseholdproperty.mapper.TycAppraisalCompanyBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.leaseholdproperty.model.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemAppraisalRelation;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemInfo;
import cn.zswltech.mithras.leaseholdproperty.model.TycAppraisalCompanyBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.leaseholdproperty.application.AppraisalCompanyWhitelistService;
import cn.zswltech.mithras.leaseholdproperty.application.impl.LeaseItemAppraisalRelationService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseAppraisalService;
import cn.zswltech.mithras.third.tianyancha.application.TycService;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasBaseInfo;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasCompanyInfo;
import cn.zswltech.mithras.third.tianyancha.application.dto.TycQueryCompanyReq;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeaseAppraisalServiceImpl implements LeaseAppraisalService {

    @Resource
    private LeaseItemAppraisalRelationService relationService;
    @Resource
    private TycAppraisalCompanyBaseInfoMapper companyBaseInfoMapper;
    @Resource
    private TycService tycService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private LeaseItemInfoMapper itemInfoMapper;

    @Override
    public List<LeaseAppraisalItemListRSP> appraisalLeaseList(LeaseAppraisalItemListREQ req) {
        List<LeaseItemAppraisalRelation> relationList = relationService.list(Wrappers.<LeaseItemAppraisalRelation>lambdaQuery()
                .eq(LeaseItemAppraisalRelation::getLeaseItemId, req.getLeaseItemId())
                .orderByDesc(LeaseItemAppraisalRelation::getUpdateTime));
        if(CollectionUtils.isEmpty(relationList)) {
            return new ArrayList<>();
        }
        List<Long> companyIdList = relationList.stream().map(LeaseItemAppraisalRelation::getCompanyId).collect(Collectors.toList());
        List<TycAppraisalCompanyBaseInfo> companyBaseInfoList = companyBaseInfoMapper.selectBatchIds(companyIdList);
        Map<Long, TycAppraisalCompanyBaseInfo> companyBaseInfoMap = companyBaseInfoList.stream().collect(Collectors.toMap(TycAppraisalCompanyBaseInfo::getId, Function.identity()));
        return relationList.stream().map(result -> {
            TycAppraisalCompanyBaseInfo companyBaseInfo = companyBaseInfoMap.get(result.getCompanyId());
            LeaseAppraisalItemListRSP rsp = BeanUtil.copyProperties(result, LeaseAppraisalItemListRSP.class);
            rsp.setCompanyName(Optional.ofNullable(companyBaseInfo).map(TycAppraisalCompanyBaseInfo::getCompanyName).orElse(null));
            return rsp;
        }).collect(Collectors.toList());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long appraisalAdd(LeaseAppraisalAddREQ req) {
        TycAppraisalCompanyBaseInfo exist = companyBaseInfoMapper.selectOne(Wrappers.<TycAppraisalCompanyBaseInfo>lambdaQuery()
                .eq(TycAppraisalCompanyBaseInfo::getCreditCode, req.getCreditCode()));
        if(exist != null){
            // 更新最新信息
            appraisalLasted(new LeaseAppraisalLastedREQ(req.getCreditCode()));
            return exist.getId();
        }

        MithrasBaseInfo baseInfo = tycService.baseInfo(req.getCreditCode());
        if(baseInfo == null){
            throw new MithrasException("未查询到该机构的详细信息，添加失败");
        }
        TycAppraisalCompanyBaseInfo companyBaseInfo = BeanUtil.copyProperties(baseInfo, TycAppraisalCompanyBaseInfo.class);
        companyBaseInfo.setCompanyName(baseInfo.getClientName());
        companyBaseInfo.setCreditCode(req.getCreditCode());
        companyBaseInfoMapper.insert(companyBaseInfo);
        return companyBaseInfo.getId();

    }

    @Override
    public List<LeaseAppraisalQueryCompanyRSP> appraisalQueryCompany(LeaseAppraisalQueryCompanyREQ req) {
        if(req.getCompanyName() != null) {
            TycQueryCompanyReq tycReq = new TycQueryCompanyReq();
            tycReq.setCompanyName(req.getCompanyName());
            tycReq.setPage(req.getPage());
            tycReq.setPageSize(req.getPageSize());
            List<MithrasCompanyInfo> baseInfoList = tycService.queryByCompanyName(tycReq);
            if (CollectionUtils.isNotEmpty(baseInfoList)) {
                return baseInfoList.stream().filter(f -> ignoreBracket(f.getCompanyName()).contains(ignoreBracket(req.getCompanyName())))
                        .map(baseInfo -> BeanUtil.copyProperties(baseInfo, LeaseAppraisalQueryCompanyRSP.class))
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    public String ignoreBracket(String str){
        if(StringUtils.isNotBlank(str)){
            String str1 = str.replaceAll("（", "");
            return str1.replaceAll("）", "");
        }
        return "";
    }

    @Override
    public LeaseAppraisalDetailRSP detail(LeaseAppraisalDetailREQ req) {
        TycAppraisalCompanyBaseInfo companyBaseInfo = companyBaseInfoMapper.selectById(req.getCompanyId());
        LeaseAppraisalDetailRSP rsp = BeanUtil.copyProperties(companyBaseInfo, LeaseAppraisalDetailRSP.class);
        rsp.setCompanyId(req.getCompanyId());
        return rsp;
    }

    @Override
    public PageR<LeaseAppraisalCompanyListRSP> appraisalCompanyList(LeaseAppraisalCompanyListREQ req) {
        Page<TycAppraisalCompanyBaseInfo> page = companyBaseInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<TycAppraisalCompanyBaseInfo>lambdaQuery().like(req.getCompanyName() != null, TycAppraisalCompanyBaseInfo::getCompanyName, req.getCompanyName()));
        List<TycAppraisalCompanyBaseInfo> records = page.getRecords();
        List<LeaseAppraisalCompanyListRSP> rspList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(records)){
            Set<String> uscCodes = records.stream().map(TycAppraisalCompanyBaseInfo::getCreditCode).collect(Collectors.toSet());
            List<AppraisalCompanyWhitelist> whitelistList = SpringUtil.getBean(AppraisalCompanyWhitelistService.class).list(
                    Wrappers.<AppraisalCompanyWhitelist>lambdaQuery().in(AppraisalCompanyWhitelist::getUscCode, uscCodes).eq(AppraisalCompanyWhitelist::getRecordStatus, RecordStatus.TAKE_EFFECT.name())
            );
            Set<String> whitelistUscCodes = whitelistList.stream().map(AppraisalCompanyWhitelist::getUscCode).collect(Collectors.toSet());
            rspList = records.stream().map(result -> {
                LeaseAppraisalCompanyListRSP rsp = new LeaseAppraisalCompanyListRSP();
                rsp.setCompanyId(result.getId());
                rsp.setCompanyName(result.getCompanyName());
                if (whitelistUscCodes.contains(result.getCreditCode())) {
                    rsp.setIsWhitelist(YesOrNoNumberEnum.YES.getCode());
                } else {
                    rsp.setIsWhitelist(YesOrNoNumberEnum.NO.getCode());
                }
                return rsp;
            }).collect(Collectors.toList());
        }
        return PageR.of(rspList, page.getTotal());
    }

    @Override
    public void appraisalLasted(LeaseAppraisalLastedREQ req) {
        MithrasBaseInfo baseInfo = tycService.baseInfo(req.getCreditCode());
        TycAppraisalCompanyBaseInfo companyBaseInfo = companyBaseInfoMapper.selectOne(Wrappers.<TycAppraisalCompanyBaseInfo>lambdaQuery()
                .eq(TycAppraisalCompanyBaseInfo::getCreditCode, req.getCreditCode()).orderByDesc(TycAppraisalCompanyBaseInfo::getCreateTime).last("limit 1"));
        companyBaseInfo.setCompanyName(baseInfo.getClientName());
        companyBaseInfo.setEstablishDate(baseInfo.getEstablishDate());
        companyBaseInfo.setBizLicenseEndDate(baseInfo.getBizLicenseEndDate());
        companyBaseInfo.setBizLicenceLongTerm(baseInfo.getBizLicenceLongTerm());
        companyBaseInfo.setBizScope(baseInfo.getBizScope());

        companyBaseInfoMapper.updateById(companyBaseInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void relation(LeaseAppraisalRelationREQ req) {
        // 前端会直接传递整个列表，对已有数据进行覆盖即可
        relationService.update(Wrappers.<LeaseItemAppraisalRelation>lambdaUpdate()
                .eq(LeaseItemAppraisalRelation::getLeaseItemId, req.getLeaseItemId())
                .set(LeaseItemAppraisalRelation::getDeleted, Boolean.TRUE));
        if(CollectionUtils.isNotEmpty(req.getRelationList())) {
            List<LeaseItemAppraisalRelation> newRelationList = req.getRelationList().stream().map(relation -> {
                LeaseItemAppraisalRelation newRelation = BeanUtil.copyProperties(relation, LeaseItemAppraisalRelation.class);
                return newRelation.setLeaseItemId(req.getLeaseItemId());
            }).collect(Collectors.toList());
            relationService.saveBatch(newRelationList);
        }


    }

    /**
     * 根据合同id查询关联的评估机构
     * @param contractId 合同id
     * @return List<LeaseItemAppraisalRelation>
     */
    @Override
    public List<LeaseItemAppraisalRelation> queryLastestListByContractId(Long contractId) {
        if (ObjectUtil.isNull(contractId)) {
            throw new MithrasException("合同id不能为空");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));
        // 找到最新审批通过的租赁物流程
        LeaseItemInfo leaseItemInfo = itemInfoMapper.queryLastestListByContractId(contractBaseInfo.getProjReviewId());
        if (ObjectUtil.isNull(leaseItemInfo)) {
            return Collections.emptyList();
        }
        return relationService.list(Wrappers.<LeaseItemAppraisalRelation>lambdaQuery()
                .eq(LeaseItemAppraisalRelation::getLeaseItemId, leaseItemInfo.getId()));
    }
}
