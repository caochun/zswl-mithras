package cn.zswltech.mithras.message.service.email;

import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.message.enums.EmailType;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.system.mapper.SystemConfigMapper;
import cn.zswltech.mithras.message.mapper.EmailSendFailLogMapper;
import cn.zswltech.mithras.system.mapper.model.SystemConfig;
import cn.zswltech.mithras.message.mapper.model.EmailSendFailLog;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.util.StringUtils;
import cn.zswltech.mithras.message.client.email.TianyiEmailUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @description: 邮件抽象类
 * @author: huangping
 * @date: 2025/11/26  19:12
 * @version: 1.0
 */
@Slf4j
public abstract class AbstractSendEmailHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractSendEmailHandler.class);


    @Value(value = "${common.notice.mithras.baseUrl}")
    protected String baseUrl;


    @Value("${spring.profiles.active}")
    private String active;

    private static final String PROD = "prod";

    private static final String EMAIL_END = "@cncico.com";

    @Resource
    private EmailSendFailLogMapper sendFailLogMapper;

    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;


    protected abstract String getSystemUrl();

    /**
     * 抽象方法：获取邮件标题（由子类自定义）
     *
     * @return 邮件标题字符串
     */
    protected abstract String generateEmailTitle();


    /**
     * 抽象方法：组装表格形式
     */
    protected abstract String generateTable(T businessData);


    /**
     * 抽象方法：是否html格式
     */
    protected abstract Boolean getIsHtml();


    /**
     * 抽象方法：生成邮件HTML内容（由子类自定义样式和结构）
     *
     * @return 完整的邮件HTML字符串
     */
    protected abstract String generateEmailHtml(String table, String systemUrl, T businessData);


    /**
     * 抽象方法：携带附件
     *
     * @return
     */
    protected abstract File[] getFiles(T businessData);




    /**
     * 公共邮件发送方法
     * 【注意 该方法使用 邮箱发送对象均为 用户】
     *
     * @param toIdSet  接收人ID
     * @param ccIdSet  抄送人ID
     * @param bccIdSet 密送人ID（原参数名修正为bcc语义，保持兼容）
     */
    public void newSendEmail(Set<Long> toIdSet, Set<Long> ccIdSet, Set<Long> bccIdSet, T businessData) {
        toIdSet = Objects.isNull(toIdSet)? new HashSet<>():toIdSet;
        ccIdSet = Objects.isNull(ccIdSet)? new HashSet<>():ccIdSet;
        bccIdSet = Objects.isNull(bccIdSet)? new HashSet<>():bccIdSet;
        log.info("newSendEmail-------初始化后：toIdSet:{}, ccIdSet:{}, bccIdSet:{}",
                JSON.toJSONString(toIdSet),
                JSON.toJSONString(ccIdSet),
                JSON.toJSONString(bccIdSet));
        Set<String> toSet = this.getUserEmailSet(toIdSet);
        //抄送、密送人 取调发送人
        filterToIdSet(toIdSet,ccIdSet);
        filterToIdSet(toIdSet,bccIdSet);
        Set<String> ccSet= this.getUserEmailSet(ccIdSet);
        Set<String> bccSet= this.getUserEmailSet(bccIdSet);
        this.sendEmail(toSet,ccSet,bccSet,businessData);
    }


    /**
     * 把idset中 与todiset重复元素过滤掉
     */
    private void filterToIdSet(Set<Long> toIdSet, Set<Long> idSet) {
        if (CollectionUtils.isEmpty(toIdSet) || CollectionUtils.isEmpty(idSet)) {
            return;
        }
        idSet.removeAll(toIdSet);
    }

    /**
     * 获取用户邮箱
     *
     */
    public Set<String>  getUserEmailSet(Set<Long> idSet){
        // 1. 空值校验：避免空指针
        if (CollectionUtils.isEmpty(idSet)) {
            return new HashSet<>();
        }
        // 2. 调用接口获取用户列表
        List<UserVO> userVOList = userServiceAPI.getUserInfoByIds(new ArrayList<>(idSet));
        if (CollectionUtils.isEmpty(userVOList)) {
            return new HashSet<>();
        }
        Set<String> emailSet = userVOList.stream()
                .filter(userVO -> Objects.equals(0,userVO.getStatus()))
                .filter(userVO -> StringUtils.isNotBlank(userVO.getEmail()))
                .map(UserVO::getEmail)
                .collect(Collectors.toSet());
        return emailSet;
    }





    /**
     * 公共邮件发送方法（支持包含 用户邮箱、客户邮箱、联系邮箱等）
     * ********【注意】：该方法为最终调用，如果包含【用户邮箱】，必须用户是启用状态！！！
     * @param toSet  接收人邮箱
     * @param ccSet  抄送人邮箱
     * @param bccSet 密送人邮箱（原参数名修正为bcc语义，保持兼容）
     */
    public void sendEmail(Set<String> toSet, Set<String> ccSet, Set<String> bccSet, T businessData) {
        try {
            if (CollectionUtils.isEmpty(toSet)) {
                log.info("sendEmail---接收人邮箱为空");
                return;
            }
            // 测试环境使用配置邮箱
            if (!Objects.equals(PROD, active)) {
                toSet=   getConfigEmail( toSet, "toSetEmails");
                ccSet=  getConfigEmail( ccSet, "ccSetEmails");
                bccSet =  getConfigEmail(  bccSet, "bccSetEmails");
            }
            // 公共逻辑：系统访问链接构建
            String systemUrl = getSystemUrl();
            // 公共逻辑：构建客户数据表格（子类复用此表格，仅自定义外层HTML）
            String table = generateTable(businessData);
            // 子类实现：生成差异化HTML
            String strHtml = generateEmailHtml(table, systemUrl, businessData);
            File[] files = getFiles(businessData);
            Boolean isHtml = getIsHtml();
            // 公共逻辑：触发邮件发送（复用原有工具类）
            String sessionId = TianyiEmailUtil.sendEMail(toSet, ccSet, bccSet, generateEmailTitle(), strHtml, isHtml, files);
            log.info("调用 TianyiEmailUtil.sendEMail ----end!! sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("sendEmail---邮件发送异常！！！e:{}", e);
            //落入日志表
            recordFailLog(toSet, ccSet, bccSet, businessData, e);
        }
    }

    /**
     * 取配置表
     * @param toSet 原接收人邮箱
     * @param configKey 配置key
     * @return 配置表-接收人邮箱
     */
    private Set<String>  getConfigEmail(Set<String> toSet,String configKey){
        toSet = Objects.isNull(toSet)? new HashSet<>():toSet;
        log.info("getConfigEmail-------初始化前：toSet:{},configKey:{}",JSON.toJSONString(toSet),configKey);
        //查询测试环境 配置用户
        toSet = getConfigSet(configKey);
        log.info("getConfigEmail-------初始化后：toSet:{},configKey:{}",JSON.toJSONString(toSet),configKey);
        return toSet;
    }



    private Set<String> getConfigSet(String configKey){
        Set<String> toSet = new HashSet<>();
        SystemConfig config = getBean(SystemConfigMapper.class).selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getConfigValue)
                .eq(SystemConfig::getConfigKey, configKey)
                .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));
        String configValue = config.getConfigValue();
        if (Objects.nonNull(configValue) && !configValue.trim().isEmpty()) {
            Arrays.stream(configValue.split(","))
                    .map(String::trim)
                    .filter(item -> !item.isEmpty())
                    .forEach(toSet::add);
        }
        return toSet;
    }




    // ==================== 失败日志记录（核心实现，匹配目标表结构）====================

    /**
     * 记录发送失败日志到数据库（严格按目标表字段赋值）
     */
    private void recordFailLog(Set<String> toSet, Set<String> ccSet, Set<String> bccSet, T businessData, Exception eInfo) {
        try {
            EmailSendFailLog failLog = new EmailSendFailLog();
            // 1. 邮箱相关字段（Set转逗号分隔字符串）
            failLog.setToSet(CollectionUtils.isEmpty(toSet) ? "无" : String.join(",", toSet));
            failLog.setCcSet(CollectionUtils.isEmpty(ccSet) ? "" : String.join(",", ccSet));
            failLog.setBccSet(CollectionUtils.isEmpty(bccSet) ? "" : String.join(",", bccSet));
            failLog.setEmailType(getEmailType().name());
            failLog.setEmailTitle(generateEmailTitle());
            // 3. 业务数据（JSON序列化，兼容任意泛型类型）
            try {
                String businessDataJson = JSON.toJSONString(businessData);
                // 关键优化：TEXT字段最大65535字节，预留100字节容错，截断阈值设为65435字节
                int maxLength = 65435;
                if (businessDataJson.getBytes(StandardCharsets.UTF_8).length > maxLength) {
                    // 按字节截断（避免按字符截断导致UTF-8中文乱码）
                    byte[] bytes = businessDataJson.getBytes(StandardCharsets.UTF_8);
                    byte[] truncatedBytes = Arrays.copyOf(bytes, maxLength);
                    businessDataJson = new String(truncatedBytes, StandardCharsets.UTF_8) + "...[数据超长已截断]";
                    log.warn("业务数据超长，已截断！日志ID后续补充，原始数据长度：{}字节", bytes.length);
                }
                failLog.setBusinessData(businessDataJson);
            } catch (Exception jsonE) {
                log.error("记录失败日志：业务数据JSON序列化失败", jsonE);
                failLog.setBusinessData("业务数据序列化失败：" + jsonE.getMessage());
            }
            // 4. 异常信息（截取前2000字符，避免text字段溢出）
            String errorMsg = eInfo.toString() + "\n" + Arrays.toString(eInfo.getStackTrace());
            if (errorMsg.length() > 2000) {
                errorMsg = errorMsg.substring(0, 2000) + "...";
            }
            failLog.setCreateBy(3L);
            failLog.setUpdateBy(3L);
            failLog.setErrorMsg(errorMsg);
            sendFailLogMapper.insert(failLog);
            log.info("邮件发送失败日志记录成功！日志ID：{}，邮件类型：{}", failLog.getId(), getEmailType());
        } catch (Exception e) {
            // 日志记录失败的兜底处理：打印关键信息到文件日志
            log.error("记录邮件发送失败日志异常！", e);
            log.error("关键失败信息备份：收件人={}，邮件类型={}，异常={}", toSet, getEmailType(), e.getMessage());
        }
    }


    public abstract boolean needHandle(EmailType emailType);


    public abstract EmailType getEmailType();

}
