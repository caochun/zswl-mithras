package cn.zswltech.mithras.application.orchestration.document.materialsfile;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswl.oss.model.OssInfo;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.file.FileRenameREQ;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListListRSP;
import cn.zswltech.mithras.dto.materialsfile.ProjMaterialsListListRSP;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.application.orchestration.metadata.enumscan.MaterialsTypeFactory;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.document.convert.FileConvert;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.customer.enums.client.ClientMaterialsDisplayEnum;
import cn.zswltech.mithras.document.enums.FileDownloadZipPathEnum;
import cn.zswltech.mithras.document.enums.MaterialsType;
import cn.zswltech.mithras.document.enums.NormalMaterialsType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.enums.client.CorporationClientMaterialTypeEnum;
import cn.zswltech.mithras.customer.enums.client.NormalClientMaterialTypeEnum;
import cn.zswltech.mithras.credit.groupcredit.establish.enums.GroupCreditEstablishMaterialsEnum;
import cn.zswltech.mithras.document.versioning.MaterialsListLibService;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishMaterialsApproveEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.document.mapper.MaterialsListLibMapper;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.materialsfile.dto.NewestMaterialsDto;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientAuthority;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.infrastructure.storage.MithrasMinioClient;
import cn.zswltech.mithras.contract.overdue.application.collection.BusinessMaterialResolver;
import cn.zswltech.mithras.application.orchestration.client.ClientAuthorityService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewService;
import cn.zswltech.mithras.document.util.FileUriUtil;
import cn.zswltech.mithras.foundation.util.StreamUtil;
import cn.zswltech.mithras.third.util.WatermarkUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.core.text.CharSequenceUtil.isNotEmpty;
import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.zswltech.mithras.dto.MaterialsListIdType.VERSIONED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.EXPIRE;
import static java.util.stream.Collectors.toList;


@Slf4j
@Service
public class MaterialsListService extends ServiceImpl<MaterialsListMapper, MaterialsList> implements BusinessMaterialResolver {
    @Resource
    private MithrasMinioClient mithrasMinioClient;
    @Resource
    private OssClient ossClient;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private MaterialsListLibMapper materialsListLibMapper;
    @Resource
    private FileConvert fileConvert;

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private ProjPricingService projPricingService;
    @Value("${oss.minio.endpoint}")
    private String path;
    @Value("${oss.minio.bucket-name}")
    private String bucketName;
    @Value("${oss.minio.down.expiry:86400}")
    private Integer expiry;

    public List<MaterialsList> listBySourceBusinessKey(BusinessModuleEnum businessModuleEnum, String sourceBusinessKey) {
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, businessModuleEnum.name());
        query.eq(MaterialsList::getSourceBusinessKey, sourceBusinessKey);
        return this.list(query);
    }

    public MaterialsList getById(Long id) {
        return materialsListMapper.selectById(id);
    }

    public List<MaterialsList> listBy(String businessType, Long belongId) {
        LambdaQueryWrapper<MaterialsList> query = new LambdaQueryWrapper<>();
        query.eq(MaterialsList::getBusinessType, businessType);
        query.eq(MaterialsList::getBelongId, belongId);
        return this.list(query);
    }

    @Override
    public boolean hasMaterials(String businessType, Long belongId) {
        return CollectionUtil.isNotEmpty(listBy(businessType, belongId));
    }

    @SneakyThrows
    public String getPreviewUrl(String ossFileName, int expiry) {
//        return ossClient.getPreviewUrl(ossFileName, expiry);
        // 设置返回头
        Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("response-content-type", "application/octet-stream");
//        return ossClient.getPreviewUrl(ossFileName, expiry, responseHeader);
        return mithrasMinioClient.getPreviewUrl(ossFileName, expiry, responseHeader);
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public Long add(MultipartFile file, Long belongId, String materialsType, String businessType) {
        return add(file.getInputStream(), file.getOriginalFilename(), belongId, materialsType, businessType);
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public void addIfNotExist(MultipartFile file, Long belongId, String materialsType, String businessType) {
        List<MaterialsList> list = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, belongId)
                .eq(MaterialsList::getMaterialsType, materialsType)
                .eq(MaterialsList::getFilename, file.getOriginalFilename())
                .eq(MaterialsList::getBusinessType, businessType));
        if (CollUtil.isNotEmpty(list)) {
            return;
        }
        add(file.getInputStream(), file.getOriginalFilename(), belongId, materialsType, businessType);
    }


    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public Long add(InputStream in, String fileName, Long belongId, String materialsType, String materialsSubType, String businessType) {
        return this.add(in, fileName, belongId, materialsType, materialsSubType, businessType, YesOrNoNumberEnum.NO);
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public Long add(InputStream in, String fileName, Long belongId, String materialsType, String materialsSubType, String businessType, YesOrNoNumberEnum systemGenerate) {
        return this.add(in, fileName, belongId, materialsType, materialsSubType, businessType, systemGenerate, null, null, null);
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public Long add(InputStream in, String fileName, Long belongId, String materialsType, String materialsSubType, String businessType, YesOrNoNumberEnum systemGenerate, String businessSourceKey, String location, Long createdBy) {
        checkPermission(Collections.singletonList(belongId), businessType);
        String suffix = FileNameUtil.getSuffix(fileName);
        String name = FileNameUtil.getPrefix(fileName);
        if (StrUtil.isEmpty(suffix)) {
            suffix = FileTypeUtil.getType(in);
        } else {
            suffix = suffix.toLowerCase();
        }
        String ossFileName = buildFilePath(name, suffix, belongId, materialsType, materialsSubType, businessType);
        OssInfo ossInfo = ossClient.upLoad(in, join("/", ossFileName), false);
        MaterialsList o = new MaterialsList();
        o.setFilename(fileName);
        o.setBelongId(belongId);
        o.setSuffix(suffix);
        o.setOssFilename(ossFileName);
        o.setFilePath(ossInfo.getPath());
        o.setMaterialsType(materialsType);
        o.setLocation(location);
        if (!StringUtils.isEmpty(materialsSubType)) {
            o.setMaterialSubType(materialsSubType);
        }
        if (StrUtil.isNotBlank(businessSourceKey)) {
            o.setSourceBusinessKey(businessSourceKey);
        }
        o.setBusinessType(businessType);
        o.setSystemGenerate(systemGenerate.getCode());
        if (createdBy != null) {
            o.setCreateBy(createdBy);
            o.setUpdateBy(createdBy);
        }
        materialsListMapper.insert(o);
        return o.getId();
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public Long newAdd(InputStream in, String fileName, Long belongId, String materialsType, String materialsSubType, String businessType, YesOrNoNumberEnum systemGenerate) {
        checkPermission(Collections.singletonList(belongId), businessType);
        String suffix = FileNameUtil.getSuffix(fileName);
        String name = FileNameUtil.getPrefix(fileName);
        if (StrUtil.isEmpty(suffix)) {
            suffix = FileTypeUtil.getType(in);
        } else {
            suffix = suffix.toLowerCase();
        }
        String ossFileName = newBuildFilePath(name, suffix, belongId, materialsType, materialsSubType, businessType);
        OssInfo ossInfo = ossClient.upLoad(in, join("/", ossFileName), false);
        MaterialsList o = new MaterialsList();
        o.setFilename(fileName);
        o.setBelongId(belongId);
        o.setSuffix(suffix);
        o.setOssFilename(ossFileName);
        o.setFilePath(ossInfo.getPath());
        o.setMaterialsType(materialsType);
        if (!StringUtils.isEmpty(materialsSubType)) {
            o.setMaterialSubType(materialsSubType);
        }
        o.setBusinessType(businessType);
        o.setSystemGenerate(systemGenerate.getCode());
        materialsListMapper.insert(o);
        return o.getId();
    }

    public String buildFilePath(String fileName, String fileSuffix, Long belongId, String materialsType, String materialsSubType, String businessType) {
        if (ObjectUtil.isNotNull(fileSuffix)) {
            fileSuffix = fileSuffix.toLowerCase();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(businessType == null ? "default" : businessType).append("/");
        builder.append(belongId).append("/");
        if (isNotEmpty(materialsType)) {
            builder.append(materialsType).append("/");
        }
        if (isNotEmpty(materialsSubType)) {
            builder.append(materialsSubType).append("/");
        }
        /*SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        builder.append(format.format(new Date())).append("/");*/
        //同名文件都需要保留，此处使用时间戳做文件夹
        String contentFileName = StrUtil.isEmpty(fileSuffix) ? builder.toString() + fileName : builder.toString() + fileName + "." + fileSuffix;
        if (ossClient.doesObjectExist(bucketName, ossClient.getBasePath() + "/" + contentFileName)) {
            builder.append(System.currentTimeMillis()).append("/");
        }
        builder.append(fileName);
        return StrUtil.isEmpty(fileSuffix) ? builder.toString() : builder.toString() + "." + fileSuffix;
    }

    public String newBuildFilePath(String fileName, String fileSuffix, Long belongId, String materialsType, String materialsSubType, String businessType) {
        if (ObjectUtil.isNotNull(fileSuffix)) {
            fileSuffix = fileSuffix.toLowerCase();
        }
        StringBuilder builder = new StringBuilder();
        if (StrUtil.isBlank(businessType)) {
            businessType = "UNKNOWN";
        }
        builder.append(businessType).append("/");
        if (isNotEmpty(materialsType)) {
            builder.append(materialsType).append("/");
        }
        if (isNotEmpty(materialsSubType)) {
            builder.append(materialsSubType).append("/");
        }
        builder.append(belongId).append("/");
        /*SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        builder.append(format.format(new Date())).append("/");*/
        //同名文件都需要保留，此处使用时间戳做文件夹
        String contentFileName = StrUtil.isBlank(fileSuffix) ? builder + fileName : builder + fileName + "." + fileSuffix;
        if (ossClient.doesObjectExist(bucketName, ossClient.getBasePath() + "/" + contentFileName)) {
            builder.append(System.currentTimeMillis()).append("/");
        }
        builder.append(fileName);
        return StrUtil.isEmpty(fileSuffix) ? builder.toString() : builder + "." + fileSuffix;
    }

    public List<MaterialsList> getList(Long belongId, String businessType, String materialsType, String materialsSubType) {
        LambdaQueryWrapper<MaterialsList> query = new LambdaQueryWrapper<>();
        query.eq(MaterialsList::getBelongId, belongId);
        query.eq(MaterialsList::getBusinessType, businessType);
        query.eq(ObjectUtil.isNotNull(materialsType), MaterialsList::getMaterialsType, materialsType);
        query.eq(ObjectUtil.isNotNull(materialsSubType), MaterialsList::getMaterialSubType, materialsSubType);
        return this.list(query);
    }

    public List<MaterialsList> getListByFileName(Long belongId, String businessType, String materialsType, String materialsSubType, String fileName) {
        LambdaQueryWrapper<MaterialsList> query = new LambdaQueryWrapper<>();
        query.eq(MaterialsList::getBelongId, belongId);
        query.eq(MaterialsList::getBusinessType, businessType);
        query.eq(ObjectUtil.isNotNull(materialsType), MaterialsList::getMaterialsType, materialsType);
        query.eq(ObjectUtil.isNotNull(materialsSubType), MaterialsList::getMaterialSubType, materialsSubType);
        query.eq(ObjectUtil.isNotNull(fileName), MaterialsList::getFilename, fileName);
        return this.list(query);
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public Long add(InputStream in, String fileName, Long belongId, String materialsType, String businessType) {
        return this.add(in, fileName, belongId, materialsType, null, businessType);
    }

    public List<MaterialsList> list(String businessType, List<String> materialsType, List<Long> belongIds) {
        return materialsListMapper
                .selectList(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBusinessType, businessType)
                        .in(ObjectUtil.isNotEmpty(materialsType), MaterialsList::getMaterialsType, materialsType)
                        .in(ObjectUtil.isNotEmpty(belongIds), MaterialsList::getBelongId, belongIds)
                        .orderByDesc(MaterialsList::getUpdateTime));
    }

    @Resource
    private MaterialsListLibService materialsListLibService;

    public List<MaterialsListListRSP> newestMaterialsList(String businessType, List<Long> belongIds) {

        NewestMaterialsDto dto = new NewestMaterialsDto();
        dto.setBusinessType(businessType);
        dto.setBelongIds(belongIds);

        if (BusinessModuleEnum.CLIENT.name().equals(businessType)) {
            List<Client> clients = clientMapper.selectBatchIds(belongIds);
            Map<String, List<Client>> clientTypeMap = clients.stream().collect(Collectors.groupingBy(Client::getClientType));
            for (String clientType : clientTypeMap.keySet()) {
                List<Long> typeIds = clientTypeMap.get(clientType)
                        .stream().map(Client::getId).collect(toList());
                if (ClientType.NORMAL.name().equals(clientType)) {
                    dto.getMaterialsTypes().addAll(EnumUtil.getNames(NormalMaterialsType.class));
                } else {
                    dto.getMaterialsTypes().addAll(EnumUtil.getNames(MaterialsType.class));
                }
            }
        } else if (BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name().equals(businessType)) {
            dto.setMaterialsTypes(new ArrayList<>(GroupCreditEstablishMaterialsEnum.listAll()));
        } else {
            List<String> projMaterials = ProjEstablishMaterialsEnum.listAll();
            projMaterials.addAll(ProjEstablishMaterialsApproveEnum.listAll());
            dto.setMaterialsTypes(projMaterials);
        }
        if (dto.getMaterialsTypes().isEmpty()) {
            return new ArrayList<>();
        }
        List<MaterialsListLib> materialsLists = materialsListLibService.newestMaterials(dto);
        return buildRsp(materialsLists, businessType, belongIds);
    }

    public List<ProjMaterialsListListRSP> toProjMaterialsListListRSP(Set<Long> clientIds, List<MaterialsList> materials, Map<Long, String> typeNameMap, Long belongId, String businessType) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        Map<Long, String> userNameMap;
        if (CollectionUtil.isNotEmpty(materials)) {
            Set<Long> userIds = materials.stream().map(BaseModel::getCreateBy).collect(Collectors.toSet());
            userNameMap = SpringUtil.getBean(Id2NameService.class).sysUserId2Name(userIds);
        } else {
            userNameMap = Collections.emptyMap();
        }
        Map<Long, List<MaterialsList>> client2MaterialsMap = materials.stream().filter(item -> StrUtil.isNotEmpty(item.getSourceBusinessKey())).collect(Collectors.groupingBy(e -> Long.parseLong(e.getSourceBusinessKey())));
        List<ProjMaterialsListListRSP> result = new LinkedList<>();
        List<Client> clientList = clientMapper.selectBatchIds(clientIds);
        for (Client client : clientList) {
            ProjMaterialsListListRSP rsp = new ProjMaterialsListListRSP();
            rsp.setClientId(client.getId());
            rsp.setName(client.getClientName());
            rsp.setClientType(client.getClientType());
            rsp.setClientTypeName(typeNameMap.get(client.getId()));
            rsp.setId(belongId);
            rsp.setBusinessType(businessType);
            List<MaterialsList> mList = client2MaterialsMap.get(client.getId());
            if (CollectionUtil.isNotEmpty(mList)) {
                rsp.setBusinessMaterialList(new LinkedList<>());
                // 按照类型分组
                Map<String, List<MaterialsList>> typeGroupList = mList.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
                for (Map.Entry<String, List<MaterialsList>> entry : typeGroupList.entrySet()) {
                    MaterialsListListRSP.MaterialGroup materialGroup = new MaterialsListListRSP.MaterialGroup();
                    materialGroup.setMaterialName(entry.getKey());
                    materialGroup.setMaterialList(new LinkedList<>());
                    for (MaterialsList material : entry.getValue()) {
                        MaterialsListListRSP.MaterialGroup.UploadItem item = new MaterialsListListRSP.MaterialGroup.UploadItem();
                        item.setMaterialSubName(material.getMaterialSubType());
                        item.setFilename(material.getFilename());
                        item.setRecordId(material.getId());
                        item.setCreateTimestamp(LocalDateTimeUtil.toEpochMilli(material.getCreateTime()));
                        item.setSourceBusinessKey(material.getSourceBusinessKey());
                        item.setCreateBy(material.getCreateBy());
                        if (Objects.nonNull(material.getCreateBy())) {
                            item.setCreateByName(userNameMap.get(material.getCreateBy()));
                        }
                        item.setSystemGenerate(material.getSystemGenerate());
                        materialGroup.getMaterialList().add(item);
                    }
                    materialGroup.getMaterialList().sort(new CommonFileSortComparator());
                    rsp.getBusinessMaterialList().add(materialGroup);
                }
                // 排序
                rsp.getBusinessMaterialList().sort(Comparator.comparing(r -> MaterialsType.ofWithDefault(r.getMaterialName()).order));
            }
            result.add(rsp);
        }
        return result.stream()
                .sorted(Comparator.comparing(r -> ClientMaterialsDisplayEnum.ofWithDefault(r.getClientTypeName()).order))
                .collect(toList());
    }

    @Deprecated
    public List<MaterialsListListRSP> listClientMaterial(List<Long> clientIds, String sourceBusinessKey) {
        List<MaterialsList> result = new LinkedList<>();
        List<String> targetTypes = new LinkedList<>();
        targetTypes.addAll(EnumUtil.getNames(CorporationClientMaterialTypeEnum.class));
        targetTypes.addAll(EnumUtil.getNames(NormalClientMaterialTypeEnum.class));
        // 去重
        Set<Long> filterClientIds = new HashSet<>(clientIds);
        for (Long clientId : filterClientIds) {
            Client client = clientMapper.selectById(clientId);
            if (Objects.isNull(client)) {
                continue;
            }
            Long userId = null;
            if (Objects.equals(client.getClientType(), ClientType.CORPORATION.name())) {
                List<CorpCommerceInfo> corpCommerceInfoList = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(clientId);
                if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
                    CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.get(0);
                    if (!Objects.equals(corpCommerceInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name())) {
                        // 非公海取管护权人
                        ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(clientId);
                        if (Objects.nonNull(clientAuthority)) {
                            userId = clientAuthority.getUserId();
                        }
                    }
                }
            }
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBelongId, clientId);
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CLIENT.name());
            query.in(MaterialsList::getMaterialsType, targetTypes);
            if (Objects.nonNull(userId)) {
                query.eq(BaseModel::getCreateBy, userId);
            }
            if (StrUtil.isNotBlank(sourceBusinessKey)) {
                query.and(innerQuery -> innerQuery.eq(MaterialsList::getSourceBusinessKey, sourceBusinessKey).or().eq(MaterialsList::getSourceBusinessKey, ""));
            }
            List<MaterialsList> list = this.list(query);
            if (CollectionUtil.isNotEmpty(list)) {
                result.addAll(list);
            }
        }
        if (CollectionUtil.isEmpty(result)) {
            // 没有资料但也需要返回客户信息，不然前端没法展示客户资料的区域
            return clientIds.stream().map(e -> {
                MaterialsListListRSP rsp = new MaterialsListListRSP();
                rsp.setBusinessType(BusinessModuleEnum.CLIENT.name());
                rsp.setId(e);
                return rsp;
            }).collect(toList());
        }
        return buildRsp(result, BusinessModuleEnum.CLIENT.name(), clientIds);
    }

    public List<MaterialsListListRSP> buildRspList(String businessType, List<Long> belongIds) {
        List<MaterialsList> materialsLists = new ArrayList<>();
        if (BusinessModuleEnum.CLIENT.name().equals(businessType)) {
            List<Client> clients = clientMapper.selectBatchIds(belongIds);
            Map<String, List<Client>> clientTypeMap = clients.stream().collect(Collectors.groupingBy(Client::getClientType));
            for (String clientType : clientTypeMap.keySet()) {
                List<Long> typeIds = clientTypeMap.get(clientType).stream().map(Client::getId).collect(toList());
                if (ClientType.NORMAL.name().equals(clientType)) {
                    materialsLists.addAll(materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                            .eq(MaterialsList::getBusinessType, businessType)
                            .in(MaterialsList::getMaterialsType, EnumUtil.getNames(NormalMaterialsType.class))
                            .in(MaterialsList::getBelongId, typeIds)
                            .orderByDesc(MaterialsList::getUpdateTime)));
                } else {
                    materialsLists.addAll(materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                            .eq(MaterialsList::getBusinessType, businessType)
                            .in(MaterialsList::getMaterialsType, EnumUtil.getNames(MaterialsType.class))
                            .in(MaterialsList::getBelongId, typeIds)
                            .orderByDesc(MaterialsList::getUpdateTime)));
                }
            }
        } else if (BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name().equals(businessType)) {
            materialsLists.addAll(materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, businessType)
                    .in(MaterialsList::getMaterialsType, new ArrayList<>(GroupCreditEstablishMaterialsEnum.listAll()))
                    .in(MaterialsList::getBelongId, belongIds)
                    .orderByDesc(MaterialsList::getUpdateTime)));
        } else {
            materialsLists.addAll(materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, businessType)
                    .in(MaterialsList::getMaterialsType, new ArrayList<>(ProjEstablishMaterialsEnum.listAll()))
                    .in(MaterialsList::getBelongId, belongIds)
                    .orderByDesc(MaterialsList::getUpdateTime)));
        }
        return buildRsp(materialsLists, businessType, belongIds);
    }

    private <T extends MaterialsList> List<MaterialsListListRSP> buildRsp(List<T> materialsLists,
                                                                          String businessType,
                                                                          List<Long> belongIds) {
        List<MaterialsListListRSP> brsp = new ArrayList<>();
        Map<Long, String> userNameMap;
        if (CollectionUtil.isNotEmpty(materialsLists)) {
            Set<Long> userIds = materialsLists.stream().map(e -> e.getCreateBy()).collect(Collectors.toSet());
            userNameMap = SpringUtil.getBean(Id2NameService.class).sysUserId2Name(userIds);
        } else {
            userNameMap = Collections.emptyMap();
        }
        Map<Long, List<MaterialsList>> idmap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
        for (Long id : idmap.keySet()) {
            MaterialsListListRSP rsp = new MaterialsListListRSP();
            rsp.setBusinessType(businessType);
            rsp.setId(id);
            Map<String, List<MaterialsList>> map = idmap.get(id).stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
            List<MaterialsListListRSP.MaterialGroup> RSPList = new ArrayList<>();
            for (Map.Entry<String, List<MaterialsList>> entry : map.entrySet()) {
                MaterialsListListRSP.MaterialGroup RSP = new MaterialsListListRSP.MaterialGroup();
                RSP.setMaterialName(entry.getKey());
                List<MaterialsListListRSP.MaterialGroup.UploadItem> itemList = new ArrayList<>();
                List<MaterialsList> value = entry.getValue();
                for (MaterialsList materialsList : value) {
                    MaterialsListListRSP.MaterialGroup.UploadItem item = new MaterialsListListRSP.MaterialGroup.UploadItem();
                    item.setMaterialSubName(materialsList.getMaterialSubType());
                    item.setFilename(materialsList.getFilename());
                    item.setRecordId(materialsList.getId());
                    item.setCreateTimestamp(LocalDateTimeUtil.toEpochMilli(materialsList.getCreateTime()));
                    item.setSourceBusinessKey(materialsList.getSourceBusinessKey());
                    item.setCreateBy(materialsList.getCreateBy());
                    if (Objects.nonNull(materialsList.getCreateBy())) {
                        item.setCreateByName(userNameMap.get(materialsList.getCreateBy()));
                    }
                    //增加版本号
                    if (materialsList instanceof MaterialsListLib) {
                        /**
                         * 之前的预览是是编辑区id和版本表的version组合作为参数，进行预览的
                         * 现在有些情况是直接传版本表的id进来。所以区分下id类型
                         * idType:1:编辑；2：版本表
                         */
                        item.setIdType(VERSIONED);
                    }
                    itemList.add(item);
                }
                itemList.sort(new CommonFileSortComparator());
                RSP.setMaterialList(itemList);
                RSPList.add(RSP);
            }
            RSPList = RSPList.stream()
                    .sorted(Comparator.comparing(r -> MaterialsType.ofWithDefault(r.getMaterialName()).order))
                    .collect(toList());
            rsp.setBusinessMaterialList(RSPList);
            brsp.add(rsp);
        }
        List<Long> inIds = brsp.stream().map(MaterialsListListRSP::getId).collect(toList());
        List<Long> reduce = belongIds.stream().filter(item -> !inIds.contains(item)).collect(toList());
        for (Long id : reduce) {
            MaterialsListListRSP nullrsp = new MaterialsListListRSP();
            nullrsp.setId(id);
            nullrsp.setBusinessType(businessType);
            brsp.add(nullrsp);
        }
        return brsp;
    }

    //判断是否为主数据编号下的文件，不是则不删除
    public void remove(List<Long> ids, Long mainId) {
        List<MaterialsList> materialsLists = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, mainId));
        if (ObjectUtil.isEmpty(materialsLists)) {
            return;
        }
        Set<Long> idSet = materialsLists.stream().map(MaterialsList::getId).collect(Collectors.toSet());
        List<Long> hasIds = new ArrayList<>();
        for (Long id : ids) {
            if (idSet.contains(id)) {
                hasIds.add(id);
            }
        }
        if (ObjectUtil.isNotEmpty(hasIds)) {
            remove(hasIds);
        }
    }

    public void remove(List<Long> ids) {
        List<MaterialsList> materialsLists = materialsListMapper.selectBatchIds(ids);
        Map<String, List<MaterialsList>> listMap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getBusinessType));
        for (String type : listMap.keySet()) {
            List<Long> typeIds = listMap.get(type).stream().map(MaterialsList::getBelongId).collect(toList());
            checkPermission(typeIds, type);
        }
        materialsLists.forEach(m -> checkPermissionApproval(m.getBusinessType(), m.getMaterialsType(), m.getBelongId(), null));
        materialsListMapper.deleteBatchIds(ids);
    }

    public void remove(Long id) {
        materialsListMapper.deleteById(id);
    }

    //校验主id
    public void remove(Long id, Long mainId) {
        materialsListMapper.delete(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getId, id)
                .eq(MaterialsList::getBelongId, mainId));
    }

    public List<MaterialsList> getByIds(List<Long> recordIds) {
        return materialsListMapper.selectBatchIds(recordIds);
    }

    public void download(OutputStream output, List<Long> recordIds) {
        download(output, recordIds, null, false);
    }

    public void download(OutputStream output, List<Long> recordIds, String version, boolean colZipFlag) {
        List<MaterialsList> records = getMaterialsListByIdsAndVersion(recordIds, version);
        if (records.isEmpty()) {
            throw new MithrasException("可下载文件列表为空");
        }
        if (records.size() > 1) {
            downloadBatch(output, records, colZipFlag);
        } else {
            ossClient.downLoad(output, join("/", records.get(0).getOssFilename()));
        }
    }

    public List<MaterialsList> getMaterialsListByIdsAndVersion(List<Long> recordIds, String version) {
        List<MaterialsList> records = new ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isBlank(version)) {
            records.addAll(materialsListMapper.selectBatchIds(recordIds));
        } else {
            records.addAll(materialsListLibMapper.selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                            .eq(MaterialsListLib::getVersion, version)
                            .in(MaterialsListLib::getOriginId, recordIds)
                    ).stream().filter(StreamUtil.distinctByKey(MaterialsListLib::getOriginId)).map(fileConvert::actualLib2Entity).collect(toList())
            );
        }
        return records;
    }

    /**
     * 打包下载
     **/
    @SneakyThrows
    public void downloadBatch(OutputStream output, List<MaterialsList> records, boolean colZipFlag) {
        if (ObjectUtil.isEmpty(records)) {
            return;
        }
        ZipOutputStream zipOut = new ZipOutputStream(output);
        Set<String> pathSet = new LinkedHashSet<>();
        StopWatch st = new StopWatch("批量下载");
        for (int i = 0; i < records.size(); i++) {
            MaterialsList materials = records.get(i);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            st.start("下载" + materials.getFilename());
            ossClient.downLoad(byteArrayOutputStream, join("/", materials.getOssFilename()));
            st.stop();
            st.start("压缩" + materials.getFilename());
            byte[] buffer = byteArrayOutputStream.toByteArray();
            String filepath = colZipFlag ? materials.getFilename() : getMaterialsPath(materials);
            filepath = FileUriUtil.fileNameDeduplication(pathSet, filepath);
            ZipEntry zEntry = new ZipEntry(filepath);
            zipOut.putNextEntry(zEntry);
            zipOut.write(buffer);
            zipOut.closeEntry();
            zipOut.flush();
            st.stop();
        }
        log.info("MaterialsListService file batch down use time {}", st.prettyPrint(TimeUnit.MILLISECONDS));
        zipOut.close();
    }

    public FileDownLoadRSP download(Long fileId) {
        return download(fileId, null);
    }

    public FileDownLoadRSP downloadLib(Long id) {
        MaterialsListLib materialsListLib = materialsListLibService.getById(id);
        FileDownLoadRSP fileListRSP = BeanUtil.copyProperties(materialsListLib, FileDownLoadRSP.class);
        //设置地址
        fileListRSP.setFileUrl(this.getPreviewUrl(fileListRSP.getOssFilename(), expiry));
        return fileListRSP;
    }

    public FileDownLoadRSP download(Long fileId, String version) {
        MaterialsList materialsList = getEntityWithCheck(fileId, version);
        FileDownLoadRSP fileListRSP = BeanUtil.copyProperties(materialsList, FileDownLoadRSP.class);
        //设置地址
        fileListRSP.setFileUrl(this.getPreviewUrl(fileListRSP.getOssFilename(), expiry));
        return fileListRSP;
    }

    /**
     * 文件下载后不可编辑，并且加水印
     *
     * @param fileId
     * @param version
     * @param watermark
     * @return
     */
    public FileDownLoadRSP download(Long fileId, String version, String watermark) {
        MaterialsList materialsList = getEntityWithCheck(fileId, version);
        FileDownLoadRSP fileListRSP = BeanUtil.copyProperties(materialsList, FileDownLoadRSP.class);
        String filePath = fileListRSP.getFilePath();
        String s = filePath + "/immutable/" + watermark + "/" + fileListRSP.getFilename();
        boolean exist2 = ossClient.doesObjectExist(bucketName, s);
        if (!exist2) {
            InputStream inputStream = ossClient.downLoad(join("/", fileListRSP.getOssFilename()));
            try {
                ByteArrayOutputStream resultOs = null;
                switch (fileListRSP.getSuffix().toLowerCase(Locale.ROOT)) {
                    case "doc":
                        resultOs = WatermarkUtil.setWordWaterMark(inputStream, watermark, "doc");
                        break;
                    case "docx":
                        resultOs = WatermarkUtil.setWordWaterMark(inputStream, watermark, "docx");
                        break;
                    case "xls":
                        resultOs = WatermarkUtil.setExcelWaterMark(inputStream, watermark, "xls");
                        break;
                    case "xlsx":
                        resultOs = WatermarkUtil.setExcelWaterMark(inputStream, watermark, "xlsx");
                        break;
                    case "ppt":
                        resultOs = WatermarkUtil.setPPTWaterMark(inputStream, watermark, "ppt");
                        break;
                    case "pptx":
                        resultOs = WatermarkUtil.setPPTWaterMark(inputStream, watermark, "pptx");
                        break;
                    case "pdf":
                        resultOs = WatermarkUtil.setPDFWaterMark(inputStream, watermark);
                        break;

                    default:
                        break;
                }
                if (resultOs != null) {
                    ByteArrayInputStream in2 = new ByteArrayInputStream(resultOs.toByteArray());
                    ossClient.upLoad(in2, join("/", s.replaceFirst("/mithras/", "")), false);
                    fileListRSP.setOssFilename(s.replaceFirst("/mithras/", ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            fileListRSP.setOssFilename(s.replaceFirst("/mithras/", ""));
        }
        //设置地址
        fileListRSP.setFileUrl(this.getPreviewUrl(fileListRSP.getOssFilename(), expiry));
        return fileListRSP;
    }

    private String getMaterialsPath(MaterialsList materials) {
        String one = Optional.ofNullable(materials.getBusinessType()).map(FileDownloadZipPathEnum::of).map(c -> c.display).orElse("资料清单");
//        String two = Optional.ofNullable(materials.getMaterialsType()).map(MaterialsType::of).map(c -> c.display).orElse(materials.getMaterialsType());
        String two = this.getMaterialType(materials.getBusinessType(), materials.getMaterialsType());
        String three = Optional.ofNullable(materials.getMaterialSubType()).map(t -> this.getMaterialType(materials.getBusinessType(), t)).orElse(materials.getMaterialSubType());
        StringBuilder builder = new StringBuilder(one);
        if (isNotEmpty(two)) {
            builder.append("/").append(two);
        }
        if (isNotEmpty(three)) {
            builder.append("/").append(three);
        }
        builder.append("/").append(materials.getFilename());
        return builder.toString();
    }

    private String getMaterialType(String businessModule, String materialsType) {
        return MaterialsTypeFactory.convert(businessModule, materialsType);
//        // FIXME 在业务模块较少的时候可以逐个模块匹配，但是模块增多后建议重新设计该方法，需要找一个地方统一管理资料表的类型（目前是按照业务模块散落到各个枚举中）
//        MaterialsType materialsType = MaterialsType.of(material.getMaterialsType());
//        if (Objects.nonNull(materialsType)) {
//            return materialsType.display;
//        }
//        ProjEstablishMaterialsEnum projEstablishMaterials = ProjEstablishMaterialsEnum.getByName(material.getMaterialsType());
//        if (Objects.nonNull(projEstablishMaterials)) {
//            return projEstablishMaterials.display();
//        }
//        ProjReviewMaterialsEnum projReviewMaterials = ProjReviewMaterialsEnum.getByName(material.getMaterialsType());
//        if (Objects.nonNull(projReviewMaterials)) {
//            return projReviewMaterials.display();
//        }
//        ContractTypeEnum contractType = ContractTypeEnum.getByName(material.getMaterialsType());
//        if (Objects.nonNull(contractType)) {
//            return contractType.getDisplay();
//        }
//        if (Objects.equals(material.getMaterialsType(), ContractLibModelEnum.CHANGE.name())) {
//            return ContractLibModelEnum.CHANGE.display;
//        }
//        if (Objects.equals(material.getMaterialsType(), ContractExtraFileTypeEnum.CONTRACT_SETTLE.name())) {
//            return ContractExtraFileTypeEnum.CONTRACT_SETTLE.getDisplay();
//        }
//        if (Objects.equals(material.getMaterialsType(), ContractChangeMaterialEnum.EXCHANGE_MATERIAL.name())) {
//            return ContractChangeMaterialEnum.EXCHANGE_MATERIAL.getDisplay();
//        }
//        LendingMaterialType lendingMaterialType = LendingMaterialType.getByName(material.getMaterialsType());
//        if (Objects.nonNull(lendingMaterialType)) {
//            return lendingMaterialType.display();
//        }
//        return material.getMaterialsType();
    }

    private void checkPermission(List<Long> belongIds, String businessType) {
        if (BusinessModuleEnum.PROJ_ESTABLISH.name().equals(businessType)) {
            for (Long id : belongIds) {
                ProjEstablishBaseInfo info = projEstablishBaseInfoService.getById(id);
                if (ObjectUtil.equal(info.getProjEstablishStatus(), CLOSED.name()) || ObjectUtil.equals(info.getProjEstablishStatus(), EXPIRE.name())) {
                    throw new MithrasException(String.format("项目[%s]已关闭/失效", info.getProjName()));
                }
            }
        }
    }

    public void checkPermissionApproval(String businessType, String materialsType, Long belongId, String processInstanceId) {
        projReviewTag: {
        if (BusinessModuleEnum.PROJ_REVIEW.name().equals(businessType)) {
            ProjReviewMaterialsEnum projReviewMaterialsEnum = ProjReviewMaterialsEnum.getByName(materialsType);
            if (Objects.isNull(projReviewMaterialsEnum)) {
                // 不需要校验的类型
                break projReviewTag;
            }
            ProcessResp processResp;
            if (processInstanceId != null) {
                processResp = projReviewService.findProcessByProcessInstanceId(processInstanceId);
            } else {
                processResp = projReviewService.findRelatedProcess(belongId);
            }
            switch (projReviewMaterialsEnum) {
                // 这4个东西只能在审批中进行操作
                case RISK_REVIEW_REPORT:
                    if (!checkProcess(processResp) || !ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                        throw new MithrasException(ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.getDisplay() + "只能在项目评审—风控经理审批节点操作");
                    }
                    break;
                case LEGAL_COMPLIANCE_REPORT:
                    if (!checkProcess(processResp) || !ProjReviewMaterialsEnum.LEGAL_COMPLIANCE_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                        throw new MithrasException(ProjReviewMaterialsEnum.LEGAL_COMPLIANCE_REPORT.getDisplay() + "只能在项目评审—法务经理审批节点操作");
                    }
                    break;
                case YIELD_REVIEW_REPORT:
                    if (!checkProcess(processResp) || !ProjReviewMaterialsEnum.YIELD_REVIEW_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                        throw new MithrasException(ProjReviewMaterialsEnum.YIELD_REVIEW_REPORT.getDisplay() + "只能在项目定价审批—财务主管1审批节点操作");
                    }
                    break;
                case MEETING_REVIEW_REPORT:
                    if (!checkProcess(processResp) || !ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                        throw new MithrasException(ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.getDisplay() + "只能在项目评审—评审会汇票节点或项目评审-会议纪要审批汇票节点操作");
                    }
                    break;
                case MEETING_REVIEW_RECORD:
                    if (!checkProcess(processResp) || !ProjReviewMaterialsEnum.MEETING_REVIEW_RECORD.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                        throw new MithrasException(ProjReviewMaterialsEnum.MEETING_REVIEW_RECORD.getDisplay() + "只能在项目评审—评审会汇票节点或项目评审-会议纪要审批汇票节点操作");
                    }
                    break;
                default:
                    break;
            }
        }
        }
        if (BusinessModuleEnum.PROJ_PRICING.name().equals(businessType)) {
            ProjPricingMaterialsEnum projPricingMaterialsEnum = ProjPricingMaterialsEnum.getByName(materialsType);
            if (Objects.isNull(projPricingMaterialsEnum)) {
                // 不需要校验的类型
                return;
            }
            ProcessResp processResp;
            if (processInstanceId != null) {
                processResp = projPricingService.findProcessByProcessInstanceId(processInstanceId);
            } else {
                processResp = projPricingService.findRelatedProcess(belongId);
            }
            switch (projPricingMaterialsEnum) {
                case BUSINESS_PRICING_APPROVAL_MEETING_REPORT:
                    if (!checkProcess(processResp) || !ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_MEETING_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                        throw new MithrasException(ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_MEETING_REPORT.getDisplay() + "只能在项目定价审批-财务主管2审批节点操作");
                    }
                    break;
                case YIELD_REVIEW_REPORT:
                    if (!checkProcess(processResp) || !ProjPricingMaterialsEnum.YIELD_REVIEW_REPORT.getCanHandleActivityIdList().contains(processResp.getCurTaskActivityIds())) {
                        throw new MithrasException(ProjPricingMaterialsEnum.YIELD_REVIEW_REPORT.getDisplay() + "只能在项目定价审批—财务主管1审批节点操作");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 判断是否在流程中 且当前审批节点有当前登陆用户
     *
     * @param processResp
     * @return
     */
    private boolean checkProcess(ProcessResp processResp) {
        return Objects.nonNull(processResp)
                && Stream.of(processResp.getCurAssigneeIds().split(","))
                .map(Long::valueOf)
                .filter(AccountUtil.getLoginInfo().getId()::equals)
                .findFirst().orElse(null) != null;
    }

    /**
     * 获取对象 并校验
     *
     * @param fileId
     * @param version
     * @return
     */
    public MaterialsList getEntityWithCheck(Long fileId, String version) {
        MaterialsList materialsList = null;
        if (org.apache.commons.lang3.StringUtils.isBlank(version)) {
            materialsList = this.getById(fileId);
        } else {
            materialsList = fileConvert.actualLib2Entity(materialsListLibMapper
                    .selectOne(Wrappers.<MaterialsListLib>lambdaQuery()
                            .eq(MaterialsListLib::getVersion, version)
                            .eq(MaterialsListLib::getOriginId, fileId))
            );
        }
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.FILE_NOT_EXIST);
        }
        return materialsList;
    }

    /**
     * 获取指定过期时间的上传url
     *
     * @param fileNames 文件路径
     * @param expireMs  过期时间 ms
     **/
    public Map<String, URL> presignedPut(List<String> fileNames, long expireMs) {
        return ossClient.presignedPut(fileNames, expireMs);
    }

    /**
     * 获取固定过期时间的上传url， 过期时间 3600 * 1000
     *
     * @param fileNames 文件路径
     **/
    public Map<String, URL> presignedPut(List<String> fileNames) {
        return ossClient.presignedPut(fileNames);
    }

    public List<MaterialsList> queryNeedSignFile(@NotNull Long contractId) {
        return materialsListMapper.queryNeedSignFile(contractId);
    }


    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public void rename(FileRenameREQ req) {
        MaterialsList materialsList = materialsListMapper.selectOne(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getId, req.getFileId()));
        if (Objects.isNull(materialsList)) {
            return;
        }
        String newfileName = req.getNewFileName();
        String suffix = FileNameUtil.getSuffix(newfileName);
        String name = FileNameUtil.getPrefix(newfileName);
        if (StrUtil.isNotEmpty(suffix)) {
            suffix = suffix.toLowerCase();
        } else {
            return;
        }
        String newOssFileName = buildFilePath(name, suffix, materialsList.getBelongId(), materialsList.getMaterialsType(), materialsList.getMaterialSubType(), materialsList.getBusinessType());
        String prevOssFileName = materialsList.getOssFilename();
        String oldPath = ossClient.getKey(prevOssFileName, true);
        String newPath = ossClient.getKey(newOssFileName, true);
        ossClient.copy(oldPath, newPath, false);
        MaterialsList toUpdate = new MaterialsList();
        toUpdate.setOssFilename(newOssFileName);
        toUpdate.setFilename(req.getNewFileName());
        materialsListMapper.update(toUpdate, Wrappers.<MaterialsList>lambdaUpdate().in(MaterialsList::getId, materialsList.getId()));
    }

    //copy 图片+记录，图片路径查询生成
    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public void copy(Long oldId, String fileName, Long belongId, String materialsType, String materialsSubType, String businessType) {
        MaterialsList oldList = this.getById(oldId);
        MaterialsList newList = BeanUtil.copyProperties(oldList, MaterialsList.class, "id", "belongId", "createBy", "updateBy", "createTime", "updateTime");
        newList.setBelongId(belongId);
        if (ObjectUtil.isNotEmpty(materialsType)) {
            newList.setMaterialsType(materialsType);
        }
        if (ObjectUtil.isNotEmpty(materialsSubType)) {
            newList.setMaterialSubType(materialsSubType);
        }
        if (ObjectUtil.isNotEmpty(businessType)) {
            newList.setBusinessType(businessType);
        }
        if (ObjectUtil.isNotEmpty(fileName)) {
            newList.setFilename(fileName);
        }

        String suffix = FileNameUtil.getSuffix(newList.getFilename());
        String name = FileNameUtil.getPrefix(newList.getFilename());

        newList.setFilename(buildFilePath(name, suffix, belongId, materialsType, materialsSubType, businessType));
        try {
            ossClient.copy(ossClient.getBasePath() + oldList.getOssFilename(), ossClient.getBasePath() + newList.getOssFilename());
        } catch (Exception e) {
            log.error("拷贝文件发生异常oldList = {}, newList = {}", oldList, newList, e);
            // 拷贝失败的话就不存文件记录了
            return;
        }
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (ObjectUtil.isNotEmpty(loginInfo)) {
            newList.setCreateBy(loginInfo.getId());
            newList.setUpdateBy(loginInfo.getId());
        }

        materialsListMapper.insert(newList);
    }
}
