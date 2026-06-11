package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientProcessStatus;
import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 校验用户是否可保存
 *
 * @author wangchuanhao
 * @date 2022/6/24 11:48 AM
 */
public interface ClientDataSaveCheckInterface<T extends ClientBaseModel> {

    default void check(T t) {
        checkByClientId(t.getClientId());
    }

    default void checkByClientId(Long clientId) {
        ClientDataSaveCheckPort clientDataSaveCheckPort = SpringContextHolder.getBean(ClientDataSaveCheckPort.class);
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        if (Objects.isNull(clientId)) {
            throw new MithrasException("客户信息不存在，不可修改数据");
        }
        if (!clientDataSaveCheckPort.canSave(clientId)) {
            throw new MithrasException("客户信息处于审批流程中，不可修改数据");
        }
        //校验是否有权限
        /*if(!clientService.checkClientAuth(clientId)){
            throw new MithrasException("非客户主办，无法修改");
        }*/
    }

    default void recordClientStatus(T t) {
        recordClientStatusByClientId(t.getClientId());
    }

    default void recordClientStatusByClientId(Long clientId) {
        ClientDataSaveCheckPort clientDataSaveCheckPort = SpringContextHolder.getBean(ClientDataSaveCheckPort.class);
        ClientMapper clientMapper = SpringContextHolder.getBean(ClientMapper.class);
        boolean hasRelatedProcess = clientDataSaveCheckPort.hasRelatedProcess(clientId);
        Client client = clientMapper.selectById(clientId);
        if (Objects.isNull(client)) {
            return;
        }
        // 如果审批流程中保存了数据或客户状态为新建时 只更新最后更新时间 不更新客户状态
        if ((ClientStatus.NEW.name().equalsIgnoreCase(client.getClientStatus())
                && Objects.equals(client.getIsReleased(), YesOrNoNumberEnum.NO.getCode())) || hasRelatedProcess) {
            // 只更新 最后更新时间
            LambdaUpdateWrapper<Client> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Client::getId, client.getId());
            updateWrapper.set(Client::getUpdateTime, LocalDateTime.now());
            clientMapper.update(null, updateWrapper);
            return;
        }
        // 都是变更中 是否需要审批不在此处记录 否则客户流程状态需要随着项目评审数据的变动而变动
        clientDataSaveCheckPort.recordClientStatus(clientId, null, ClientProcessStatus.UN_SUBMIT);
        // 判断是否立项
//        ProjReviewService projReviewService = SpringContextHolder.getBean(ProjReviewService.class);
//        if (projReviewService.clientRelatedProjReview(clientId)) {
//            clientService.recordClientStatus(clientId, null, ClientProcessStatus.UN_SUBMIT);
//        } else {
//            clientService.recordClientStatus(clientId, null, ClientProcessStatus.NO_APPROVAL_REQUIRED);
//        }
    }

}
