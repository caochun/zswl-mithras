package cn.zswltech.mithras.associationreport;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.service.*;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.service.others.MithrasException;

/**
  * @author dingqi
  * @date 2025/4/22
  * @description 
  */
public class DeleteDataSelector {
      public static DeleteData getInstance(AssociationReportCategoryEnum category) {
          switch (category) {
              case J0001: return SpringUtil.getBean(AssociationBasicSituationService.class);
              case J0002: return SpringUtil.getBean(AssociationShahStorInfoService.class);
              case J0003: return SpringUtil.getBean(AssociationShahChangeInfoService.class);
              case J0004: return SpringUtil.getBean(AssociationSeniorExecutiveInfoService.class);
              case J0005: return SpringUtil.getBean(AssociationBusinessSituationService.class);
              case J0006: return SpringUtil.getBean(AssociationEntityEconomyServiceService.class);
              case J0007: return SpringUtil.getBean(AssociationBalanceSheetPartialService.class);
              case J0008: return SpringUtil.getBean(AssociationCompanyProfitStatementService.class);
              case J0009: return SpringUtil.getBean(AssociationMainBusinessService.class);
              case J0010: return SpringUtil.getBean(AssociationExternalFinancingService.class);
              case J0011: return SpringUtil.getBean(AssociationTop10ClientConcentrationService.class);
              case J0012: return SpringUtil.getBean(AssociationRelationService.class);
              case J0013: return SpringUtil.getBean(AssociationLawInvolvedVisitRelatedInfoService.class);
              case J0014: return SpringUtil.getBean(AssociationMajorMattersBasicReportService.class);
              case J0015: return SpringUtil.getBean(AssociationMajorMattersEventReportService.class);
              default: throw new MithrasException("未定义的报表类型");
          }
      }
}
