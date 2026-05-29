package cn.zswltech.mithras.service.controller.fund;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.FundCreditApi;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.dto.materialsfile.FundMaterialListRSP;
import cn.zswltech.mithras.service.CreditLimitManagerService;
import cn.zswltech.mithras.service.CreditLimitService;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.FundCreditMaterialsEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.fund.FundCredit;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.bo.CreditLimitQueryBO;
import cn.zswltech.mithras.service.service.fund.FundCreditGuaranteeDetailService;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_credit
 * @date 2022-12-13
 */
@RestController
public class FundCreditController implements FundCreditApi {

    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FundCreditGuaranteeDetailService guaranteeDetailService;
    @Resource
    private CreditLimitManagerService creditLimitManagerService;

    @Override
    public R<Long> add(FundCreditAddREQ req) {
        //权限检验
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能创建!");
        }
        Long id = fundCreditService.add(req);
        return R.ok(id);
    }

    @Override
    public R<Void> modify(FundCreditModifyREQ req) {
        FundCredit toBeModify = fundCreditService.getById(req.getId());
        if (ObjectUtil.isNull(toBeModify)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能编辑!");
        }
        fundCreditService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundCreditListRSP> list(FundCreditListREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            return R.ok(null);
        }
        return R.ok(fundCreditService.list(req));
    }

    @Override
    public R<FundCreditDetailRSP> detail(FundCreditDetailREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            return R.ok(null);
        }
        return R.ok(fundCreditService.detail(req.getId()));
    }

    @Override
    public R<Void> remove(FundCreditRemoveREQ req) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能删除!");
        }
        fundCreditService.remove(req);
        return R.ok();
    }

    @Override
    public R<CreditLimitRsp> limit(CreditLimitReq req) {
        Pair<Long, Long> longLongPair = fundCreditService.queryCreditLimit(req.getOrganizationId());
        CreditLimitRsp creditLimitRsp = new CreditLimitRsp();
        creditLimitRsp.setOriginalCreditLimit(longLongPair.getKey());
        creditLimitRsp.setFinancingAmount(longLongPair.getValue());
        creditLimitRsp.setRemainingCreditLimit(longLongPair.getKey() - longLongPair.getValue());
        return R.ok(creditLimitRsp);
    }

    @Override
    public R<List<FundMaterialListRSP>> fileList(FundCreditDetailREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB", "ZJGLB") && !sysUserService.adminAuth()) {
            return R.ok(null);
        }
        return R.ok(fundCreditService.fileList(req.getId()));
    }

    @Override
    public R<Void> fileUpload(MultipartFile file, Long belongId) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可以上传资料！");
        }
        fundCreditService.fileUpload(file, belongId, FundCreditMaterialsEnum.DEFAULT.name());
        return R.ok();
    }

    @Override
    public R<Void> fileRemove(FundCreditRemoveREQ req) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可以删除资料！");
        }
        fundCreditService.fileRemove(req);
        return R.ok();
    }

    @Override
    public R<FileListRSP> fileDownload(FundCreditRemoveREQ req) throws IOException {
        // 下载权限校验
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可下载查看！");
        }
        List<MaterialsList> materials = materialsListService.getByIds(req.getIds());
        if (CollectionUtils.isEmpty(req.getIds())) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (materials.size() == 1) {
            return R.ok(materialsListService.download(req.getIds().get(0)));
        }
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip", "UTF-8"));
        materialsListService.download(outputStream, req.getIds());
        return R.ok();
    }

    @Override
    public R<List<FundCreditListRSP.FundCreditList>> listEffect(@Valid FundCreditListREQ req) {
        Assert.notNull(req.getOrganizationId(), () -> MithrasException.newException("机构id不能为空"));
        return R.ok(fundCreditService.listEffectByOrgId(req.getOrganizationId(), req.getEffective()));
    }

    @Override
    public R<Void> invalid(FundCreditSingletonIdREQ req) {
        fundCreditService.invalid(req);
        return R.ok();
    }

    @Override
    public R<FundCreditLimitDetailRSP> limitDetail(FundCreditSingletonIdREQ req) {
        return R.ok(fundCreditService.limitDetail(req));
    }

}