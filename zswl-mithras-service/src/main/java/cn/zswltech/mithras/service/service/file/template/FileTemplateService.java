package cn.zswltech.mithras.service.service.file.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswl.oss.core.minio.MinioOssClient;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.OrgJobVO;
import cn.zswltech.mithras.dto.file.template.FileTemplateHistoryListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateUpdateREQ;
import cn.zswltech.mithras.service.mapper.corp.GeneralDictionaryMapper;
import cn.zswltech.mithras.service.mapper.file.template.FileTemplateMapper;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.FileAuthenticationConfig;
import cn.zswltech.mithras.service.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.FileAuthenticationConfigService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractSignInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.ChineseToPinyinUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.FILE_TEMPLATE;
import static cn.zswltech.mithras.service.enums.YesOrNoNumberEnum.NO;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static java.util.Objects.isNull;

/**
 * @author yibin
 */
@Slf4j
@Service
public class FileTemplateService extends ServiceImpl<FileTemplateMapper, FileTemplate> {
    public static final String DICK_KEY = "FILE_TEMPLATE_TYPE";
    public static final String DICK_DESC = "文件模板类型";
    public static final String BASE_DIR = "/doc/%s/%s";

    @Resource
    private FileAuthenticationConfigService fileAuthenticationConfigService;
    @Resource
    private ContractSignInfoService contractSignInfoService;

    @Transactional(rollbackFor = Exception.class)
    public void addTemplateType(String name) {
        List<GeneralDictionary> list = getBean(GeneralDictionaryMapper.class).selectList(
                Wrappers.<GeneralDictionary>lambdaQuery().eq(GeneralDictionary::getDictKey, DICK_KEY)
        );
        if (list.stream().anyMatch(e -> e.getCode().matches(name))) {
            err("已存在的文件模板类型");
        }
        GeneralDictionary dict = new GeneralDictionary();
        dict.setDictDesc(DICK_DESC);
        dict.setDictKey(DICK_KEY);
        dict.setCode(name);
        dict.setDisplay(name);
        dict.setSort(list.size());
        getBean(GeneralDictionaryMapper.class).insert(dict);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeTemplateType(String name) {
        getBean(GeneralDictionaryMapper.class).delete(
                Wrappers.<GeneralDictionary>lambdaQuery().eq(GeneralDictionary::getDictKey, DICK_KEY)
                        .eq(GeneralDictionary::getCode, name)
        );
    }

    public List<String> listTemplateTypes() {
        List<FileAuthenticationConfig> configList = new LinkedList<>();
        List<FileAuthenticationConfig> list = fileAuthenticationConfigService.list(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getOwnerId, AccountUtil.getLoginInfo().getId()));
        if (CollUtil.isNotEmpty(list)) {
            configList.addAll(list);
        }
        List<String> jobsName = new LinkedList<>();
        SpringContextHolder.getBean(UserService.class).getUserInfoById(AccountUtil.getLoginInfo().getId())
                .getData().getJobsName()
                .forEach(jobList -> {
                    jobsName.addAll(jobList.getJobNames().stream().map(OrgJobVO.Job::getJobCode).collect(Collectors.toList()));
                });
        List<FileAuthenticationConfig> configs = fileAuthenticationConfigService.list(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .in(CollUtil.isNotEmpty(jobsName), FileAuthenticationConfig::getOwnerPost, jobsName));
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
        // 需要初始化文件模版的Key
        String string = filename.substring(0, filename.lastIndexOf("."));
        String substring = string;
        if (substring.contains(".")) {
            substring = string.substring(string.lastIndexOf(".") + 1);
        }
        String pinYinHeadChar = ChineseToPinyinUtil.getPinYinHeadChar(substring.replaceAll("（", "-").replaceAll("）", ""));
        FileTemplate fileTemplate = new FileTemplate().setTemplateType(templateType)
                .setFileTemplateKey(pinYinHeadChar)
                .setFaceSignShowFlag(Optional.of(oldRecord).map(FileTemplate::getFaceSignShowFlag).orElse(NO.getCode()))
                .setFilename(filename).setOutdated(false);
        this.save(fileTemplate);
        String ossFilename = String.format(BASE_DIR, templateType, filename);
        getBean(MaterialsListService.class).newAdd(is, ossFilename, fileTemplate.getId(), null, null, FILE_TEMPLATE.name(), NO);
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void replaceTemplate(Long id, InputStream inputStream) {
        FileTemplate record = this.getById(id);
        checkAuthentication(record);
        this.updateById(new FileTemplate().setId(record.getId()).setOutdated(true));
        // 需要将原本的面签是否展示标识带过去
        addTemplate(inputStream, record);
    }

    private void checkAuthentication(FileTemplate record) {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobsName = new LinkedList<>();
        SpringContextHolder.getBean(UserService.class).getUserInfoById(AccountUtil.getLoginInfo().getId())
                .getData().getJobsName()
                .forEach(jobList->{
                    jobsName.addAll(jobList.getJobNames().stream().map(OrgJobVO.Job::getJobCode).collect(Collectors.toList()));
                });
        FileAuthenticationConfig config = fileAuthenticationConfigService.getOne(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .in(CollUtil.isNotEmpty(jobsName), FileAuthenticationConfig::getOwnerPost, jobsName)
                .eq(FileAuthenticationConfig::getFileType, record.getTemplateType())
                .last(StringUtil.mysqlLimitOne()));
        FileAuthenticationConfig configServiceOne = fileAuthenticationConfigService.getOne(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getOwnerId, userId)
                .eq(FileAuthenticationConfig::getFileType, record.getTemplateType())
                .last(StringUtil.mysqlLimitOne()));

        if (Objects.isNull(config) && Objects.isNull(configServiceOne)) {
            throw new MithrasException("没有权限！请联系管理员！");
        }
    }

    public Page<FileTemplate> listTemplate(FileTemplateListREQ req) {
        List<FileAuthenticationConfig> configList = new LinkedList<>();
        List<FileAuthenticationConfig> list = fileAuthenticationConfigService.list(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getFileType, req.getTemplateType())
                .eq(FileAuthenticationConfig::getOwnerId, AccountUtil.getLoginInfo().getId()));
        List<String> jobsName = new LinkedList<>();
        SpringContextHolder.getBean(UserService.class).getUserInfoById(AccountUtil.getLoginInfo().getId())
                .getData().getJobsName()
                .forEach(jobList -> {
                    jobsName.addAll(jobList.getJobNames().stream().map(OrgJobVO.Job::getJobCode).collect(Collectors.toList()));
                });
        List<FileAuthenticationConfig> configs = fileAuthenticationConfigService.list(Wrappers.<FileAuthenticationConfig>lambdaQuery()
                .eq(FileAuthenticationConfig::getFileType, req.getTemplateType())
                .in(FileAuthenticationConfig::getOwnerPost, jobsName)
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
        List<MaterialsList> materialsList = getBean(MaterialsListService.class).list(FILE_TEMPLATE.name(), null, ListUtil.of(byId.getId()));
        MaterialsList materials = materialsList.get(0);
        //
        FileTemplate one = this.getOne(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getTemplateType, byId.getTemplateType())
                .eq(FileTemplate::getOutdated, false)
                .eq(FileTemplate::getFilename, byId.getFilename()));
        InputStream inputStream = getBean(MinioOssClient.class).downLoad(materials.getOssFilename());
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
            List<MaterialsList> materialsList = getBean(MaterialsListService.class).list(FILE_TEMPLATE.name(), null, ListUtil.of(one.getId()));
            MaterialsList materials = materialsList.get(0);
            return getBean(MinioOssClient.class).downLoad(materials.getOssFilename());
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

        // 修改模板
        FileTemplate template = new FileTemplate();
        template.setId(fileTemplate.getId());
        template.setFaceSignShowFlag(req.getFaceSignShowFlag());
        this.updateById(template);

        // 还要更新合同签约人对应的文件
        contractSignInfoService.lambdaUpdate()
                .eq(ContractSignInfo::getFileTemplateKey, fileTemplate.getFileTemplateKey())
                .set(ContractSignInfo::getFaceSignShowFlag, req.getFaceSignShowFlag())
                .update();
    }
}
