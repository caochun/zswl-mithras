package cn.zswltech.mithras.api.groupcreditreview;

import cn.zswltech.mithras.api.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 后门接口 方便查看一个项目评审对应的授信评审，和一个授信评审对应多少项目评审
 *
 * @author wangchuanhao
 * @date 2022/11/28 10:01 AM
 */
@Api(tags = "集团授信评审数据查询-接口")
public interface GroupCreditReviewBackdoorApi {

    @ApiOperation("查询项目评审")
    @GetMapping("/group/credit/review/backdoor/queryProjReview")
    R<Map<String, Object>> queryProjReview(@RequestParam("projReviewId") Long projReviewId);

    @ApiOperation("查询授信评审")
    @GetMapping("/group/credit/review/backdoor/queryRelationship")
    R<List<Map<String, Object>>> queryRelationship(@RequestParam("groupCreditReviewId") Long groupCreditReviewId);

}
