package cn.zswltech.mithras.service.service.materialsdger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsDownloadRecordsQueryRSP;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryRSP;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.service.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.DownloadStatusEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.service.enums.fund.DirectFinancingType;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.filingmaterials.ArchivedMaterialsDownloadRecordMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.ArchivedMaterialsDownloadRecord;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.FilingMaterials;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.FundDirectFinancingFilingMaterialsQuery;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.FundDirectFinancingFilingMaterialsResult;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.FundFinancingFilingMaterialsQuery;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.FundFinancingFilingMaterialsResult;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.service.service.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.FileUriUtil;
import cn.zswltech.mithras.common.util.StringUtils;
import cn.zswltech.mithras.common.util.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.HashBasedTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.google.common.collect.Table;

import static cn.hutool.core.text.CharSequenceUtil.join;

@Slf4j
@Service
public class FundSideMaterialsManagementLedgerService extends AbstractMaterialsManagementLedger<FundSideArchivedMaterialsQueryREQ, FundSideArchivedMaterialsQueryRSP> {

    @Resource
    private FilingMaterialsService filingMaterialsService;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private MaterialsListMapper materialsListMapper;

    @Resource
    private FundFilingMaterialsService fundFilingMaterialsService;

    @Resource
    private ArchivedMaterialsDownloadRecordMapper archivedMaterialsDownloadRecordMapper;

    @Resource
    private OssClient ossClient;

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private FundOrganizationService organizationService;

    @Override
    public PageR<FundSideArchivedMaterialsQueryRSP> queryMaterialsLedger(FundSideArchivedMaterialsQueryREQ req) {
        List<FundSideArchivedMaterialsQueryRSP> resList = new ArrayList<>();
        if (StringUtils.isEmpty(req.getProjClassify()) && StringUtils.isEmpty(req.getProductName())) {
            // 查询间融的归档资料数据
            FundFinancingFilingMaterialsQuery fundFinancingQuery = BeanCopyUtils.generatorObject(req, FundFinancingFilingMaterialsQuery.class);
            fundFinancingQuery.setArchiveDateFrom(DateUtil.startOfDay(req.getArchiveDateFrom()));
            fundFinancingQuery.setArchiveDateTo(DateUtil.endOfDay(req.getArchiveDateTo()));
            List<FundFinancingFilingMaterialsResult> fundFinancingResults =
                    filingMaterialsService.getBaseMapper().queryFundFinancingFilingMaterials(fundFinancingQuery);
            if (CollUtil.isNotEmpty(fundFinancingResults)) {
                List<FundSideArchivedMaterialsQueryRSP> fundSideArchivedMaterialsQueryRSPS = convertToFundingResponse(
                        fundFinancingResults, FilingMaterialsFilingTypeEnum.FUND_FINANCING.display);
                resList.addAll(fundSideArchivedMaterialsQueryRSPS);
            }
        }

        if (StringUtils.isEmpty(req.getBizType()) && Objects.isNull(req.getOrganizationId())) {
            // 查询直融的归档资料数据
            FundDirectFinancingFilingMaterialsQuery fundDirectQuery = BeanCopyUtils.generatorObject(req, FundDirectFinancingFilingMaterialsQuery.class);
            fundDirectQuery.setArchiveDateFrom(DateUtil.startOfDay(req.getArchiveDateFrom()));
            fundDirectQuery.setArchiveDateTo(DateUtil.endOfDay(req.getArchiveDateTo()));
            List<FundDirectFinancingFilingMaterialsResult> fundDirectResults =
                    filingMaterialsService.getBaseMapper().queryFundDirectFinancingFilingMaterials(fundDirectQuery);
            if (CollUtil.isNotEmpty(fundDirectResults)) {
                List<FundSideArchivedMaterialsQueryRSP> fundSideArchivedMaterialsQueryRSPS = convertToFundingResponse(
                        fundDirectResults, FilingMaterialsFilingTypeEnum.FUND_DIRECT_FINANCING.display);
                resList.addAll(fundSideArchivedMaterialsQueryRSPS);
            }
        }

        return PageR.of(new Page<>(req.getPage(), req.getPageSize(), resList.size()), resList);
    }

    /**
     * 归档资料批量下载
     */
    public void archivedMaterialsBatchDownload(FundSideArchivedMaterialsBatchDownloadREQ req) {
        if (CollUtil.isEmpty(req.getFilingMaterialsIds())) {
            // 文件归档 id为空时，前端未勾选具体资料，下载根据当前查询条件查询回的所有数据
            req.setFilingMaterialsIds(makeFilingMaterialsIds(req));
        }

        // 1. 验证参数并获取归档材料数据
        List<FilingMaterials> filingMaterialsList = validateAndFetchFilingMaterials(req.getFilingMaterialsIds());
        // 过滤获取 审批通过（已归档）的数据
        filingMaterialsList = filingMaterialsList
                .stream()
                .filter(fm-> StringUtils.equals(fm.getApproveStatus(), FilingMaterialsProcessStatusEnum.APPROVAL_PASS.name()))
                .collect(java.util.stream.Collectors.toList());

        if(CollUtil.isEmpty(filingMaterialsList)){
            log.info("无符合条件资料，不进行下载！");
            return;
        }
        // 获取过滤后的归档资料清单 id列表
        List<Long> filingMaterialsIds = filingMaterialsList.stream().map(FilingMaterials::getId).collect(Collectors.toList());
        // 2. 获取资料清单
        List<MaterialsList> materialsListList = fetchMaterialsList(filingMaterialsIds);

        // 3. 获取配置映射
        Set<String> filingTypeSet = filingMaterialsList.stream()
                .map(FilingMaterials::getFilingType)
                .collect(Collectors.toSet());
        Map<String, String> dirCode2dirNameMap = getDirectoryMapping(filingTypeSet);
        Map<Long, String> filingMaterialsId2OverrideFileNameMap = getFileOverrideMapping(filingMaterialsList);

        // 4. 设置根路径
        String rootPath = "归档资料-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 5. 归类资料数据，行<归档资料id>、列<资料类型>、值<资料清单>
        Table<Long, String, List<MaterialsList>> filingMaterialsIdAddMaterialsType2MaterialsListTable = HashBasedTable.create();
        materialsListList.forEach(materialsList -> {
            List<MaterialsList> materialsLists = filingMaterialsIdAddMaterialsType2MaterialsListTable.get(materialsList.getBelongId(), materialsList.getMaterialsType());
            if (CollUtil.isEmpty(materialsLists)){
                materialsLists = new ArrayList<>();
                filingMaterialsIdAddMaterialsType2MaterialsListTable.put(materialsList.getBelongId(), materialsList.getMaterialsType(), materialsLists);
            }
            materialsLists.add(materialsList);
        });

        // 6. 登记到归档资料下载记录
        ArchivedMaterialsDownloadRecord downloadRecord = new ArchivedMaterialsDownloadRecord();
        downloadRecord.setDownloadStatus(DownloadStatusEnum.IN_PROGRESS.name());
        downloadRecord.setFileName(rootPath + ".zip");
        archivedMaterialsDownloadRecordMapper.insert(downloadRecord);

        // 7、异步执行文件打包和上传
        ThreadPoolUtil.getCommonPool().execute(() -> processDownloadAsync(
                filingMaterialsIdAddMaterialsType2MaterialsListTable,
                rootPath,
                dirCode2dirNameMap,
                filingMaterialsId2OverrideFileNameMap,
                downloadRecord.getId()
        ));
    }

    /**
     * 获取文件归档 id列表
     */
    private List<Long> makeFilingMaterialsIds(FundSideArchivedMaterialsBatchDownloadREQ req){
        FundSideArchivedMaterialsQueryREQ fundSideArchivedMaterialsQueryREQ = BeanCopyUtils.generatorObject(req, FundSideArchivedMaterialsQueryREQ.class);
        fundSideArchivedMaterialsQueryREQ.setArchiveFlag(true);
        PageR<FundSideArchivedMaterialsQueryRSP> pageResult = queryMaterialsLedger(fundSideArchivedMaterialsQueryREQ);
        List<FundSideArchivedMaterialsQueryRSP> records = pageResult.getList();

        // 收集符合条件的归档资料 ID
        return records.stream()
                .map(FundSideArchivedMaterialsQueryRSP::getFilingMaterialsId)
                .collect(Collectors.toList());
    }

    /**
     * 查询归档资料下载记录
     */
    public PageR<FundSideArchivedMaterialsDownloadRecordsQueryRSP> recordsQuery(PageReq req) {
        Page<ArchivedMaterialsDownloadRecord> records = archivedMaterialsDownloadRecordMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<ArchivedMaterialsDownloadRecord>lambdaQuery().orderByDesc(ArchivedMaterialsDownloadRecord::getCreateTime));
        if (CollUtil.isEmpty(records.getRecords())) {
            return PageR.empty(0, 0);
        }

        List<FundSideArchivedMaterialsDownloadRecordsQueryRSP> supplementInfo = records.getRecords().stream().map(r -> {
            FundSideArchivedMaterialsDownloadRecordsQueryRSP rsp = BeanCopyUtils.generatorObject(r, FundSideArchivedMaterialsDownloadRecordsQueryRSP.class);
            rsp.setDownloadStatus(DownloadStatusEnum.getDisplayByCode(r.getDownloadStatus()));
            rsp.setDownloadTime(r.getCreateTime());
            // 设置url 有效期为一天
            String previewUrl = materialsListService.getPreviewUrl(r.getFilePath(), 60 * 60 * 24);
            rsp.setFilePath(previewUrl);
            return rsp;
        }).collect(Collectors.toList());

        return PageR.of(records, supplementInfo);
    }

    /**
     * 上传字节内容到 OSS
     *
     * @param content  文件内容
     * @param fileName 文件名
     * @return 完整的OSS 路径
     */
    private String uploadContentToOss(byte[] content, String fileName) {
        String ossFilePath = SpringContextHolder.getBean(MaterialsListService.class).buildFilePath(fileName, ".zip", 0L, FilingMaterialsConstants.ARCHIVED_DOCUMENT_PACKAGE, null, null);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            ossClient.upLoad(inputStream, ossFilePath);
            return ossFilePath;
        } catch (IOException e) {
            log.error("上传文件到OSS失败，路径: {}", ossFilePath, e);
            throw new RuntimeException("上传文件到OSS失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建重命名后的文件名
     *
     * @param materialsList                         资料清单
     * @param filingMaterialsId2OverrideFileNameMap 归档材料ID 到重写文件名的映射
     * @param materialsTypeName                     材料类型名称
     * @return 重命名后的文件名
     */
    private String buildRenamedFileName(MaterialsList materialsList,
                                        Map<Long, String> filingMaterialsId2OverrideFileNameMap,
                                        String materialsTypeName) {
        String baseFileName = Optional.ofNullable(filingMaterialsId2OverrideFileNameMap.get(materialsList.getBelongId()))
                .orElse("未知文件");
        return baseFileName + "-" + materialsTypeName + "-" + materialsList.getFilename();
    }

    /**
     * 创建ZIP 内容
     *
     * @param filingMaterialsIdAddMaterialsType2MaterialsListTable 资料数据归类，行<归档资料id>、列<资料类型>、值<资料清单>
     * @param rootPath                                             根路径
     * @param dirCode2dirNameMap                                   目录编码到名称的映射
     * @param filingMaterialsId2OverrideFileNameMap                归档材料ID 到重写文件名的映射
     * @return ZIP 内容的字节数组
     */
    private byte[] createZipContent(Table<Long, String, List<MaterialsList>> filingMaterialsIdAddMaterialsType2MaterialsListTable,
                                    String rootPath,
                                    Map<String, String> dirCode2dirNameMap,
                                    Map<Long, String> filingMaterialsId2OverrideFileNameMap) throws IOException {
        try (ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(zipStream, StandardCharsets.UTF_8)) {
            for (Table.Cell<Long, String, List<MaterialsList>> cell : filingMaterialsIdAddMaterialsType2MaterialsListTable.cellSet()) {
                String materialsTypeCode = cell.getColumnKey();
                String materialsTypeName = Optional.ofNullable(dirCode2dirNameMap.get(materialsTypeCode))
                        .orElse(materialsTypeCode);
                /*重复文件名替换*/
                fundFilingMaterialsService.repeatFileNameReplace(Objects.requireNonNull(cell.getValue()));
                for (MaterialsList materialsList : cell.getValue()) {
                    // 构建重命名后的文件名
                    String fileName = buildRenamedFileName(materialsList, filingMaterialsId2OverrideFileNameMap, materialsTypeName);
                    // 构建文件路径 一级文件夹为“【融资编号】-【产品名称/融资机构】”、二级文件夹为其对应一级标题
                    String filePath = rootPath + File.separator + filingMaterialsId2OverrideFileNameMap.get(materialsList.getBelongId()) + File.separator + materialsTypeName + File.separator + fileName;
                    // 从OSS下载文件内容
                    try (ByteArrayOutputStream ossOutStream = new ByteArrayOutputStream()) {
                        ossClient.downLoad(ossOutStream, join("/", materialsList.getOssFilename()));
                        // 添加文件到ZIP
                        ZipEntry zEntry = new ZipEntry(filePath);
                        zipOut.putNextEntry(zEntry);
                        ossOutStream.writeTo(zipOut);
                        zipOut.closeEntry();
                    } catch (Exception e) {
                        log.error("文件写入zip失败:{},写入失败原因:{}", filePath, e.getMessage());
                    }
                }
            }
            zipOut.finish();
            zipOut.flush();
            return zipStream.toByteArray();
        }
    }

    /**
     * 异步处理下载任务
     */
    private void processDownloadAsync(Table<Long, String, List<MaterialsList>> filingMaterialsIdAddMaterialsType2MaterialsListTable,
                                      String rootPath,
                                      Map<String, String> dirCode2dirNameMap,
                                      Map<Long, String> filingMaterialsId2OverrideFileNameMap,
                                      Long recordId) {
        ArchivedMaterialsDownloadRecord downloadRecord = new ArchivedMaterialsDownloadRecord();
        downloadRecord.setId(recordId);
        try {
            // 创建ZIP 并打包文件
            byte[] zipContent = createZipContent(filingMaterialsIdAddMaterialsType2MaterialsListTable, rootPath, dirCode2dirNameMap,
                    filingMaterialsId2OverrideFileNameMap);

            // 上传到 OSS
            String ossPath = uploadContentToOss(zipContent, rootPath);

            log.info("批量下载归档文档成功，OSS路径: {}，文件大小: {} 字节", ossPath, zipContent.length);

            // 更新归档资料下载记录 - 成功
            downloadRecord.setDownloadStatus(DownloadStatusEnum.SUCCESS.name());
            downloadRecord.setFilePath(ossPath);
        } catch (Exception e) {
            log.error("批量下载归档文档失败", e);
            // 更新归档资料下载记录 - 失败
            downloadRecord.setDownloadStatus(DownloadStatusEnum.FAILED.name());
        } finally {
            try {
                archivedMaterialsDownloadRecordMapper.updateById(downloadRecord);
            } catch (Exception e) {
                log.error("更新下载记录失败", e);
            }
        }
    }

    /**
     * 获取归档材料ID 到重写文件名的映射
     *
     * @param filingMaterialsList 归档材料列表
     * @return ID到重写文件名的映射
     */
    private Map<Long, String> getFileOverrideMapping(List<FilingMaterials> filingMaterialsList) {
        return fundFilingMaterialsService.getOverrideFileName(filingMaterialsList);
    }

    /**
     * 获取目录映射关系
     *
     * @param filingTypeSet 归档类型集合
     * @return 目录编码到目录名称的映射
     */
    private Map<String, String> getDirectoryMapping(Set<String> filingTypeSet) {
        return fundFilingMaterialsService.getDirMap(filingTypeSet);
    }


    /**
     * 验证请求参数并获取归档材料列表
     *
     * @param filingMaterialsIds 归档材料ID列表
     * @return 归档材料列表
     */
    private List<FilingMaterials> validateAndFetchFilingMaterials(List<Long> filingMaterialsIds) {
        List<FilingMaterials> filingMaterialsList = filingMaterialsService.listByIds(filingMaterialsIds);
        Assert.notEmpty(filingMaterialsList, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        return filingMaterialsList;
    }

    /**
     * 获取资料清单列表
     *
     * @param filingMaterialsIds 归档材料ID列表
     * @return 资料清单列表
     */
    private List<MaterialsList> fetchMaterialsList(List<Long> filingMaterialsIds) {
        return materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .in(CollUtil.isNotEmpty(filingMaterialsIds), MaterialsList::getBelongId, filingMaterialsIds)
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.FUND_FILING.name()));
    }

    /**
     * 将查询结果转换为响应对象
     */
    private <T> List<FundSideArchivedMaterialsQueryRSP> convertToFundingResponse(List<T> results, String filingTypeName) {
        return results.stream().map(result -> {
            FundSideArchivedMaterialsQueryRSP res = new FundSideArchivedMaterialsQueryRSP();
            if (result instanceof FundFinancingFilingMaterialsResult) {
                FundFinancingFilingMaterialsResult financingResult = (FundFinancingFilingMaterialsResult) result;
                res = BeanCopyUtils.generatorObject(financingResult, FundSideArchivedMaterialsQueryRSP.class);
                res.setProductNameOrOrganizationName(id2Name(orgId2NameMap, ((FundFinancingFilingMaterialsResult) result).getOrganizationId(), this::orgId2Name));
                res.setProjClassifyOrBizType(FundFinancingBizTypeEnum.name2Display(((FundFinancingFilingMaterialsResult) result).getBizType()));
            }
            if (result instanceof FundDirectFinancingFilingMaterialsResult) {
                FundDirectFinancingFilingMaterialsResult directResult = (FundDirectFinancingFilingMaterialsResult) result;
                res = BeanCopyUtils.generatorObject(directResult, FundSideArchivedMaterialsQueryRSP.class);
                res.setProductNameOrOrganizationName(((FundDirectFinancingFilingMaterialsResult) result).getProductName());
                res.setProjClassifyOrBizType(DirectFinancingType.name2Display(((FundDirectFinancingFilingMaterialsResult) result).getProjClassify()));
            }
            res.setFundManagerName(id2Name(userId2NameMap, getFundManagerId(result), id2NameService::sysUserId2NameSingle));
            res.setArchiveFlag(FilingMaterialsProcessStatusEnum.APPROVAL_PASS.name().equals(getApproveStatus(result)));
            res.setFilingType(filingTypeName);
            return res;
        }).collect(Collectors.toList());
    }

    /**
     * 获取资金经理 ID
     */
    @SuppressWarnings("unchecked")
    private <T> Long getFundManagerId(T result) {
        if (result instanceof FundFinancingFilingMaterialsResult) {
            return ((FundFinancingFilingMaterialsResult) result).getFundManagerId();
        } else if (result instanceof FundDirectFinancingFilingMaterialsResult) {
            return ((FundDirectFinancingFilingMaterialsResult) result).getFundManagerId();
        }
        return null;
    }

    /**
     * 获取审批状态
     */
    @SuppressWarnings("unchecked")
    private <T> String getApproveStatus(T result) {
        if (result instanceof FundFinancingFilingMaterialsResult) {
            return ((FundFinancingFilingMaterialsResult) result).getApproveStatus();
        } else if (result instanceof FundDirectFinancingFilingMaterialsResult) {
            return ((FundDirectFinancingFilingMaterialsResult) result).getApproveStatus();
        }
        return null;
    }

    /**
     * id 转机构名称
     */
    private String orgId2Name(Long orgId){
        if (Objects.isNull(orgId)){
            return Strings.EMPTY;
        }
        FundOrganization fundOrganization = organizationService.getById(orgId);
        if(Objects.isNull(fundOrganization)){
            return Strings.EMPTY;
        }
        return fundOrganization.getOrganizationName();
    }

    @Override
    protected void init() {
        List<FundFinancingFilingMaterialsResult> fundFinancingResults = filingMaterialsService.getBaseMapper().queryFundFinancingFilingMaterials(new FundFinancingFilingMaterialsQuery());
        List<Long> orgIds = fundFinancingResults.stream().map(FundFinancingFilingMaterialsResult::getOrganizationId).collect(Collectors.toList());
        orgId2NameMap.putAll(organizationService.getNamesByIds(orgIds));
    }
}
