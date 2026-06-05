package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListREQ;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemRemoveREQ;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.client.CorpSubjectItemApi;
import cn.zswltech.mithras.customer.application.client.api.CorpSubjectItemApplicationService;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpSubjectItemController implements CorpSubjectItemApi {
    @Resource
    private CorpSubjectItemApplicationService corpSubjectItemApplicationService;

    @Override
    public R<Void> importSubjectItems(@RequestParam("excelFile") MultipartFile excelFile, @RequestParam("clientId") Long clientId) {
        return corpSubjectItemApplicationService.importSubjectItems(excelFile, clientId);
    }

    @Override
    public R<List<CorpSubjectItemListRSP>> list(@RequestBody @Valid CorpSubjectItemListREQ req) {
        return corpSubjectItemApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid CorpSubjectItemRemoveREQ req) {
        return corpSubjectItemApplicationService.remove(req);
    }

    @Override
    public void downloadTemplate(@ApiParam("模板类型。1：法人，2：事业单位") @RequestParam("type") Integer type) throws Exception {
        corpSubjectItemApplicationService.downloadTemplate(type);
    }
}
