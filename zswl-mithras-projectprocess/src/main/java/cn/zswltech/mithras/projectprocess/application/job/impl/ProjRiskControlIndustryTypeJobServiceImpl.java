package cn.zswltech.mithras.projectprocess.application.job.impl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.projectprocess.application.job.ProjRiskControlIndustryTypeJobService;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/5/15 18:20
 * @description
 */
@Slf4j
@Component
public class ProjRiskControlIndustryTypeJobServiceImpl implements ProjRiskControlIndustryTypeJobService {

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoLibService projEstablishBaseInfoLibService;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;


    @Override
    public void projRiskControlIndustryTypeJob() {
        log.info("projRiskControlIndustryTypeJob start..........");
        // 获取所有的评审
        List<ProjReviewBaseInfo> infoList = projReviewBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        // 存在的
        List<ProjReviewBaseInfo> existList = infoList.stream().filter(item -> item.getRiskControlIndustryClassify() != null).collect(Collectors.toList());
        Map<Long, ProjReviewBaseInfo> existListMap = existList.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getProjEstablishId, Function.identity(), (k1, k2) -> k1));
        // 处理立项
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = new ArrayList<>();
        if (CollUtil.isNotEmpty(existList)) {
            projEstablishBaseInfos = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                    .in(ProjEstablishBaseInfo::getId, existList.stream().map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toList())));
        }
        List<ProjEstablishBaseInfo> updateProjEstablishList = new ArrayList<>();
        for (ProjEstablishBaseInfo establishBaseInfo : projEstablishBaseInfos) {
            if (establishBaseInfo.getRiskControlIndustryClassify() == null) {
                ProjEstablishBaseInfo info = new ProjEstablishBaseInfo();
                info.setId(establishBaseInfo.getId());
                info.setRiskControlIndustryClassify(Optional.ofNullable(existListMap.get(establishBaseInfo.getId()))
                        .map(ProjReviewBaseInfo::getRiskControlIndustryClassify)
                        .orElse(null));
                updateProjEstablishList.add(info);
                projEstablishBaseInfoLibService.lambdaUpdate()
                        .set(ProjEstablishBaseInfoLib::getRiskControlIndustryClassify, info.getRiskControlIndustryClassify())
                        .eq(ProjEstablishBaseInfoLib::getOriginId, establishBaseInfo.getId())
                        .update();
            } else {
                // 理论上有数据的,打个error记录一下
                log.error("projRiskControlIndustryTypeJob riskControlIndustryClassify is not null ===>[{}]", establishBaseInfo.getProjCode());
            }
        }
        if (!updateProjEstablishList.isEmpty()) {
            updateProjEstablishList.forEach(projEstablishBaseInfoMapper::updateById);
            log.info("projRiskControlIndustryTypeJob updateProjEstablishList size:{}", updateProjEstablishList.size());
        }

        // 不存在的
        List<ProjReviewBaseInfo> nonList = infoList.stream().filter(item -> item.getRiskControlIndustryClassify() == null).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(nonList)) {
            log.info("projRiskControlIndustryTypeJob nonList size:{}", nonList.size());
            List<ProjReviewBaseInfo> reviewBaseInfos = new ArrayList<>();
            // 说明立项那边一定没有，直接取客户的更新, 这种从立项到评审全部刷一遍就好了
            List<CorpCommerceInfo> list = corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                    .in(ClientBaseModel::getClientId, nonList.stream().map(ProjReviewBaseInfo::getClientId).collect(Collectors.toList())));
            if (CollUtil.isNotEmpty(list)) {
                Map<Long, CorpCommerceInfo> corpCommerceInfoMap = list.stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, Function.identity(), (k1, k2) -> k1));
                for (ProjReviewBaseInfo projReviewBaseInfo : nonList) {
                    CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMap.get(projReviewBaseInfo.getClientId());
                    if (corpCommerceInfo != null) {
                        ProjReviewBaseInfo info = new ProjReviewBaseInfo();
                        info.setId(projReviewBaseInfo.getId());
                        info.setRiskControlIndustryClassify(corpCommerceInfo.getRiskControlIndustryClassify());
                        reviewBaseInfos.add(info);
                        // 更新立项版本数据
                        projEstablishBaseInfoLibService.lambdaUpdate()
                                .set(ProjEstablishBaseInfoLib::getRiskControlIndustryClassify, corpCommerceInfo.getRiskControlIndustryClassify())
                                .eq(ProjEstablishBaseInfoLib::getOriginId, projReviewBaseInfo.getProjEstablishId())
                                .update();

                        // 更新评审数据
                        projReviewBaseInfoMapper.updateById(info);

                        // 更新评审版本数据
                        projReviewBaseInfoLibService.lambdaUpdate()
                                .set(ProjReviewBaseInfoLib::getRiskControlIndustryClassify, corpCommerceInfo.getRiskControlIndustryClassify())
                                .eq(ProjReviewBaseInfoLib::getOriginId, projReviewBaseInfo.getId())
                                .update();
                    }
                }
            }
            if (CollUtil.isNotEmpty(reviewBaseInfos)) {
                reviewBaseInfos.forEach(projReviewBaseInfoMapper::updateById);
                log.info("projRiskControlIndustryTypeJob reviewBaseInfos size:{}", reviewBaseInfos.size());
            }
        }

        log.info("projRiskControlIndustryTypeJob end..........");
    }
}
