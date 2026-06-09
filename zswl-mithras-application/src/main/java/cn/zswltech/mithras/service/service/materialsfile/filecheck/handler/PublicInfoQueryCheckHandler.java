package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.pubinfo.PublicInfoQuery;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.pubinfo.PublicInfoRecord;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoQueryService;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bigbear
 * @date 2024/12/26 18:40
 * @description
 */
@Component
public class PublicInfoQueryCheckHandler extends FileModuleCheck {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private PublicInfoRecordService publicInfoRecordService;
    @Autowired
    private PublicInfoQueryService publicInfoQueryService;
    @Autowired
    private FlowTaskApiService flowTaskApiService;
    @Autowired
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Autowired
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.PUBLIC_INFO.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        // 这里存在权限控制
        PublicInfoRecord infoRecordServiceById = publicInfoRecordService.getById(mainId);
        Assert.notNull(infoRecordServiceById, () -> MithrasException.newException("id: " + mainId + ResultMsg.RECORD_NOT_EXIST));

        PublicInfoQuery publicInfoQuery = publicInfoQueryService.getById(infoRecordServiceById.getPublicInfoQueryId());
        Assert.notNull(publicInfoQuery, () -> MithrasException.newException("id: " + infoRecordServiceById.getPublicInfoQueryId() + ResultMsg.RECORD_NOT_EXIST));

        // 校验当前用户是否有权限
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Assert.notNull(loginInfo, () -> MithrasException.newException(ResultMsg.USER_NOT_LOGIN));

        // 判断当前信息是否在流程中
        if (CharSequenceUtil.isBlank(publicInfoQuery.getProcessInstanceId())) {
            // 当前不在流程中，只有运营经办可以修改记录
            if (sysUserService.getUserByDeptCode(JobEnum.yunYingGuanLi.name())
                    .stream().map(UserDO::getId).noneMatch(obj -> obj.equals(loginInfo.getId()))) {
                throw MithrasException.newException("非【运营经办】没有变更权限");
            }
        } else {
            // 找到当前流程实例
            ProcessResp processResp = flowTaskApiService.queryProcessById(publicInfoQuery.getProcessInstanceId());
            Assert.notNull(processResp, () -> MithrasException.newException("id: " + publicInfoQuery.getProcessInstanceId() + "流程实例不存在"));
            // 当前流程一定要是【付款申请】流程
            Assert.isTrue(processResp.getModelKey().equals(ProcessModelTypeEnum.PaymentCreateFlow.name()), () -> MithrasException.newException("当前流程不是【付款申请】流程，不能修改"));
            // 判断当前节点是运营经办还是项目经理 -> 20241224变更：不再按审批节点区分编辑，判断当前审批节点审批人为合同主办时可编辑
            // 找到当前付款申请的项目主办
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(publicInfoQuery.getPaymentId());
            Assert.notNull(paymentBaseInfo, "未找到付款申请信息");
            // 找到合同主办
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
            Assert.notNull(contractBaseInfo, "未找到合同信息");
            if (contractBaseInfo.getProjSponsorUserId().equals(loginInfo.getId()) && processResp.getCurAssigneeIds().contains(loginInfo.getId().toString())) {
                // 如果是合同主办，并且当前结点审批人是合同主办，什么也不干
            } else if (!processResp.getCurTaskActivityIds().contains("userTask_startUser")) {
                throw MithrasException.newException("当前流程节点不支持修改");
            }
        }
    }
}
