package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.customer.domain.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientAuthority;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.ClientTransferService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MessageNoticeInfoJob {

    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private ClientService clientService;


    /**
     * 工作台消息通知
     */
    @XxlJob("messageNoticeJob")
    public void MessageNoticeJob() {
        try {
            List<Long> targetClientIds;
            String jobParam = XxlJobHelper.getJobParam();
            if (StrUtil.isNotBlank(jobParam)) {
                targetClientIds = Collections.singletonList(Long.valueOf(jobParam));
            } else {
                // 查询所有存在管护权的客户
                targetClientIds = this.listHasManagerClientIds();
            }
            if (CollectionUtil.isEmpty(targetClientIds)) {
                return;
            }
            // 遍历判断是否可以释放
            for (Long clientId : targetClientIds) {
                String s = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN);
                try {
                    Client client = clientService.getById(clientId);
                    if (Objects.isNull(client)) {
                        log.error("没有找到{}的客户信息", clientId);
                        continue;
                    }
                    MDC.put(GlobalConstants.LOG_TRACE_ID, String.format("%s-%s", clientId, s));
                    clientService.trySendClientNotice(client);
                } catch (Exception e) {
                    log.error("释放客户发生异常[clientId:{}]", clientId, e);
                } finally {
                    MDC.remove(GlobalConstants.LOG_TRACE_ID);
                }
            }
            //项目即将结清
            try {
                clientService.projSettle(16);
            } catch (Exception e) {
                log.error("项目即将结清发生异常", e);
            }
            //租后检查提醒
            try {
                clientService.newAfterLeaseCheckPlanClient(16);
            } catch (Exception e) {
                log.error("租后检查提醒发生异常", e);
            }
            //账户到期提醒
            try {
                clientService.accountExpireNotice(16);
            } catch (Exception e) {
                log.error("账户到期提醒发生异常", e);
            }
            //还本付息提醒
            try {
                clientService.fundReceiptRepayNotice(16);
            } catch (Exception e) {
                log.error("还本付息发生异常", e);
            }

        } catch (Exception e) {
            log.error("客户移交任务失败");
        }
    }


    private List<Long> listHasManagerClientIds() {
        LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
        query.eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel());
        List<ClientAuthority> clientAuthorityList = clientAuthorityService.list(query);
        if (CollectionUtil.isEmpty(clientAuthorityList)) {
            return Collections.emptyList();
        }
        return clientAuthorityList.stream().map(ClientAuthority::getClientId).collect(Collectors.toList());
    }

}