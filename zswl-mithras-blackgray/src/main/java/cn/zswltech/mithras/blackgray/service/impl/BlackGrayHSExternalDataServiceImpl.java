package cn.zswltech.mithras.blackgray.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.external.*;
import cn.zswltech.mithras.blackgray.service.BlackGrayExternalDataService;
import cn.zswltech.mithras.blackgray.service.PortraitService;
import cn.zswltech.mithras.blackgray.client.ConcentrationImageServiceImpl;
import cn.zswltech.mithras.blackgray.client.ImageRelationDTO;
import cn.zswltech.mithras.blackgray.client.RelationOuterDataService;
import cn.zswltech.mithras.blackgray.client.dto.ImageTableQry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 获取外部数据
 * @Author jackerhe
 * @Date 2023/12/6 1:46 下午
 * @Version 1.0
 **/
@Service(value = "blackGrayHSExternalDataService")
@Slf4j
public class BlackGrayHSExternalDataServiceImpl implements BlackGrayExternalDataService {

    @Resource
    private PortraitService portraitService;

    @Resource
    private ConcentrationImageServiceImpl concentrationImageService;
    @Resource
    private RelationOuterDataService relationOuterDataService;

    //模糊查询企业信息
    @Override
    public List<VagueEnterpriseSearchRSP> vagueEnterpriseSearch(VagueEnterpriseSearchREQ req){
        R<List<SearchEnterpriseDTO.EnterpriseDTO>> listResponse = portraitService.outerSearch(req.getEnterpriseName(), req.getUnifiedSocialCreditCode());
        List<SearchEnterpriseDTO.EnterpriseDTO> data = listResponse.getData();
        List<VagueEnterpriseSearchRSP> rsps = new ArrayList<VagueEnterpriseSearchRSP>();
        if(ObjectUtil.isEmpty(data)){
            return rsps;
        }
        data.forEach(dto -> {
            VagueEnterpriseSearchRSP rsp = new VagueEnterpriseSearchRSP();
            rsp.setEnterpriseName(dto.getEnterpriseName());
            rsp.setUnifiedSocialCreditCode(dto.getUniformCreditCode());
            if(ObjectUtil.isNotEmpty(rsp.getUnifiedSocialCreditCode())){
                rsps.add(rsp);
            }
        });
        return rsps;
    }

    @Override
    public AffiliatedEnterpriseSearchRSP affiliatedEnterpriseSearch(AffiliatedEnterpriseSearchREQ req) {
        ImageTableQry imageTableQry = new ImageTableQry();
        imageTableQry.setName(req.getEnterpriseName());
        ImageRelationDTO relation = concentrationImageService.relation(imageTableQry);
        AffiliatedEnterpriseSearchRSP rsp = new AffiliatedEnterpriseSearchRSP();
        rsp.setGroupEnterpriseName(relation.getControllerName());
        rsp.setIsAffiliated(isAffiliated(req.getEnterpriseName(), relation.getControllerName()) ? 1 : 0);
        if (StringUtils.isNotBlank(relation.getControllerName())) {
            rsp.setGroupCreditCode(this.getEntCreditCodeByName(rsp.getGroupEnterpriseName()));
        }
        return rsp;
    }

    private String getEntCreditCodeByName(String groupEnterpriseName) {
        HsCompanyInfoDTO hsCompanyInfoDTO = relationOuterDataService.requestCompanyInfo(null, groupEnterpriseName);
        return hsCompanyInfoDTO == null ? null : hsCompanyInfoDTO.getCredit_code();
    }

    private Boolean isAffiliated(String name, String group){
        if(ObjectUtil.isEmpty(group)){
            return false;
        }
        if(StringUtil.equals(name, group)){
            return true;
        }
        String[] splits = group.split(",");
        for(String split : splits){
            if(StringUtil.equals(name, split)){
                return true;
            }
        }
        return false;
    }

    //todo 这里不解析为树，后续替换外部数据源后构建。原因 1.此处使用之前的数据接口，不具备或构建树结果操作较难。2。只是暂时使用，后续会替换
    @Override
    public List<AssociatedEnterpriseSearchRSP> associatedEnterpriseSearch(AssociatedEnterpriseSearchREQ req) {
        List<EntDownHolderDTO> entDownHolderDTOS = relationOuterDataService.entDownHolder(req.getUnifiedSocialCreditCode(), req.getEnterpriseName(), new BigDecimal("0.01"));
        List<AssociatedEnterpriseSearchRSP> rsp = new ArrayList<>();
        if(ObjectUtil.isEmpty(entDownHolderDTOS)){
            return rsp;
        }
        entDownHolderDTOS.forEach(dto -> {
            AssociatedEnterpriseSearchRSP rsp1 = new AssociatedEnterpriseSearchRSP();
            rsp1.setEnterpriseName(dto.getInvest_name());
            rsp1.setUnifiedSocialCreditCode(dto.getEnterprise_info());
            rsp.add(rsp1);
        });
        return rsp;
    }

    @Override
    public List<AssociatedEnterpriseBatchSearchRSP> batchAffiliatedEnterpriseSearch(List<AffiliatedEnterpriseSearchREQ> req) {
        List<AssociatedEnterpriseBatchSearchRSP> list = new ArrayList<>();
        AffiliatedEnterpriseSearchREQ affiliatedEnterpriseSearchREQ;
        for(int i = 0; i < req.size(); i++) {
            affiliatedEnterpriseSearchREQ = req.get(i);
            AffiliatedEnterpriseSearchRSP searchRSP = this.affiliatedEnterpriseSearch(affiliatedEnterpriseSearchREQ);
            if(ObjectUtil.isNotEmpty(searchRSP)){
                AssociatedEnterpriseBatchSearchRSP rsp = new AssociatedEnterpriseBatchSearchRSP();
                rsp.setEnterpriseName(affiliatedEnterpriseSearchREQ.getEnterpriseName());
                rsp.setUnifiedSocialCreditCode(affiliatedEnterpriseSearchREQ.getUnifiedSocialCreditCode());
                rsp.setIsAffiliated(searchRSP.getIsAffiliated());
                rsp.setGroupEnterpriseName(searchRSP.getGroupEnterpriseName());
                rsp.setGroupCreditCode(searchRSP.getGroupCreditCode());
                AssociatedEnterpriseSearchREQ associatedEnterpriseSearchREQ = new AssociatedEnterpriseSearchREQ();
                //todo 暂时获取当前用户
                associatedEnterpriseSearchREQ.setEnterpriseName(affiliatedEnterpriseSearchREQ.getEnterpriseName());
               /* AssociatedEnterpriseSearchRSP rsp1 = this.associatedEnterpriseSearch(associatedEnterpriseSearchREQ);
                rsp.setChildren(rsp1 == null ? null : rsp1.getChildren());*/
                list.add(rsp);
            }
            if (i % 10 == 0) {
                //每十次停顿
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error("批量接口error", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        return list;
    }

}
