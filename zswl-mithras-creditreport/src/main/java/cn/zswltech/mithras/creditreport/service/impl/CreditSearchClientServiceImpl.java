package cn.zswltech.mithras.creditreport.service.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditSearchClientQuery;
import cn.zswltech.mithras.creditreport.service.CreditReportQueryService;
import cn.zswltech.mithras.creditreport.service.CreditSearchClientService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CreditSearchClientServiceImpl implements CreditSearchClientService {


    @Resource
    private CreditReportQueryService creditReportQueryService;
/*
    @Override
    public PageR<CreditReportListDTO> list(CreditSearchClientQuery req) {
        LambdaQueryWrapper<CreditReportItemDO> wrapper = Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportItemDO::getClientId, req.getClientId());
        Page<CreditReportItemDO> page = new Page<>();
        List<CreditReportItemDO> items = creditReportItemDOMapper.selectPage(page, wrapper).getRecords();
        List<CreditReportListDTO> list = convertCreditReport(items);
        return PageR.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }*/

    @Override
    public PageR<CreditReportListDTO> list(CreditSearchClientQuery req) {
        CreditReportListREQ creditReportListREQ = new CreditReportListREQ();
        creditReportListREQ.setClientId(req.getClientId());
        creditReportListREQ.setPage(req.getPage());
        creditReportListREQ.setPageSize(req.getPageSize());
        return creditReportQueryService.list(creditReportListREQ);
        //return PageR.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }



    /*private List<CreditReportListDTO> convertCreditReport(List<CreditReportItemDO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        //提取用户和部门 ID 集合
        List<Long> userIdList = items.stream()
                .map(CreditReportDO::getApplyUser)
                .distinct()
                .collect(Collectors.toList());

        Set<Long> deptIdList = items.stream()
                .map(CreditReportDO::getApplyOrg)
                .collect(Collectors.toSet());

        //批量获取用户名和部门名
        Map<Long, String> userId2Name = userNameResolver.sysUserId2Name(userIdList);
        Map<Long, String> deptId2Name = deptNameResolver.deptId2Name(deptIdList);

        //最终转换为 DTO 列表
        return items.stream().map(item -> {

            // 构造 DTO
            CreditReportListDTO listDTO = new CreditReportListDTO();
            BeanUtils.copyProperties(item, listDTO);
            listDTO.setClientNameList(Collections.singletonList(item.getClientName()));
            listDTO.setCscCodeList(Collections.singletonList(item.getCscCode()));
            listDTO.setZhongZhengCodeList(Collections.singletonList(item.getZhongZhengCode()));
            listDTO.setSelectGoalList(Collections.singletonList(item.getSelectGoal()));

            // 设置用户和部门名称（带默认值）
            listDTO.setApplyUserName(userId2Name.getOrDefault(item.getApplyUser(), "未知用户"));
            listDTO.setApplyOrgName(deptId2Name.getOrDefault(item.getApplyOrg(), "未知部门"));

            return listDTO;
        }).collect(Collectors.toList());
    }
*/

    @Override
    public void delete(Long id) {
        creditReportQueryService.remove(id);
        /*CreditReportItemDO itemDO = creditReportItemDOMapper.selectById(id);
        if (Objects.isNull(itemDO)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!CreditApplyStatusEnum.UN_SUBMIT.name().equals(itemDO.getApplyStatus())) {
            throw new MithrasException("征信查询申请状态不是待提交，无法删除");
        }
        Long creditReportId = itemDO.getCreditReportId();
        Integer count = creditReportItemDOMapper.selectCount(Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportItemDO::getCreditReportId, creditReportId));
        if (count == 1) {
            creditReportDOMapper.deleteById(creditReportId);

        }
        creditReportItemDOMapper.deleteById(itemDO.getId());*/
    }
}
