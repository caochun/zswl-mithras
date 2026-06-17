import ProjEstablishTable from './StageDrawer/ProjEstablishTable'
import ProjReviewTable from './StageDrawer/ProjReviewTable'
import ProjReviewNoContractTable from './StageDrawer/ProjReviewNoContractTable'
import ContractTable from './StageDrawer/ContractTable'
import PreparePaymentTable from './StageDrawer/PreparePaymentTable'
import PaymentTable from './StageDrawer/PaymentTable'
import RePaymentTable from './StageDrawer/RePaymentTable'
import LegalReportTable from './StageDrawer/LegalReportTable'

export const initFieldsConfig = [
  {
    group: '立项阶段',
    groupCode: 'PROJECT_VIEW_STAGE_ESTABLISH',
    iconType: 'icon-lixiang1',
    tipContent: '通过「授信立项」、「项目立项」功能创建项目开始，到创建评审前',
    component: <ProjEstablishTable />,
  },
  {
    group: '评审阶段',
    groupCode: 'PROJECT_VIEW_STAGE_REVIEW',
    iconType: 'icon-pingshen1',
    tipContent: '提交评审到评审通过',
    component: <ProjReviewTable />,
  },
  {
    group: '评审通过未创建合同',
    groupCode: 'PROJECT_VIEW_STAGE_REVIEW_NO_CONTRACT',
    iconType: 'icon-hetong1',
    tipContent: '评审通过，但未通过「合同管理」功能创建合同',
    component: <ProjReviewNoContractTable />,
  },
  {
    group: '签约阶段',
    groupCode: 'PROJECT_VIEW_STAGE_CONTRACT',
    iconType: 'icon-qianyue1',
    tipContent: '已创建签约合同，但未完成签约并创建付款申请单',
    component: <ContractTable />,
  },
  {
    group: '投放阶段',
    groupCode: 'PROJECT_VIEW_STAGE_PAYMENT',
    iconType: 'icon-toufang1',
    tipContent: '付款申请单审批通过，还未完成付款核销',
    component: <PreparePaymentTable />,
  },
  {
    group: '付款阶段',
    groupCode: 'PROJECT_VIEW_STAGE_PREPARE_PAYMENT',
    iconType: 'icon-fukuan1',
    tipContent: '已创建付款申请单，但还未审批通过',
    component: <PaymentTable />,
  },
  {
    group: '还款阶段',
    groupCode: 'PROJECT_VIEW_STAGE_REPAYMENT',
    iconType: 'icon-huankuan',
    tipContent:
      '已进入租金收取的阶段（直租租前息也包含），但还没结束（全部偿还完款项并且保证金和厂商质保金也完成退还）',
    component: <RePaymentTable />,
  },
  {
    group: '待出具合规意见',
    groupCode: 'PROJECT_VIEW_STAGE_REVIEW_NO_LEGAL_REPORT',
    iconType: 'icon-heguiyijian',
    tipContent: '已发起项目评审流程，但尚未上传《法律合规意见书》',
    component: <LegalReportTable />,
  },
]

export const getNameColumns = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current?.component ?? <div></div>
}

export const columnsFilterKey = '工作台_项目视图_项目阶段'
