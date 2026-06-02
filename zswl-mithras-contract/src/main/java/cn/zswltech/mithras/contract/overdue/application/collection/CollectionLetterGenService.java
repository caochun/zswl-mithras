package cn.zswltech.mithras.contract.overdue.application.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.enums.overdue.LetterType;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractLesseeInfo;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectLetterCode;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionAction;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionActionId;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionRepository;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.CollectionLetterGenerator;
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
public class CollectionLetterGenService implements CollectionLetterGenerator {

    @Resource
    private CollectionRepository collectionRepository;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private CollectionLetterRenderer collectionLetterRenderer;
    @Resource
    private CollectionLetterMaterialStore collectionLetterMaterialStore;

    @Override
    public void genLetter(Long actionId) {
        genLetter(new CollectionActionId(actionId));
    }

    public void genLetter(CollectionActionId id) {
        collectionLetterMaterialStore.removeGeneratedCollectionLetters(id.getId());

        CollectionAction action = collectionRepository.findAction(id);
        if (action.getLetterType() == LetterType.Lawyer) {
            if (log.isDebugEnabled()) {
                log.debug("skip gen letter for action:{}, action letter type is Lawyer", action.getActionCode().getCode());
            }
            return;
        }
        if (ObjectUtil.isEmpty(action.getContractIds())) {
            if (log.isDebugEnabled()) {
                log.debug("skip gen letter for action:{}, no specific contracts", action.getActionCode().getCode());
            }
            return;
        }
        List<Long> contractIds = action.getContractIds();
        Map<String, List<ContractLesseeInfo>> lesseeInfos =
                contractBaseInfoMapper.getContractLessees(contractIds).stream()
                        .collect(Collectors.groupingBy(ContractLesseeInfo::getContractCode));

        Map<String, List<ContractGuarantorInfo>> guarantorInfos =
                contractBaseInfoMapper.getContractGuarantors(contractIds).stream()
                        .collect(Collectors.groupingBy(ContractGuarantorInfo::getContractCode));

        LocalDate now = LocalDate.now();
        Integer collectLetterIndex = collectionRepository.findCollectLetterIndex(now.getYear());
        collectionRepository.incrementCollectLetterIndex(now.getYear(), contractIds.size());
        CollectLetterCode collectLetterCode = new CollectLetterCode(now.getYear(), collectLetterIndex);
        for (String contractCode : action.getContractCodes()) {
            List<ContractLesseeInfo> contractLesseeInfos = lesseeInfos.get(contractCode);
            for (ContractLesseeInfo contractLesseeInfo : contractLesseeInfos) {
                contractLesseeInfo.setLetterCode(collectLetterCode.getCode());
                contractLesseeInfo.setGenDate(now);
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    String fileName = collectionLetterRenderer.renderCollectionLetter(os, contractLesseeInfo);
                    collectionLetterMaterialStore.addGeneratedCollectionLetter(new ByteArrayInputStream(os.toByteArray()), fileName, id.getId());
                } catch (Exception e) {
                    log.error("生成催收函发生未知异常[{}]", contractLesseeInfo.getContractCode());
                    throw new MithrasException("生成催收函发生未知异常");
                }

                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    String fileName = collectionLetterRenderer.renderCreditNotificationLetter(os, contractLesseeInfo);
                    collectionLetterMaterialStore.addGeneratedCollectionLetter(new ByteArrayInputStream(os.toByteArray()), fileName, id.getId());
                } catch (Exception e) {
                    log.error("生成征信告知书发生未知异常[{}]", contractLesseeInfo.getContractCode());
                    throw new MithrasException("生成征信告知书发生未知异常");
                }
            }
            List<ContractGuarantorInfo> contractGuarantorInfos = guarantorInfos.get(contractCode);
            int grantorIdx = 1;
            for (ContractGuarantorInfo contractGuarantorInfo : contractGuarantorInfos) {
                contractGuarantorInfo.setLetterCode(collectLetterCode.getCode() + String.format("-%02d", grantorIdx++));
                contractGuarantorInfo.setGenDate(now);
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    String fileName = collectionLetterRenderer.renderJointLiabilityNotice(os, contractGuarantorInfo);
                    collectionLetterMaterialStore.addGeneratedCollectionLetter(new ByteArrayInputStream(os.toByteArray()), fileName, id.getId());
                } catch (Exception e) {
                    log.error("生成履行连带责任保证通知书发生未知异常[{}]", contractGuarantorInfo.getContractCode());
                    throw new MithrasException("生成履行连带责任保证通知书发生未知异常");
                }
            }
            collectLetterCode = collectLetterCode.nextCode();
        }
    }
}
