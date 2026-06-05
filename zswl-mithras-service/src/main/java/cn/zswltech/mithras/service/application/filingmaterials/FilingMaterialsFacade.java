package cn.zswltech.mithras.service.application.filingmaterials;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.filingmaterials.application.FilingMaterialsApplicationService;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.filingmaterials.*;
import cn.zswltech.mithras.filingmaterials.domain.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.filingmaterials.domain.enums.*;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.FilingMaterialsMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.FilingMaterials;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.FileUriUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static java.util.stream.Collectors.toList;

/**
 * @author lllin
 * @date 2025-12-03
 */
@Service
@Slf4j
public class FilingMaterialsFacade implements FilingMaterialsApplicationService {
    @Resource
    private OssClient ossClient;
    @Resource
    private HttpServletResponse response;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ClientService clientService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private FilingMaterialsService filingMaterialsService;
    @Resource
    ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FlowTaskApiService taskApiService;


    @Override
    public void batchDownload(FilingMaterialsREQ req) throws IOException{
        String moduleCode = req.getModuleCode();
        String tabCode = req.getTabCode();
        boolean equals = Objects.equals(moduleCode, FilingMaterialsModuleCodeEnum.REFERENCE_MATERIALS.name());
        boolean basicEqual = Objects.equals(tabCode, FilingDirectoryEnum.BASIC_MATERIALS.getCode());
        List<String> businessTypeList = new ArrayList<>();
        if (equals) {
            if (basicEqual) {
                businessTypeList.add(BusinessModuleEnum.BUSINESS_REVIEW_CLIENT.name());
            } else {
                List<FilingDirectoryEnum> childrenByParentCodeList = FilingDirectoryEnum.getChildrenByParentCode(req.getTabCode(), req.getModuleCode());
                Assert.notEmpty(childrenByParentCodeList, () -> MithrasException.newException("批量下载：tab映射失败" + tabCode));
                businessTypeList = childrenByParentCodeList.stream().map(FilingDirectoryEnum::getBusinessType).distinct().collect(toList());
            }
        } else {
            FilingMaterialsBusinessTypeEnum businessTypeEnum = FilingMaterialsBusinessTypeEnum.of(tabCode);
            Assert.notNull(businessTypeEnum, () -> MithrasException.newException("批量下载：映射失败" + tabCode));
            businessTypeList.add(businessTypeEnum.getCode());
        }
        StopWatch st = new StopWatch("项目归档资料批量下载");
        st.start("数据查询");
        List<MaterialsList> materialsListList = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, req.getId())
                .eq(Objects.nonNull(req.getClientId()), MaterialsList::getSourceBusinessKey, req.getClientId())
                .in(MaterialsList::getBusinessType, businessTypeList)
                .in(CollUtil.isNotEmpty(req.getFileIds()), MaterialsList::getId, req.getFileIds()));
        Assert.notEmpty(materialsListList, () -> MithrasException.newException("没有找到任何文件记录"));
        FilingMaterials filingMaterials = filingMaterialsService.getById(req.getId());
        Assert.notNull(filingMaterials, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        st.stop();
        String rootPath = FilingDirectoryEnum.getTabName(tabCode) + "_" + FilingMaterialsModuleCodeEnum.getModuleName(moduleCode);
        String zipFileName = rootPath + ".zip";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.name()));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        st.start("文件压缩下载");
        try (OutputStream out = response.getOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            Set<String> pathSet = Collections.synchronizedSet(new LinkedHashSet<>());
            List<String> pathList = new CopyOnWriteArrayList<>();

            if (basicEqual) {
                Map<Long, List<MaterialsList>> clientMaterialMap;
                if (equals) {
                    /*归档资料重新查询项目评审下交易主体*/
                    Set<Long> clientIds = filingMaterialsService.getProjReviewClient(filingMaterials.getContractId(), new HashSet<>(), new HashMap<>(), materialsListList);
                    clientMaterialMap = materialsListList.stream().filter(e -> clientIds.contains(Long.parseLong(e.getSourceBusinessKey())))
                            .collect(Collectors.groupingBy(m -> Long.parseLong(m.getSourceBusinessKey())));
                } else {
                    clientMaterialMap = materialsListList.stream().collect(Collectors.groupingBy(m -> Long.parseLong(m.getSourceBusinessKey())));
                }
                // 批量查询客户信息，客户名称会作为下级目录
                List<Client> clientList = clientService.listByClientIds(clientMaterialMap.keySet());
                Map<Long, Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, v -> v));
                if (!equals) {
                    Long clientId = req.getClientId();
                    Assert.notNull(req.getClientId(), () -> MithrasException.newException("基础资料-归档批量下载客户id为空"));
                    Client client = clientMap.get(clientId);
                    Assert.notNull(client, () -> MithrasException.newException("通过客户资料中的客户id没有找到客户信息clientId:" + clientId));
                    rootPath = "(" + client.getClientName() + ")" + rootPath;
                    zipFileName = rootPath + ".zip";
                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.name()));
                }
                String clientPath = rootPath;
                for (Map.Entry<Long, List<MaterialsList>> entry : clientMaterialMap.entrySet()) {
                    Long clientId = entry.getKey();
                    List<MaterialsList> clientMaterials = entry.getValue();
                    Client client = clientMap.get(clientId);
                    if (Objects.isNull(client)) {
                        log.warn("通过客户资料中的客户id没有找到客户信息[clientId: {}]", clientId);
                        continue;
                    }
                    if (Objects.equals(moduleCode, FilingMaterialsModuleCodeEnum.REFERENCE_MATERIALS.name())) {
                        clientPath = rootPath + File.separator + client.getClientName();
                    }
                    download(zipOut, clientMaterials, clientPath, pathList, pathSet);
                }
            } else {
                download(zipOut, materialsListList, rootPath, pathList, pathSet);
            }
            zipOut.finish();
            zipOut.flush();
            out.flush();
            response.flushBuffer();
        } catch (Exception e) {
            log.error("批量下载失败", e);
            throw MithrasException.newException("文件下载失败：" + e.getMessage());
        } finally {
            st.stop();
            log.info("MaterialsListController batchDownload {}", st.prettyPrint(TimeUnit.MILLISECONDS));
        }
    }

    private void download(ZipOutputStream zipOut, List<MaterialsList> clientMaterials, String clientPath, List<String> pathList, Set<String> pathSet) {
        filingMaterialsService.repeatFileNameReplace(clientMaterials);
        for (MaterialsList material : clientMaterials) {
            // 拼接文件路径
            String filePath = clientPath + File.separator + material.getFilename();
            // 获取文件流
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ossClient.downLoad(byteArrayOutputStream, join("/", material.getOssFilename()));
                ZipEntry zEntry = new ZipEntry(filePath);
                zipOut.putNextEntry(zEntry);
                byteArrayOutputStream.writeTo(zipOut);
                zipOut.closeEntry();
                log.info("文件写入zip成功:"+filePath);
            } catch (Exception e){
                log.error("文件写入zip失败:{},写入失败原因:{}"+filePath + e.getMessage());
            }
        }
    }


    @Override
    public R<List<FilingMaterialsTabRSP>> filingTab(@Valid FilingBaseREQ req) {
        FilingMaterials filingMaterials = filingMaterialsService.getById(req.getId());
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<FilingDirectoryEnum> allParent = FilingDirectoryEnum.getAllParent();
        if (allParent.contains(FilingDirectoryEnum.COLLATERALIZATION_MATERIALS)
                && !Objects.equals(YesOrNoNumberEnum.YES.getCode(), filingMaterials.getGuaranteeFlag())) {
            allParent.remove(FilingDirectoryEnum.COLLATERALIZATION_MATERIALS);
        }
        List<FilingMaterialsTabRSP> returnList = new ArrayList<>();
        allParent.stream().forEach(item -> {
            FilingMaterialsTabRSP filingMaterialsTabRSP = new FilingMaterialsTabRSP();
            filingMaterialsTabRSP.setTabCode(item.getCode());
            filingMaterialsTabRSP.setTabName(item.getName());
            returnList.add(filingMaterialsTabRSP);
        });
        return R.ok(returnList);
    }

    /**
     * 根据前端参数，查询对应模块的数据
     *
     * @param req
     * @return
     */
    @Override
    public R<FilingProjMaterialsListListRSP> getCustomerReferenceMaterials(@Valid FilingMaterialsQueryREQ req) {
        return R.ok(filingMaterialsService.getCustomerReferenceMaterials(req));
    }

    /**
     * 资料同步
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> materialsSynchronization(@Valid FilingMaterialsSynchronizationREQ req) {
        List<FilingMaterials> firstFilingMaterialsList = checkApprovePass(req.getId());
        Assert.notEmpty(firstFilingMaterialsList, () -> MithrasException.newException("该项目无已审批通过的项目归档流程"));
        FilingMaterials firstFilingMaterials = firstFilingMaterialsList.get(0);
        LambdaQueryWrapper<MaterialsList> query = Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, firstFilingMaterials.getId())
                .eq(MaterialsList::getBusinessType, req.getBusinessType());
        if (Objects.nonNull(req.getClientId())) {
            query.eq(MaterialsList::getSourceBusinessKey, req.getClientId());
        }
        List<MaterialsList> materialsLists = SpringUtil.getBean(MaterialsListMapper.class).selectList(query);
        Assert.notEmpty(materialsLists, () -> MithrasException.newException("没有找到任何文件记录"));
        /*排除归档资料-基础资料清单*/
        materialsLists.removeIf(e -> Objects.equals(FilingMaterialsConstants.BASIC_INFORMATION,e.getMaterialsType())
                && FilingMaterialsBusinessTypeEnum.getBusinessTypeAll().contains(e.getBusinessType()));
        if (Objects.nonNull(req.getClientId())) {
            materialsLists.forEach(item -> item.setSourceBusinessKey(String.valueOf(req.getClientId())));
        }
        /*资料拷贝到本次审批流程*/
        filingMaterialsService.copyProjectFile(materialsLists, req.getId(), req.getBusinessType(), null);
        return R.ok();
    }

    /**
     * 查询该笔合同关联项目是否存在已审批通过的项目资料归档流程
     *
     * @param id
     * @return
     */
    private List<FilingMaterials> checkApprovePass(Long id) {
        FilingMaterials filingMaterials = filingMaterialsService.getById(id);
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<FilingMaterials> firstFilingMaterialsList = SpringUtil.getBean(FilingMaterialsMapper.class).selectList(Wrappers.<FilingMaterials>lambdaQuery()
                .eq(FilingMaterials::getProjCode, filingMaterials.getProjCode())
                .eq(FilingMaterials::getApproveStatus, ProcessStatus.APPROVAL_PASS.name())
                .ne(FilingMaterials::getId, filingMaterials.getId())
                .orderByAsc(FilingMaterials::getApproveDate).last(StringUtil.mysqlLimitOne()));
        if (CollUtil.isEmpty(firstFilingMaterialsList)) {
            return Collections.emptyList();
        }
        return firstFilingMaterialsList;
    }

    /**
     * 资料引入
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> materialsImport(@Valid FilingMaterialsImportREQ req) {
        List<Long> materialIds = req.getMaterialIds();
        Assert.notEmpty(materialIds, () -> MithrasException.newException("请勾选需要引入的文件"));
        List<MaterialsList> materialsLists = materialsListMapper.selectBatchIds(materialIds);
        Assert.notEmpty(materialsLists, () -> MithrasException.newException("项目资料归档-资料引入未找到文件"));
        if (Objects.nonNull(req.getClientId())) {
            materialsLists.forEach(item -> item.setSourceBusinessKey(String.valueOf(req.getClientId())));
        }
        filingMaterialsService.copyProjectFile(materialsLists, req.getId(), req.getBusinessType(), req.getMaterialsType());
        return R.ok();
    }


    @Override
    public R<String> effect(@Valid FilingBaseREQ req) {
        return R.ok(filingMaterialsService.startProcess(req));
    }

    /**
     * 资料同步按钮是否展示
     *
     * @param req
     * @return
     */
    @Override
    public R<Boolean> synchronizationButtonFlag(@Valid FilingMaterialsSynchronizationREQ req) {
        List<FilingMaterials> filingMaterials = checkApprovePass(req.getId());
        return R.ok(!CollUtil.isEmpty(filingMaterials));
    }


    @Override
    public R<FilingMaterialsQueryRSP> getNonCustomerReferenceMaterials(FilingMaterialsQueryREQ req) {
        return R.ok(filingMaterialsService.getNonCustomerReferenceMaterials(req));
    }

    @Override
    public R<Map<String, List<SelectRSP>>> getOperationsDirDict(FilingBaseREQ filingBaseREQ) {
        Map<String, List<SelectRSP>> operationsDirDict = filingMaterialsService.getPageOperationsDirDict(FilingMaterialsFilingTypeEnum.BUSINESS_MATERIALS.name(), filingBaseREQ.getId());
        return R.ok(operationsDirDict);
    }

    @Override
    public R<FileDownLoadRSP> download(@Valid FilingFileDownloadREQ filingFileDownloadREQ) {
        MaterialsList materialsList = materialsListService.getById(filingFileDownloadREQ.getFileId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应的文件");
        }
        return R.ok(materialsListService.download(filingFileDownloadREQ.getFileId()));
    }

    @Override
    public void downloadTemplate(@Valid FilingTemplateDownloasREQ filingTemplateDownloasREQ) {
        FilingMaterials filingMaterials = filingMaterialsService.getById(filingTemplateDownloasREQ.getId());
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BusinessMaterialsDocNameEnum materialsDocNameEnum = BusinessMaterialsDocNameEnum.getBusinessMaterialsDocNameEnumByRelateCode(filingTemplateDownloasREQ.getBusinessType());
        assert materialsDocNameEnum != null;
        String display = materialsDocNameEnum.display;
        int lastDotIndex = display.lastIndexOf(".");
        String prefix = display.substring(0, lastDotIndex);
        String suffix = display.substring(lastDotIndex);
        String fileName = prefix + "模板" + suffix;
        if (Objects.nonNull(filingTemplateDownloasREQ.getClientId())) {
            Client client = SpringUtil.getBean(ClientService.class).getById(filingTemplateDownloasREQ.getClientId());
            Assert.notNull(client, () -> MithrasException.newException("模板下载,合同下不存在该交易主体[" + filingTemplateDownloasREQ.getClientId() + "]"));
            fileName = client.getClientName() + "-" + fileName;
        }
        try (OutputStream os = response.getOutputStream()) {
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            filingMaterialsService.generateBasicTemplate(os, filingTemplateDownloasREQ, materialsDocNameEnum);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载模板发生未知异常", e);
            throw new MithrasException("下载模板发生未知异常");
        }
    }


    @Override
    public R<Void> remove(FilingBasicRemoveREQ filingBasicRemoveREQ) {
        filingMaterialsService.fileRemove(filingBasicRemoveREQ);
        return R.ok();
    }

    @Override
    public R<String> getCurTaskDefKey(FilingProcessREQ filingProcessREQ) {
        ProcessResp processResp = taskApiService.queryProcessById(filingProcessREQ.getProcessInstanceId());
        if(Objects.isNull(processResp)){
            return R.ok(null);
        }
        return R.ok(processResp.getCurTaskActivityIds());
    }

}
