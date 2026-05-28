package cn.zswltech.mithras.api.collection;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.CollectionReconciliationLetterREQ;
import cn.zswltech.mithras.dto.collection.CollectionReconciliationLetterRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @ClassName CollectionReconciliationLetterApi
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/27 10:51 上午
 * @Version 1.0
 **/
@Api(tags = "合同收付款-对账函")
public interface CollectionReconciliationLetterApi {
    @ApiOperation("收款核销列表（excel导出）")
    @GetMapping("/collection/letter/export")
    void exportList(@Valid CollectionReconciliationLetterREQ req);

    @ApiOperation("收款核销列表（excel导出）")
    @PostMapping("/collection/letter/list")
    R<CollectionReconciliationLetterRSP> list(@RequestBody @Valid CollectionReconciliationLetterREQ req);

}
