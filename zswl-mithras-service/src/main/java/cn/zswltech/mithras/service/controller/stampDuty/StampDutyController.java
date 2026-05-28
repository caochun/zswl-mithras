package cn.zswltech.mithras.service.controller.stampDuty;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.stampduty.StampDutyApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.interestPay.*;
import cn.zswltech.mithras.dto.stampduty.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.mapper.model.stampduty.StampDutyDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.stampduty.ReportStampDutyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class StampDutyController implements StampDutyApi {

    @Resource
    private ReportStampDutyService reportStampDutyService;
    @Autowired
    private HttpServletResponse response;
    @Resource
    private OrgDOMapper orgDOMapper;

    @Override
    public R<StampDutyListRSP> listPage(StampDutyListREQ req) {
        return R.ok(reportStampDutyService.listPage(req));
    }

    @Override
    public R<StampDutyDetailREQ> add(StampDutyDetailREQ req) {
        return R.ok(reportStampDutyService.addStamp(req));
    }

    @Override
    public R<Void> remove(StampDutyBatchREQ req) {
        List<StampDutyDetail> res = new ArrayList<>();
        List<StampDutyDetail> toBeRemoves = reportStampDutyService.getBaseMapper().selectBatchIds(req.getIds());
        for (StampDutyDetail toBeRemove : toBeRemoves) {
            toBeRemove.setIsDelete("1");
            res.add(toBeRemove);
        }
        reportStampDutyService.saveOrUpdateBatch(res);
        return R.ok();
    }

    @Override
    public R<Void> importExcel(StampDutyImportREQ stampDutyImportREQ) {
        try {
            String fileName = stampDutyImportREQ.getFile().getOriginalFilename().toLowerCase();
            if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
                throw new MithrasException("请上传正确的印花税明细模板！");
            }
            reportStampDutyService.importExcel(stampDutyImportREQ.getFile().getInputStream());
            return R.ok();
        } catch (IOException e) {
            log.error("印花税缴纳明细读取异常", e);
            throw new MithrasException("读取导入文件异常");
        }
    }

    public R<StampDutyListRSP> contracts(StampDutyContractREQ req){
        return R.ok(reportStampDutyService.listContract(req));
    }

    @Override
    public void export(StampDutyBatchREQ param) {
        try {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("印花税管理台账" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            reportStampDutyService.exportExcel(response.getOutputStream(), param.getIds());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出台账管理表发生未知异常", e);
            throw new MithrasException("导出台账管理表发生未知异常");
        }
    }

    @Override
    public R<List<SelectRSP>> orgList(String name, Integer type) {
        //List<OrgDO> orgDOList = orgDOMapper.queryByName(name, type);
        Example example = new Example(OrgDO.class);
//        if (ObjectUtil.isNotEmpty(type)) {
//            example.createCriteria().andEqualTo("type", type);
//        }
        if (ObjectUtil.isNotEmpty(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        example.createCriteria().andNotIn("code", Arrays.asList("ZSZL", "LDC", "FXGLWYH", "XMPSWYH", "ADMIN", "DSH", "DJWYH", "ZSJK", "GGLRZX"));
        List<OrgDO> orgDOList = orgDOMapper.selectByExample(example);
        List<SelectRSP> result = new ArrayList<>();
        if (orgDOList != null) {
            result = orgDOList.stream().map(e -> new SelectRSP(e.getName(), e.getId().toString(), e.getState())).collect(Collectors.toList());
        }
        return R.ok(result);
    }
}
