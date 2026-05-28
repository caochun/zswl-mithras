package cn.zswltech.mithras.api.groupcreditreview;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishVagueListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishVagueListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 集团授信评审基本信息表
* @author wangchuanhao
* @date 2022-11-11
*/
@Api(tags = "集团授信评审基本信息-接口")
public interface GroupCreditReviewBaseInfoApi {

    @ApiOperation("集团授信立项模糊查询")
    @PostMapping("/group/credit/review/establish/query")
    R<List<GroupCreditEstablishVagueListRSP>> vague(@RequestBody @Valid GroupCreditEstablishVagueListREQ req);


    @ApiOperation("新增集团授信评审基本信息")
    @PostMapping("/group/credit/review/base/info/add")
    R<GroupCreditReviewBaseInfoAddRSP> add(@RequestBody @Valid GroupCreditReviewBaseInfoAddREQ req);

    @ApiOperation("修改集团授信评审基本信息")
    @PostMapping("/group/credit/review/base/info/modify")
    R<Void> modify(@RequestBody @Valid GroupCreditReviewBaseInfoModifyREQ req);

    @ApiOperation("集团授信评审基本信息列表")
    @PostMapping("/group/credit/review/base/info/list")
    R<PageR<GroupCreditReviewListRSP>> list(@RequestBody @Valid GroupCreditReviewListREQ req);

    @ApiOperation("集团授信评审基本信息详情")
    @PostMapping("/group/credit/review/base/info/detail")
    R<GroupCreditReviewBaseInfoDetailRSP> detail(@RequestBody @Valid GroupCreditReviewBaseInfoDetailREQ req);

    @ApiOperation("获取剩余授信额度")
    @PostMapping("/group/credit/review/remainCreditAmount")
    R<Long> getClientStockRiskExposure(@RequestBody @Valid GroupCreditReviewBaseInfoDetailREQ req);

    @ApiOperation("更新评级信息")
    @PostMapping("/group/credit/review/base/info/updateRating")
    R<GroupCreditReviewInfoUpdateRatingRSP> updateRating(@RequestBody @Valid GroupCreditReviewBaseInfoUpdateRatingREQ req);
}