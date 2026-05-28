package cn.zswltech.mithras.service.overdue.domain.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.overdue.CollectionActionFileType;
import cn.zswltech.mithras.service.enums.overdue.LetterType;
import cn.zswltech.mithras.service.gendoc.render.overduecollect.CollectionLetterRender;
import cn.zswltech.mithras.service.gendoc.render.overduecollect.CreditNotificationLetterRender;
import cn.zswltech.mithras.service.gendoc.render.overduecollect.JointLiabilityNoticeRender;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.service.overdue.domain.acl.ContractLesseeInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 催收函生成服务
 * @author: zhaozhengkang
 * @date: 2024/10/25 09:58
 */
@Service
@Slf4j
public class CollectionLetterGenService {

    @Resource
    private CollectionRepository collectionRepository;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionLetterRender collectionLetterRender;
    @Resource
    private CreditNotificationLetterRender creditNotificationLetterRender;
    @Resource
    private JointLiabilityNoticeRender jointLiabilityNoticeRender;
    @Resource
    private MaterialsListService materialsListService;

    public void genLetter(CollectionActionId id) {
        // 先删除原来生成的文件
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, id.getId())
                .eq(MaterialsList::getSystemGenerate, 1)
                .eq(MaterialsList::getIsEdit, YesOrNoNumberEnum.NO.getCode())
                .eq(MaterialsList::getMaterialsType, CollectionActionFileType.COLLECTION.name()));

        // 生成新文件
        CollectionAction action = collectionRepository.findAction(id);
        if (action.getLetterType() == LetterType.Lawyer) {
            if(log.isDebugEnabled()){
                log.debug("skip gen letter for action:{}, action letter type is Lawyer", action.getActionCode().getCode());
            }
            return;
        }
        if(ObjectUtil.isEmpty(action.getContractIds())){
            if(log.isDebugEnabled()){
                log.debug("skip gen letter for action:{}, no specific contracts", action.getActionCode().getCode());
            }
            return;
        }
        List<Long> contractIds = action.getContractIds();
        Map<String, List<ContractLesseeInfo>> lesseeInfos =
                contractBaseInfoService.getContractLessees(contractIds).stream()
                        .collect(Collectors.groupingBy(ContractLesseeInfo::getContractCode));

        Map<String, List<ContractGuarantorInfo>> guarantorInfos =
                contractBaseInfoService.getContractGuarantors(contractIds).stream()
                        .collect(Collectors.groupingBy(ContractGuarantorInfo::getContractCode));

        LocalDate now = LocalDate.now();
        Integer collectLetterIndex = collectionRepository.findCollectLetterIndex(now.getYear());
        collectionRepository.incrementCollectLetterIndex(now.getYear(), contractIds.size());
        // 生成初始催收函编号
        CollectLetterCode collectLetterCode = new CollectLetterCode(now.getYear(),collectLetterIndex);
        for (String contractCode : action.getContractCodes()) {
            // 承租人生成催收函 及 征信告知书
            List<ContractLesseeInfo> lesseeInfos1 = lesseeInfos.get(contractCode);
            for (ContractLesseeInfo contractLesseeInfo : lesseeInfos1) {
                contractLesseeInfo.setLetterCode(collectLetterCode.getCode());
                contractLesseeInfo.setGenDate(now);
                try (ByteArrayOutputStream os = new ByteArrayOutputStream();){
                    String fileName = collectionLetterRender.render(os, contractLesseeInfo);
                    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                    materialsListService.add(is, fileName, id.getId(), CollectionActionFileType.COLLECTION.name(), null, BusinessModuleEnum.OVERDUE_COLLECTION_ACTION.name(), YesOrNoNumberEnum.YES);
                    is.close();
                }catch (Exception e){
                    log.error("生成催收函发生未知异常[{}]", contractLesseeInfo.getContractCode());
                    throw new MithrasException("生成催收函发生未知异常");
                }

                try (ByteArrayOutputStream os = new ByteArrayOutputStream();){
                    String fileName = creditNotificationLetterRender.render(os, contractLesseeInfo);
                    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                    materialsListService.add(is, fileName, id.getId(), CollectionActionFileType.COLLECTION.name(), null, BusinessModuleEnum.OVERDUE_COLLECTION_ACTION.name(), YesOrNoNumberEnum.YES);
                    is.close();
               }catch (Exception e){
                    log.error("生成征信告知书发生未知异常[{}]", contractLesseeInfo.getContractCode());
                    throw new MithrasException("生成征信告知书发生未知异常");
                }
            }
            // 担保人生成 履行连带责任保证通知书
            List<ContractGuarantorInfo> guarantorInfos1 = guarantorInfos.get(contractCode);
            int grantorIdx = 1;
            for (ContractGuarantorInfo contractGuarantorInfo : guarantorInfos1) {
                contractGuarantorInfo.setLetterCode(collectLetterCode.getCode() + String.format("-%02d", grantorIdx++));
                contractGuarantorInfo.setGenDate(now);
                try (ByteArrayOutputStream os = new ByteArrayOutputStream();){
                    String fileName = jointLiabilityNoticeRender.render(os, contractGuarantorInfo);
                    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                    materialsListService.add(is, fileName, id.getId(), CollectionActionFileType.COLLECTION.name(), null, BusinessModuleEnum.OVERDUE_COLLECTION_ACTION.name(), YesOrNoNumberEnum.YES);
                    is.close();
                }catch (Exception e){
                    log.error("生成履行连带责任保证通知书发生未知异常[{}]", contractGuarantorInfo.getContractCode());
                    throw new MithrasException("生成履行连带责任保证通知书发生未知异常");
                }
            }
            // 更新催收函编号，循环处理下一合同
            collectLetterCode = collectLetterCode.nextCode();
        }
    }
}
