package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ZipUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.NewAfterLeaseCheckMaterialsEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.gendoc.render.afterlease.AfterLeaseCheckReportRenderFactory;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportDownloadService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/20
 * @description
 */
@Slf4j
@Service
public class AfterLeaseCheckReportDownloadServiceImpl implements AfterLeaseCheckReportDownloadService {
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;

    @Override
    public void downloadSingleClientReport(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient) {
        Assert.isTrue(Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name()), () -> MithrasException.newException("审批通过后才能下载报告"));
        NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(checkPlanClient.getId());
        Assert.notNull(checkReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.PUBLIC.name(), checkReportMeta.getReportType())) {
            this.doPublic(outputStream, checkPlanClient, checkReportMeta);
        } else if (Objects.equals(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), checkReportMeta.getReportType())) {
            this.doNonPublic(outputStream, checkPlanClient, checkReportMeta);
        } else if (Objects.equals(AfterLeaseCheckReportTypeEnum.LOW_RISK.name(), checkReportMeta.getReportType())) {
            this.doLowRisk(outputStream, checkPlanClient, checkReportMeta);
        } else if (Objects.equals(AfterLeaseCheckReportTypeEnum.BUS.name(), checkReportMeta.getReportType())) {
            this.doBus(outputStream, checkPlanClient, checkReportMeta);
        } else if (Objects.equals(AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(), checkReportMeta.getReportType())) {
            this.doStateOwnedAsset(outputStream, checkPlanClient, checkReportMeta);
        } else {
            throw new MithrasException("非法的检查报告类型");
        }
    }

    @Override
    public void downloadBatchClientReport(OutputStream outputStream, List<Long> checkPlanClientIdList) {
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = afterLeaseCheckPlanClientService.listByIds(checkPlanClientIdList);
        List<NewAfterLeaseCheckReportMeta> checkReportMetaList = afterLeaseCheckReportMetaService.listByCheckPlanClientIds(checkPlanClientIdList);
        Map<Long, NewAfterLeaseCheckReportMeta> checkReportMetaMap = checkReportMetaList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, e -> e));
        List<String> filePaths = new LinkedList<>();
        List<String> tempPathList = new LinkedList<>();
        List<InputStream> tempInputStreamList = new LinkedList<>();
        for (NewAfterLeaseCheckPlanClient checkPlanClient : checkPlanClientList) {
            if (!Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name())) {
                continue;
            }
            NewAfterLeaseCheckReportMeta checkReportMeta = checkReportMetaMap.get(checkPlanClient.getId());
            Assert.notNull(checkReportMeta, () -> MithrasException.newException(checkPlanClient.getClientName() + "的检查报告元数据不存在"));
            if (Objects.equals(AfterLeaseCheckReportTypeEnum.PUBLIC.name(), checkReportMeta.getReportType())) {
                String s = this.generatePublicReportFile(checkReportMeta, checkPlanClient);
                filePaths.add(s);
                int index = s.lastIndexOf("/");
                tempPathList.add(checkPlanClient.getClientName() + "/" + s.substring(index));
                tempInputStreamList.add(FileUtil.getInputStream(s));
            } else if (Objects.equals(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), checkReportMeta.getReportType())) {
                List<String> list = this.generateNonPublicReportFile(checkReportMeta, checkPlanClient);
                filePaths.addAll(list);
                for (String s : list) {
                    int index = s.lastIndexOf("/");
                    tempPathList.add(checkPlanClient.getClientName() + "/" + s.substring(index));
                    tempInputStreamList.add(FileUtil.getInputStream(s));
                }
            } else if (Objects.equals(AfterLeaseCheckReportTypeEnum.LOW_RISK.name(), checkReportMeta.getReportType())) {
                String s = this.generateLowRiskReportFile(checkReportMeta, checkPlanClient);
                filePaths.add(s);
                int index = s.lastIndexOf("/");
                tempPathList.add(checkPlanClient.getClientName() + "/" + s.substring(index));
                tempInputStreamList.add(FileUtil.getInputStream(s));
            }
        }
        if (CollectionUtil.isEmpty(filePaths)) {
            throw new MithrasException("没有可下载的报告");
        }
        String[] paths = new String[tempPathList.size()];
        InputStream[] inputStreams = new InputStream[tempInputStreamList.size()];
        tempPathList.toArray(paths);
        tempInputStreamList.toArray(inputStreams);
        try {
            ZipUtil.zip(outputStream, paths, inputStreams);
        } finally {
            for (String s : filePaths) {
                try {
                    FileUtil.del(s);
                } catch (Exception e) {
                    log.error("删除租后检查报告临时文件发生异常[{}]", s, e);
                }
            }
        }
    }

    @Override
    public void downloadBizDeptClientReport(OutputStream outputStream, Long planId, Long bizDeptId) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanClient::getPlanId, planId);
        query.eq(NewAfterLeaseCheckPlanClient::getIsCheck, YesOrNoNumberEnum.YES.getCode());
        query.eq(NewAfterLeaseCheckPlanClient::getBelongDeptId, bizDeptId);
        query.eq(NewAfterLeaseCheckPlanClient::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name());
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = afterLeaseCheckPlanClientService.list(query);
        Assert.notEmpty(checkPlanClientList, () -> MithrasException.newException("该部门没有可下载的检查报告"));
        List<Long> checkPlanClientIdList = checkPlanClientList.stream().map(NewAfterLeaseCheckPlanClient::getId).collect(Collectors.toList());
        this.downloadBatchClientReport(outputStream, checkPlanClientIdList);
    }

    private void doPublic(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, NewAfterLeaseCheckReportMeta checkReportMeta) {
        // 查询用户上传的财务数据
        List<MaterialsList> materialsListList = materialsListService.list(
                BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name(),
                Collections.singletonList(NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_PUBLIC_FINANCE.name()),
                Collections.singletonList(checkPlanClient.getId())
        );
        // 生成主报告
        String filePath = this.generatePublicReportFile(checkReportMeta, checkPlanClient);
        int size;
        if (CollectionUtil.isEmpty(materialsListList)) {
            size = 1;
        } else {
            size = materialsListList.size() + 1;
        }
        // 打成压缩包
        String[] zipPaths = new String[size];
        InputStream[] zipInputStreams = new InputStream[size];
        zipPaths[0] = filePath.substring(filePath.lastIndexOf("/"));
        zipInputStreams[0] = FileUtil.getInputStream(filePath);
        if (CollectionUtil.isNotEmpty(materialsListList)) {
            for (int i = 0; i < materialsListList.size(); i++) {
                MaterialsList materialsList = materialsListList.get(i);
                zipPaths[i + 1] = materialsList.getFilename();
                zipInputStreams[i + 1] = ossClient.downLoad(materialsList.getOssFilename());
            }
        }
        try {
            ZipUtil.zip(outputStream, zipPaths, zipInputStreams);
        } finally {
            try {
                FileUtil.del(filePath);
            } catch (Exception e) {
                log.error("删除租后检查报告临时文件发生异常[{}]", filePath, e);
            }
        }
    }

    private String generatePublicReportFile(NewAfterLeaseCheckReportMeta checkReportMeta, NewAfterLeaseCheckPlanClient checkPlanClient) {
        String filePath = "/tmp/租后检查报告_" + checkPlanClient.getId() + "_" + System.currentTimeMillis() + GlobalConstants.OFFICE_EXCEL_SUFFIX;
        try (OutputStream outputStream = FileUtil.getOutputStream(filePath)) {
            AfterLeaseCheckReportRenderFactory.getInstance(checkReportMeta.getReportType(), checkReportMeta.getReportTemplateVersion()).render(outputStream, checkPlanClient);
        } catch (Exception e) {
            log.error("生成租后检查报告（公用事业类）发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("生成租后检查报告发生异常");
        }
        return filePath;
    }

    private void doNonPublic(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, NewAfterLeaseCheckReportMeta checkReportMeta) {
        List<String> filePaths = this.generateNonPublicReportFile(checkReportMeta, checkPlanClient);
        String[] zipPaths = new String[filePaths.size()];
        InputStream[] inputStreams = new InputStream[filePaths.size()];
        for (int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            int index = filePath.lastIndexOf("/");
            String fileName = filePath.substring(index);
            zipPaths[i] = fileName;
            inputStreams[i] = FileUtil.getInputStream(filePath);
        }
        try {
            ZipUtil.zip(outputStream, zipPaths, inputStreams);
        } finally {
            for (String s : filePaths) {
                try {
                    FileUtil.del(s);
                } catch (Exception e) {
                    log.error("删除租后检查报告临时文件发生异常[{}]", s, e);
                }
            }
        }
    }

    private List<String> generateNonPublicReportFile(NewAfterLeaseCheckReportMeta checkReportMeta, NewAfterLeaseCheckPlanClient checkPlanClient) {
        // 报告和补充信息压缩后一并返回
        // 报告模板变更后补充信息合并到主报告中
        String nonPublicReportPath = "/tmp/租后检查报告_" + checkPlanClient.getId() + "_" + System.currentTimeMillis() + GlobalConstants.OFFICE_WORD_SUFFIX;
//        String nonPublicReportExtraPath = "/tmp/租后检查报告补充说明_" + checkPlanClient.getId() + "_" + System.currentTimeMillis() + GlobalConstants.OFFICE_WORD_SUFFIX;
        try (OutputStream outputStream = FileUtil.getOutputStream(nonPublicReportPath)) {
            AfterLeaseCheckReportRenderFactory.getInstance(checkReportMeta.getReportType(), checkReportMeta.getReportTemplateVersion()).render(outputStream, checkPlanClient);
        } catch (Exception e) {
            log.error("生成租后检查报告（产业类）发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("生成租后检查报告发生异常");
        }
//        try (OutputStream outputStream = FileUtil.getOutputStream(nonPublicReportExtraPath)) {
//            afterLeaseCheckReportNonPublicExtraRender.render(outputStream, checkPlanClient);
//        } catch (Exception e) {
//            log.error("生成租后检查报告补充信息（非公用事业类）发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
//            throw new MithrasException("生成租后检查报告发生异常");
//        }
//        return ListUtil.toList(nonPublicReportPath, nonPublicReportExtraPath);
        return Collections.singletonList(nonPublicReportPath);
    }

    private void doLowRisk(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, NewAfterLeaseCheckReportMeta checkReportMeta) {
        String filePath = this.generateLowRiskReportFile(checkReportMeta, checkPlanClient);
        try (InputStream inputStream = FileUtil.getInputStream(filePath)) {
            IoUtil.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("租后检查报告拷贝文件流发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("读取租后检查报告发生异常");
        }
    }
    private void doBus(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, NewAfterLeaseCheckReportMeta checkReportMeta) {
        String filePath = this.generateBusReportFile(checkReportMeta, checkPlanClient);
        try (InputStream inputStream = FileUtil.getInputStream(filePath)) {
            IoUtil.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("租后检查报告拷贝文件流发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("读取租后检查报告发生异常");
        }
    }
    private void doStateOwnedAsset(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, NewAfterLeaseCheckReportMeta checkReportMeta) {
        String filePath = this.generateBusReportFile(checkReportMeta, checkPlanClient);
        try (InputStream inputStream = FileUtil.getInputStream(filePath)) {
            IoUtil.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("租后检查报告拷贝文件流发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("读取租后检查报告发生异常");
        }
    }

    private String generateLowRiskReportFile(NewAfterLeaseCheckReportMeta checkReportMeta, NewAfterLeaseCheckPlanClient checkPlanClient) {
        String filePath = "/tmp/租后检查报告_" + checkPlanClient.getId() + "_" + System.currentTimeMillis() + GlobalConstants.OFFICE_WORD_SUFFIX;
        try (OutputStream outputStream = FileUtil.getOutputStream(filePath)) {
            AfterLeaseCheckReportRenderFactory.getInstance(checkReportMeta.getReportType(), checkReportMeta.getReportTemplateVersion()).render(outputStream, checkPlanClient);
        } catch (Exception e) {
            log.error("生成租后检查报告（低风险）发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("生成租后检查报告发生异常");
        }
        return filePath;
    }

    private String generateBusReportFile(NewAfterLeaseCheckReportMeta checkReportMeta, NewAfterLeaseCheckPlanClient checkPlanClient) {
        String filePath = "/tmp/租后检查报告_" + checkPlanClient.getId() + "_" + System.currentTimeMillis() + GlobalConstants.OFFICE_WORD_SUFFIX;
        try (OutputStream outputStream = FileUtil.getOutputStream(filePath)) {
            AfterLeaseCheckReportRenderFactory.getInstance(checkReportMeta.getReportType(), checkReportMeta.getReportTemplateVersion()).render(outputStream, checkPlanClient);
        } catch (Exception e) {
            log.error("生成租后检查报告（低风险）发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("生成租后检查报告发生异常");
        }
        return filePath;
    }

    private String generateStateOwnedAssetReportFile(NewAfterLeaseCheckReportMeta checkReportMeta, NewAfterLeaseCheckPlanClient checkPlanClient) {
        String filePath = "/tmp/租后检查报告_" + checkPlanClient.getId() + "_" + System.currentTimeMillis() + GlobalConstants.OFFICE_WORD_SUFFIX;
        try (OutputStream outputStream = FileUtil.getOutputStream(filePath)) {
            AfterLeaseCheckReportRenderFactory.getInstance(checkReportMeta.getReportType(), checkReportMeta.getReportTemplateVersion()).render(outputStream, checkPlanClient);
        } catch (Exception e) {
            log.error("生成租后检查报告（低风险）发生异常[checkPlanClientId: {}]", checkPlanClient.getId(), e);
            throw new MithrasException("生成租后检查报告发生异常");
        }
        return filePath;
    }
}
