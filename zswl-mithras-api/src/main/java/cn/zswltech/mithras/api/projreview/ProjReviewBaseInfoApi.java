package cn.zswltech.mithras.api.projreview;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewVagueListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewVagueListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckReasonREQ;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckResult;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckResultDetail;
import cn.zswltech.mithras.dto.projreview.ProjReviewButtonStatusRsp;
import cn.zswltech.mithras.dto.projreview.baseinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 立项基本信息表
 * @date 2022-08-02
 */
@Api(tags = "项目评审-基本信息接口")
public interface ProjReviewBaseInfoApi {

    @ApiOperation("立项模糊查询")
    @PostMapping("/proj/review/establish/query")
    R<List<ProjEstablishVagueListRSP>> vague(@RequestBody @Valid ProjEstablishVagueListREQ req);

    @ApiOperation("立项生效模糊查询")
    @PostMapping("/proj/review/query/effect")
    R<List<ProjEstablishVagueListRSP>> vagueEffect(@RequestBody @Valid ProjEstablishVagueListREQ req);

    /**
     * 首页模糊查询
     **/
    @ApiOperation("模糊查询授信评审")
    @PostMapping("/proj/review/group/credit/review/query")
    R<List<GroupCreditReviewVagueListRSP>> groupCreditReviewVague(@RequestBody @Valid GroupCreditReviewVagueListREQ req);


    @ApiOperation("新增项目评审基本信息表")
    @PostMapping("/proj/review/base/info/add")
    R<ProjReviewBaseInfoAddRSP> add(@RequestBody @Valid ProjReviewBaseInfoAddREQ req);

    @ApiOperation("新增项目评审基本信息表(选取集团授信)")
    @PostMapping("/proj/review/base/info/addByGroupCredit")
    R<ProjReviewBaseInfoAddRSP> addByGroupCredit(@RequestBody @Valid ProjReviewBaseInfoAddByGroupCreditREQ req);

    @ApiOperation("修改项目评审基本信息")
    @PostMapping("/proj/review/base/info/modify")
    R<Void> modify(@RequestBody @Valid ProjReviewBaseInfoModifyREQ req);

    @ApiOperation("更新评级信息")
    @PostMapping("/proj/review/base/info/updateRating")
    R<ProjReviewBaseInfoUpdateRatingRSP> updateRating(@RequestBody @Valid ProjReviewBaseInfoUpdateRatingREQ req);

    @ApiOperation("项目评审基本信息列表查询")
    @PostMapping("/proj/review/base/info/list")
    R<PageR<ProjReviewBaseInfoListRSP>> list(@RequestBody @Valid ProjReviewBaseInfoListREQ req);

    @ApiOperation("项目评审基本信息详情查询")
    @PostMapping("/proj/review/base/info/detail")
    R<ProjReviewBaseInfoDetailRSP> detail(@RequestBody @Valid ProjReviewBaseInfoDetailREQ req);

    @ApiOperation("删除立项基本信息")
    @PostMapping("/proj/review/base/info/disable")
    R<Void> disable(@RequestBody @Valid ProjReviewBaseInfoRemoveREQ req);

    @ApiOperation("获取评审按钮状态")
    @PostMapping("/proj/review/button/status")
    R<ProjReviewButtonStatusRsp> buttonStatus(@RequestBody @Valid ProjReviewBaseInfoDetailREQ req);

    @ApiOperation("校验评审相关客户财报情况")
    @PostMapping("/proj/review/client/subjectitem/checkresult")
    R<CorpSubjectItemCheckResult> preCheckSubjectCorpItem(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("校验材料评审是否提交")
    @PostMapping("/proj/review/material/comments/check")
    R<CorpSubjectItemCheckResult> preCheckReviewMaterialComments(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("获取评审下所有客户的财报校验结果详情")
    @PostMapping("/proj/review/client/subjectitem/checkresult/list")
    R<List<CorpSubjectItemCheckResultDetail>> listSubjectItemCheckResult(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("保存财报不完整原因")
    @PostMapping("/proj/review/client/subjectitem/checkresult/reason/save")
    R<Void> saveSubjectItemReason(@RequestBody @Valid CorpSubjectItemCheckReasonREQ req);
}
