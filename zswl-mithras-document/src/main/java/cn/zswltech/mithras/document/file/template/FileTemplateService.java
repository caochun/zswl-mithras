package cn.zswltech.mithras.document.file.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswl.oss.core.minio.MinioOssClient;
import cn.zswl.oss.model.OssInfo;
import cn.zswltech.mithras.dto.file.template.FileTemplateHistoryListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateUpdateREQ;
import cn.zswltech.mithras.document.application.port.DocumentDictionaryPort;
import cn.zswltech.mithras.document.application.port.FileTemplateContractSignPort;
import cn.zswltech.mithras.document.persistence.mapper.FileAuthenticationConfigMapper;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.persistence.mapper.FileTemplateMapper;
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.document.persistence.model.FileAuthenticationConfig;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserResolver;
import cn.zswltech.mithras.document.util.ChineseToPinyinUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static java.util.Objects.isNull;

@Slf4j
@Service
public class FileTemplateService extends ServiceImpl<FileTemplateMapper, FileTemplate> {

    public static final String DICK_KEY = "FILE_TEMPLATE_TYPE";
    public static final String DICK_DESC = "文件模板类型";
    public static final String BASE_DIR = "/doc/%s/%s";
    private static final String BUSINESS_TYPE_FILE_TEMPLATE = "FILE_TEMPLATE";
    private static final int NO = 0;

    @Resource
    private DocumentDictionaryPort documentDictionaryPort;
    @Resource
    private FileAuthenticationConfigMapper fileAuthenticationConfigMapper;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private OssClient ossClient;
    @Resource
    private MinioOssClient minioOssClient;
    @Resource
    private CurrentUserResolver currentUserResolver;
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;
    @Resource
    private FileTemplateContractSignPort fileTemplateContractSignPort;

    @Value("${oss.minio.bucket-name}")
    private String bucketName;

    @Transactional(rollbackFor = Exception.class)
    public void addTemplateType(String name) {
        List<String> list = documentDictionaryPort.listCodesByDictKey(DICK_KEY);
        if (list.stream().anyMatch(e -> e.matches(name))) {
            err("已存在的文件模板类型");
        }
        documentDictionaryPort.add(DICK_KEY, DICK_DESC, name, name, list.size());
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeTemplateType(String name) {
        documentDictionaryPort.deleteByDictKeyAndCode(DICK_KEY, name);
    }

    public List<String> listTemplateTypes() {
        List<FileAuthenticationConfig> configList = new LinkedList<>();
        List<FileAuthenticationConfig> list = fileAuthenticationConfigMapper.selectList(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getOwnerId, currentUserResolver.currentUserId()));
        if (CollUtil.isNotEmpty(list)) {
            configList.addAll(list);
        }
        List<String> jobCodes = currentUserJobResolver.queryUserJobList(currentUserResolver.currentUserId());
        List<FileAuthenticationConfig> configs = fileAuthenticationConfigMapper.selectList(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .in(CollUtil.isNotEmpty(jobCodes), FileAuthenticationConfig::getOwnerPost, jobCodes));
        if (CollUtil.isNotEmpty(configs)) {
            configList.addAll(configs);
        }
        if (CollUtil.isEmpty(configList)) {
            return Collections.emptyList();
        }
        return configList.stream().map(FileAuthenticationConfig::getFileType).distinct().collect(Collectors.toList());
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void addTemplate(InputStream is, FileTemplate oldRecord) {
        String templateType = oldRecord.getTemplateType();
        String filename = oldRecord.getFilename();
        FileTemplate one = this.getOne(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getOutdated, false)
                .eq(FileTemplate::getTemplateType, templateType).eq(FileTemplate::getFilename, filename));
        err(isNotNull(one), "已经存在同样的模板文件");
        String string = filename.substring(0, filename.lastIndexOf("."));
        String substring = string;
        if (substring.contains(".")) {
            substring = string.substring(string.lastIndexOf(".") + 1);
        }
        String pinYinHeadChar = ChineseToPinyinUtil.getPinYinHeadChar(substring.replaceAll("（", "-").replaceAll("）", ""));
        FileTemplate fileTemplate = new FileTemplate().setTemplateType(templateType)
                .setFileTemplateKey(pinYinHeadChar)
                .setFaceSignShowFlag(Optional.of(oldRecord).map(FileTemplate::getFaceSignShowFlag).orElse(NO))
                .setFilename(filename).setOutdated(false);
        this.save(fileTemplate);
        String ossFilename = String.format(BASE_DIR, templateType, filename);
        addTemplateMaterial(is, ossFilename, fileTemplate.getId());
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void replaceTemplate(Long id, InputStream inputStream) {
        FileTemplate record = this.getById(id);
        checkAuthentication(record);
        this.updateById(new FileTemplate().setId(record.getId()).setOutdated(true));
        addTemplate(inputStream, record);
    }

    private void checkAuthentication(FileTemplate record) {
        Long userId = currentUserResolver.currentUserId();
        List<String> jobCodes = currentUserJobResolver.queryUserJobList(userId);
        FileAuthenticationConfig postConfig = fileAuthenticationConfigMapper.selectOne(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .in(CollUtil.isNotEmpty(jobCodes), FileAuthenticationConfig::getOwnerPost, jobCodes)
                .eq(FileAuthenticationConfig::getFileType, record.getTemplateType())
                .last(StringUtil.mysqlLimitOne()));
        FileAuthenticationConfig userConfig = fileAuthenticationConfigMapper.selectOne(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getOwnerId, userId)
                .eq(FileAuthenticationConfig::getFileType, record.getTemplateType())
                .last(StringUtil.mysqlLimitOne()));

        if (Objects.isNull(postConfig) && Objects.isNull(userConfig)) {
            throw new MithrasException("没有权限！请联系管理员！");
        }
    }

    public Page<FileTemplate> listTemplate(FileTemplateListREQ req) {
        List<FileAuthenticationConfig> configList = new LinkedList<>();
        List<FileAuthenticationConfig> list = fileAuthenticationConfigMapper.selectList(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getFileType, req.getTemplateType())
                .eq(FileAuthenticationConfig::getOwnerId, currentUserResolver.currentUserId()));
        List<String> jobCodes = currentUserJobResolver.queryUserJobList(currentUserResolver.currentUserId());
        List<FileAuthenticationConfig> configs = fileAuthenticationConfigMapper.selectList(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getFileType, req.getTemplateType())
                .in(FileAuthenticationConfig::getOwnerPost, jobCodes)
                .like(isNotBlank(req.getFilename()), FileAuthenticationConfig::getFileName, req.getFilename()));
        if (CollUtil.isEmpty(list) && CollUtil.isEmpty(configs)) {
            return new Page<>(req.getPage(), req.getPageSize());
        }
        if (CollUtil.isNotEmpty(list)) {
            configList.addAll(list);
        }
        if (CollUtil.isNotEmpty(configs)) {
            configList.addAll(configs);
        }
        List<String> fileNames = configList.stream().filter(a -> Objects.nonNull(a.getFileName()))
                .map(FileAuthenticationConfig::getFileName).collect(Collectors.toList());
        return this.page(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FileTemplate>lambdaQuery()
                        .eq(FileTemplate::getTemplateType, req.getTemplateType())
                        .eq(FileTemplate::getOutdated, false)
                        .in(CollUtil.isNotEmpty(fileNames), FileTemplate::getFilename, fileNames)
                        .orderByDesc(FileTemplate::getUpdateTime)
        );
    }

    public Page<FileTemplate> listTemplateHistory(FileTemplateHistoryListREQ req) {
        FileTemplate byId = this.getById(req.getId());
        err(isNull(byId), "记录不存在");
        checkAuthentication(byId);
        return this.page(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FileTemplate>lambdaQuery()
                        .eq(FileTemplate::getTemplateType, byId.getTemplateType())
                        .eq(FileTemplate::getFilename, byId.getFilename())
                        .eq(FileTemplate::getOutdated, true)
                        .orderByDesc(FileTemplate::getCreateTime)
        );
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void rollbackHistory(Long id) {
        FileTemplate byId = this.getById(id);
        err(isNull(byId), "记录不存在");
        checkAuthentication(byId);
        List<MaterialsList> materialsList = listTemplateMaterials(ListUtil.of(byId.getId()));
        MaterialsList materials = materialsList.get(0);
        FileTemplate one = this.getOne(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getTemplateType, byId.getTemplateType())
                .eq(FileTemplate::getOutdated, false)
                .eq(FileTemplate::getFilename, byId.getFilename()));
        InputStream inputStream = minioOssClient.downLoad(materials.getOssFilename());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IoUtil.copy(inputStream, bos);
        replaceTemplate(one.getId(), new ByteArrayInputStream(bos.toByteArray()));
    }

    public InputStream getTemplate(String templateType, String filename) {
        FileTemplate one = this.getOne(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getTemplateType, templateType)
                .eq(FileTemplate::getOutdated, false)
                .eq(FileTemplate::getFilename, filename));

        if (isNotNull(one)) {
            List<MaterialsList> materialsList = listTemplateMaterials(ListUtil.of(one.getId()));
            MaterialsList materials = materialsList.get(0);
            return minioOssClient.downLoad(materials.getOssFilename());
        }
        return null;
    }

    public FileTemplate getTemplateRecord(String templateType, String filename) {
        return this.getOne(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getTemplateType, templateType)
                .eq(FileTemplate::getOutdated, false)
                .eq(FileTemplate::getFilename, filename));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateTemplate(FileTemplateUpdateREQ req) {
        FileTemplate fileTemplate = getOne(Wrappers.<FileTemplate>lambdaQuery().eq(FileTemplate::getId, req.getId())
                .eq(FileTemplate::getOutdated, false));
        err(isNull(fileTemplate), "记录不存在");
        checkAuthentication(fileTemplate);

        FileTemplate template = new FileTemplate();
        template.setId(fileTemplate.getId());
        template.setFaceSignShowFlag(req.getFaceSignShowFlag());
        this.updateById(template);

        fileTemplateContractSignPort.updateFaceSignShowFlag(fileTemplate.getFileTemplateKey(), req.getFaceSignShowFlag());
    }

    private List<MaterialsList> listTemplateMaterials(List<Long> belongIds) {
        return materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BUSINESS_TYPE_FILE_TEMPLATE)
                .in(ObjectUtil.isNotEmpty(belongIds), MaterialsList::getBelongId, belongIds)
                .orderByDesc(MaterialsList::getUpdateTime));
    }

    @SneakyThrows
    private Long addTemplateMaterial(InputStream in, String fileName, Long belongId) {
        String suffix = FileNameUtil.getSuffix(fileName);
        String name = FileNameUtil.getPrefix(fileName);
        if (StrUtil.isEmpty(suffix)) {
            suffix = FileTypeUtil.getType(in);
        } else {
            suffix = suffix.toLowerCase();
        }
        String ossFileName = buildFilePath(name, suffix, belongId);
        OssInfo ossInfo = ossClient.upLoad(in, join("/", ossFileName), false);
        MaterialsList material = new MaterialsList();
        material.setFilename(fileName);
        material.setBelongId(belongId);
        material.setSuffix(suffix);
        material.setOssFilename(ossFileName);
        material.setFilePath(ossInfo.getPath());
        material.setBusinessType(BUSINESS_TYPE_FILE_TEMPLATE);
        material.setSystemGenerate(NO);
        materialsListMapper.insert(material);
        return material.getId();
    }

    private String buildFilePath(String fileName, String fileSuffix, Long belongId) {
        if (ObjectUtil.isNotNull(fileSuffix)) {
            fileSuffix = fileSuffix.toLowerCase();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(BUSINESS_TYPE_FILE_TEMPLATE).append("/");
        builder.append(belongId).append("/");
        String contentFileName = StrUtil.isBlank(fileSuffix) ? builder + fileName : builder + fileName + "." + fileSuffix;
        if (ossClient.doesObjectExist(bucketName, ossClient.getBasePath() + "/" + contentFileName)) {
            builder.append(System.currentTimeMillis()).append("/");
        }
        builder.append(fileName);
        return StringUtils.isEmpty(fileSuffix) ? builder.toString() : builder + "." + fileSuffix;
    }
}
