package cn.zswltech.mithras.document.file.template;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.basedata.mapper.GeneralDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.dto.file.FileAuthenticationConfigREQ;
import cn.zswltech.mithras.document.enums.OwnerTypeEnum;
import cn.zswltech.mithras.document.mapper.FileAuthenticationConfigMapper;
import cn.zswltech.mithras.document.model.FileAuthenticationConfig;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangxiong
 * @description 针对表【file_authentication_config(文件模版权限配置表)】的数据库操作Service实现
 * @createDate 2024-03-18 16:29:04
 */
@Service
public class FileAuthenticationConfigService extends ServiceImpl<FileAuthenticationConfigMapper, FileAuthenticationConfig> {

    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;
    @Resource
    private CurrentUserResolver currentUserResolver;
    @Resource
    private UserNameResolver userNameResolver;

    @Transactional(rollbackFor = Exception.class)
    public void setAuthenticationByPost(FileAuthenticationConfigREQ param) {
        if (CharSequenceUtil.isBlank(param.getPost()) && Objects.isNull(param.getUserId())) {
            throw new MithrasException("参数非法！岗位或者用户ID不存在！");
        }

        if (CharSequenceUtil.isNotBlank(param.getPost())) {
            GeneralDictionary post = generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery()
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
            Map<Long, String> userNameMap = userNameResolver.sysUserId2Name(Collections.singleton(param.getUserId()));
            if (userNameMap.isEmpty() || !userNameMap.containsKey(param.getUserId())) {
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
                .ownerId(param.getUserId())
                .createBy(Optional.ofNullable(currentUserResolver.currentUserId()).orElse(0L))
                .updateBy(Optional.ofNullable(currentUserResolver.currentUserId()).orElse(0L))
                .build();
    }

}




