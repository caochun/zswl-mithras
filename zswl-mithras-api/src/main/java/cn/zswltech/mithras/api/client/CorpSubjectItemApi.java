package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListREQ;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @author junke
 */
@Api(tags = "法人财务信息-接口")
public interface CorpSubjectItemApi {

    @ApiOperation("导入财报excel")
    @PostMapping("/corp/subject/item/import")
    R<Void> importSubjectItems(@RequestParam("excelFile") MultipartFile excelFile, @RequestParam("clientId") Long clientId);

    @ApiOperation("财报科目查询")
    @PostMapping("/corp/subject/item/list")
    R<List<CorpSubjectItemListRSP>> list(@RequestBody @Valid CorpSubjectItemListREQ req);

    @ApiOperation("删除财报")
    @PostMapping("/corp/subject/item/remove")
    R<Void> remove(@RequestBody @Valid CorpSubjectItemRemoveREQ req);

    @PostMapping("/corp/subject/item/template")
    @ApiOperation("财务报表导入模板")
    void downloadTemplate(@ApiParam("模板类型。1：法人，2：事业单位") @RequestParam("type") Integer type) throws Exception;

}
