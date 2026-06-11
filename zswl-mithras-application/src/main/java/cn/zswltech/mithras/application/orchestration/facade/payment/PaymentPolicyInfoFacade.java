package cn.zswltech.mithras.application.orchestration.facade.payment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.payment.application.PaymentPolicyInfoApplicationService;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.dto.payment.lib.*;
import cn.zswltech.mithras.dto.policy.PolicyInfoMaterialsListRSP;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.payment.application.checker.PaymentModifyAuthChecker;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentPolicyInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentPolicyInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description payment_policy_info
 * @date 2022-09-13
 */
@Service
@Slf4j
public class PaymentPolicyInfoFacade implements PaymentPolicyInfoApplicationService {

    @Resource
    private PaymentPolicyInfoService paymentPolicyInfoService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private OssClient ossClient;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;

    @Override
    @DataAuthCheck(keyFieldName = "paymentId", paramType = DataAuthCheck.ParamType.DIRECT,
            paramIndex = 1,checkerClass = PaymentModifyAuthChecker.class, businessModule = "PAYMENT", mapperClass = PaymentPolicyInfoMapper.class)
    public R<Void> add(MultipartFile[] files, Long paymentId,PaymentPolicyInfoAddREQ req) {
        paymentPolicyInfoService.add(files,paymentId,req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "paymentId", paramType = DataAuthCheck.ParamType.OBJECT,
            paramIndex = 2, checkerClass = PaymentModifyAuthChecker.class, businessModule = "PAYMENT", mapperClass = PaymentPolicyInfoMapper.class)
    public R<Void> modify(MultipartFile[] files, Long id,PaymentPolicyInfoModifyREQ req) {
        paymentPolicyInfoService.modify(files,id,req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = PaymentModifyAuthChecker.class, businessModule = "PAYMENT", mapperClass = PaymentPolicyInfoMapper.class)
    public R<Void> modifyFlag(@Valid PaymentPolicyInfoModifyFlagREQ req) {
        PaymentBaseInfo info = paymentBaseInfoMapper.selectById(req.getId());
        info.setPolicyFlag(req.getFlag());
        paymentBaseInfoMapper.updateById(info);
        return R.ok();
    }

    @Override
    public R<PageR<PaymentPolicyInfoListRSP>> list(PaymentPolicyInfoListREQ req) {
        Page<PaymentPolicyInfo> data = paymentPolicyInfoService.list(req);
        List<PaymentPolicyInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), PaymentPolicyInfoListRSP.class);
        if (CollUtil.isNotEmpty(list)){
            List<Long> belongIds = list.stream().map(PaymentPolicyInfoListRSP::getId).collect(Collectors.toList());
            List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBusinessType, "PAYMENTPOLICY")
                    .eq(MaterialsList::getMaterialsType,"POLICY")
                    .in(ObjectUtil.isNotEmpty(belongIds), MaterialsList::getBelongId, belongIds));
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(list.stream().map(PaymentPolicyInfoListRSP::getCreateBy).collect(Collectors.toList()));
            list.forEach(base -> {
                base.setCreateName(userId2Name.get(base.getCreateBy()));
            });
            if (CollUtil.isNotEmpty(materialsLists)){
                Map<Long, List<MaterialsList>> listMap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
                for (PaymentPolicyInfoListRSP paymentPolicyInfoListRSP : list) {
                    List<MaterialsList> materials = listMap.get(paymentPolicyInfoListRSP.getId());
                    List<PolicyInfoMaterialsListRSP> policyMaterials = new ArrayList<>();
                    if (CollUtil.isNotEmpty(materials)) {
                        for (MaterialsList material : materials) {
                            PolicyInfoMaterialsListRSP tmp = new PolicyInfoMaterialsListRSP();
                            tmp.setId(material.getId());
                            tmp.setName(material.getFilename());
                            policyMaterials.add(tmp);
                        }
                    }
                    paymentPolicyInfoListRSP.setFiles(policyMaterials);
                }
            }
        }
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> export(@Valid PaymentPolicyInfoExportREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("付款保单列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            paymentPolicyInfoService.export(req, httpServletResponse.getOutputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出付款保单发生未知异常", e);
            return R.fail("导出付款保单发生未知异常");
        }
    }

    @Override
    @DataAuthCheck(keyFieldName = "paymentId", checkerClass = PaymentModifyAuthChecker.class, businessModule = "PAYMENT", mapperClass = PaymentPolicyInfoMapper.class)
    public R<Void> remove(PaymentPolicyInfoRemoveREQ req) {
        paymentPolicyInfoService.remove(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "paymentId", checkerClass = PaymentModifyAuthChecker.class, businessModule = "PAYMENT", mapperClass = PaymentPolicyInfoMapper.class)
    public R<Void> removeBatch(@Valid PaymentPolicyInfoRemoveBatchREQ req) {
        paymentPolicyInfoService.removeBatch(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "paymentId", checkerClass = PaymentModifyAuthChecker.class, businessModule = "PAYMENT")
    public R<String> importExcel(@Valid PaymentPoliceImportREQ req) {
        try {
            return R.ok(paymentPolicyInfoService.importExcel(req.getFile().getInputStream(), req.getPaymentId()));
        } catch (IOException e) {
            throw new MithrasException("导入保单信息错误");
        }
    }

    @Override
    public R<String> downloadTemplate() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_NAME_PAYMENT_POLICY_ITEM, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_NAME_PAYMENT_POLICY_ITEM);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_NAME_PAYMENT_POLICY_ITEM, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            throw new MithrasException("下载租赁物清单模板发生未知异常");
        }
    }

}