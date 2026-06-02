package cn.zswltech.mithras.service.controller.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractFileApi;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.file.*;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeMaterialEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractFileService;
import cn.zswltech.mithras.service.service.contract.ContractTextInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/17
 * @description
 */
@Slf4j
@RestController
public class ContractFileController implements ContractFileApi {
    @Resource
    private ContractFileService contractFileService;
    @Resource
    private ContractTextInfoService contractTextInfoService;

    private ThreadLocal<Long> contractIdThreadLocal = new ThreadLocal<>();

    @Override
    public R<List<ContractFileGroupRSP>> listExchangeMaterialGroup(@Valid ContractSingleIdREQ contractSingleIdREQ) {
        List<MaterialsList> materialsListList = contractFileService.listExchangeMaterial(contractSingleIdREQ.getContractId());
        ContractFileGroupRSP rsp = new ContractFileGroupRSP();
        rsp.setContractType(ContractChangeMaterialEnum.EXCHANGE_MATERIAL.name());
        rsp.setContractTypeName("变更材料");
        if (CollectionUtil.isEmpty(materialsListList)) {
            rsp.setFileList(Collections.emptyList());
        } else {
            List<ContractFileGroupRSP.ContractFile> fileList = materialsListList.stream().map(item -> {
                ContractFileGroupRSP.ContractFile contractFile = new ContractFileGroupRSP.ContractFile();
                contractFile.setId(item.getId());
                contractFile.setName(item.getFilename());
                contractFile.setCreateTimestamp(LocalDateTimeUtil.toEpochMilli(item.getCreateTime()));
                return contractFile;
            }).sorted(new CommonFileSortComparator()).collect(Collectors.toList());
            rsp.setFileList(fileList);
        }
        return R.ok(Collections.singletonList(rsp));
    }

    @Override
    public R<List<ContractFileGroupRSP>> listContractGroup(ContractSingleIdREQ contractSingleIdREQ) {
        Map<String, List<MaterialsList>> map = contractFileService.getContractFileMap(contractSingleIdREQ.getContractId());
        if (CollectionUtils.isEmpty(map)) {
            return R.ok(Collections.emptyList());
        }
        List<ContractTypeEnum> allType = ContractTypeEnum.allEnum();
        allType.sort(Comparator.comparingInt(ContractTypeEnum::getSort));
        List<ContractFileGroupRSP> result = new LinkedList<>();
        for (ContractTypeEnum contractTypeEnum : allType) {
            List<MaterialsList> list = map.get(contractTypeEnum.name());
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            ContractFileGroupRSP contractFileGroupRSP = new ContractFileGroupRSP();
            contractFileGroupRSP.setContractType(contractTypeEnum.name());
            contractFileGroupRSP.setContractTypeName(contractTypeEnum.getDisplay());
            contractFileGroupRSP.setSort(contractTypeEnum.getSort());
            List<ContractFileGroupRSP.ContractFile> fileList = list.stream().map(item -> {
                ContractFileGroupRSP.ContractFile contractFile = new ContractFileGroupRSP.ContractFile();
                contractFile.setId(item.getId());
                contractFile.setName(item.getFilename());
                contractFile.setIsGenerate(item.getSystemGenerate());
                contractFile.setCreateTimestamp(LocalDateTimeUtil.toEpochMilli(item.getCreateTime()));
                return contractFile;
            }).sorted(new CommonFileSortComparator()).collect(Collectors.toList());
            contractFileGroupRSP.setFileList(fileList);
            result.add(contractFileGroupRSP);
        }
        result.sort(Comparator.comparingInt(ContractFileGroupRSP::getSort));
        return R.ok(result);
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Long> upload(ContractFileUploadREQ contractFileUploadREQ) {
        return R.ok(contractFileService.upload(contractFileUploadREQ.getFile(), contractFileUploadREQ.getContractType(), contractFileUploadREQ.getContractId()));
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Void> generate(ContractSingleIdREQ contractSingleIdREQ) {
        try {
            contractIdThreadLocal.set(contractSingleIdREQ.getContractId());
            contractFileService.generate(contractSingleIdREQ);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成合同文本发生未知异常[contractId: {}]", contractSingleIdREQ.getContractId(), e);
            throw new MithrasException("生成合同文本发生未知异常");
        }finally {
            contractIdThreadLocal.remove();
        }
        return R.ok();
    }

    @Override
    public R<Void> remove(@Valid ContractFileRemoveREQ contractFileRemoveREQ) {
        contractFileService.removeContractFile(contractFileRemoveREQ.getId());
        return R.ok();
    }

    @Override
    public R<Void> saveTextInfo(@Valid ContractTextInfoREQ req) {
        contractTextInfoService.saveTextInfo(req);
        return R.ok();
    }

    @Override
    public R<ContractTextInfoRSP> getTextInfo(@Valid ContractSingleIdREQ req) {
        return R.ok(contractTextInfoService.getTextInfo(req.getContractId()));
    }

    public  ThreadLocal<Long> getContractIdThreadLocal() {
        return contractIdThreadLocal;
    }
}
