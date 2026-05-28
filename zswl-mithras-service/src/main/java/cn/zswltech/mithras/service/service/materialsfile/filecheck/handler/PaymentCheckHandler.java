package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.FileConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.app.VisitPhaseStatus;
import cn.zswltech.mithras.service.enums.app.VisitRecordStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.payment.LendingMaterialType;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.service.service.payment.PaymentService;
import cn.zswltech.mithras.service.util.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @ClassName PaymentCheckHandler
 * 付款申请文件检查
 * @Author jackerhe
 * @Version 1.0
 **/
@Component
public class PaymentCheckHandler extends FileModuleCheck {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private PaymentService paymentService;
    @Resource
    private PaymentPolicyInfoMapper paymentPolicyInfoMapper;
    @Resource
    protected FileConvert fileConvert;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.PAYMENT.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        if (LendingMaterialType.LOAN_APPROVAL.name().equals(materialsType)) {
//            throw new MithrasException("该类文件只能在审批流中由放款审核岗位人员上传");
            // 放开限制，为了给流程中放款审核岗进行上传
            return;
        }
        if ("POLICY".equals(materialsType)) {
            PaymentPolicyInfo paymentPolicyInfo = paymentPolicyInfoMapper.selectById(mainId);
            mainId = paymentPolicyInfo.getPaymentId();
        }
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(mainId);
        authCheck(paymentBaseInfo);
    }


    @Override
    public List<Pair<String, List<FileListRSP>>> afterList(Long mainId, String moduleKey, FileListREQ req) {
        if (BusinessModuleEnum.PAYMENT.name().equalsIgnoreCase(moduleKey)) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(mainId);
            if (ObjectUtil.isNull(paymentBaseInfo)) {
                return null;
            }
            if (paymentBaseInfo.getContractId() != null) {
                List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                        .eq(MaterialsList::getBelongId, paymentBaseInfo.getContractId())
                        .in(MaterialsList::getMaterialsType, ListUtil.toList(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), LendingMaterialType.LEASE_RELATED.name()))
                );
                if (CollectionUtil.isNotEmpty(dataList)) {
                    dataList.removeIf(e -> StrUtil.isBlank(e.getSourceBusinessKey()));
                }
                List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
                fileConvert.fillName(rspList);
                rspList.sort(new CommonFileSortComparator());
                List<List<FileListRSP>> groupRspList = rspList.stream()
                        .collect(Collectors.groupingBy(FileListRSP::getMaterialsType))
                        .values().stream()
                        .collect(Collectors.toList());
                // 分组排序
                sortGroup(groupRspList);
                List<Pair<String, List<FileListRSP>>> resList = new ArrayList<>();
                for (List<FileListRSP> groupRsp : groupRspList) {
                    resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
                }
                return resList;
            }
        }
        return null;
    }

    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        if (LendingMaterialType.LOAN_APPROVAL.name().equals(materialsList.getMaterialsType())) {
            // 放开限制，为了给流程中放款审核岗进行删除
            return;
        }
        // 再查找付款主数据
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(paymentBaseInfo);
    }


    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        //下载权限同查看暂不限制
        super.checkDownload(moduleKey, mainId, fileId);
    }

    private void authCheck(PaymentBaseInfo paymentBaseInfo) {
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (PaymentStatusEnum.CLOSED.name().equals(paymentBaseInfo.getPaymentStatus())) {
            throw new MithrasException("该申请已关闭，不允许再修改有关信息");
        }
//        if(PaymentWriteOffStatus.WRITTEN_OFF.name().equals(paymentBaseInfo.getWriteOffStatus())){
//            throw new MithrasException("该申请已核销，不允许再修改有关信息");
//        }
        if (!ObjectUtil.equal(AccountUtil.getLoginInfo().getId(), paymentBaseInfo.getCreateBy())) {
            throw new MithrasException("权限校验失败: 只有付款创建人可以操作！");
        }
        ProcessResp processResp = paymentService.findRelatedProcess(paymentBaseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        List<MaterialsList> materialsLists = materialsListService.listByIds(fileIds);
        if (ObjectUtil.isNotEmpty(materialsLists)) {
            Map<Long, List<MaterialsList>> collect = materialsLists.stream().filter(e -> !Objects.equals(e.getMaterialsType(), LendingMaterialType.LOAN_APPROVAL.name())).collect(Collectors.groupingBy(MaterialsList::getBelongId));
            if (CollectionUtil.isEmpty(collect)) {
                return;
            }
            List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectBatchIds(collect.keySet());
            if (ObjectUtil.isEmpty(paymentBaseInfos)) {
                throw new MithrasException("未查询到付款相关数据");
            }
            paymentBaseInfos.forEach(payment -> {
                if (!Objects.equals(AccountUtil.getLoginInfo().getId(), payment.getCreateBy())) {
                    throw new AuthCheckException("权限校验失败: 只有付款创建人可以操作删除");
                }
            });
        }
    }

    protected int getGroupFileSort(FileListRSP rsp) {
        return 0;
    }

    /**
     * 文件分组排序
     * @param groupRspList
     */
    protected void sortGroup(List<List<FileListRSP>> groupRspList) {
        groupRspList.sort(Comparator.comparing(rsp -> getGroupFileSort(rsp.get(0))));
    }

}
