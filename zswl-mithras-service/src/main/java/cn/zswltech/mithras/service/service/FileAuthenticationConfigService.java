package cn.zswltech.mithras.service.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.file.FileAuthenticationConfigREQ;
import cn.zswltech.mithras.service.enums.OwnerTypeEnum;
import cn.zswltech.mithras.service.mapper.FileAuthenticationConfigMapper;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.FileAuthenticationConfig;
import cn.zswltech.mithras.service.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.leaseholdproperty.GeneralDictionaryService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【file_authentication_config(文件模版权限配置表)】的数据库操作Service实现
 * @createDate 2024-03-18 16:29:04
 */
@Service
public class FileAuthenticationConfigService extends ServiceImpl<FileAuthenticationConfigMapper, FileAuthenticationConfig> {

    @Resource
    private GeneralDictionaryService generalDictionaryService;
    @Resource
    private SysUserService sysUserService;
    @Resource(name = "userServiceAPI")
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public void setAuthenticationByPost(FileAuthenticationConfigREQ param) {
        if (CharSequenceUtil.isBlank(param.getPost()) && Objects.isNull(param.getUserId())) {
            throw new MithrasException("参数非法！岗位或者用户ID不存在！");
        }

        if (CharSequenceUtil.isNotBlank(param.getPost())) {
            GeneralDictionary post = generalDictionaryService.getOne(Wrappers.<GeneralDictionary>lambdaQuery()
                    .eq(GeneralDictionary::getCode, param.getPost())
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(post)) {
                throw new MithrasException("参数非法！岗位不存在！");
            }
            FileAuthenticationConfig fileAuthenticationConfig = buildConfig(param, OwnerTypeEnum.POST);
            this.save(fileAuthenticationConfig);
            return;
        }

        if (Objects.nonNull(param.getUserId())) {
            UserVO userVO = userService.getUserInfoById(param.getUserId()).getData();
            if (Objects.isNull(userVO)) {
                throw new MithrasException("用户不存在！");
            }
            FileAuthenticationConfig fileAuthenticationConfig = buildConfig(param, OwnerTypeEnum.PERSON);
            this.save(fileAuthenticationConfig);
        }
    }

    private FileAuthenticationConfig buildConfig(FileAuthenticationConfigREQ param, OwnerTypeEnum typeEnum) {
        return FileAuthenticationConfig.builder()
                .fileType(param.getFileType())
                .fileName(param.getFileName())
                .ownerType(typeEnum.getCode())
                .ownerPost(param.getPost())
                .createBy(Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(0L))
                .updateBy(Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(0L))
                .build();
    }

}




