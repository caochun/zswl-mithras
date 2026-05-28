export default {
  yesOrNoString: [
    { label: '是', value: '1' },
    { label: '否', value: '0' },
  ],
  yesOrNo: [
    { label: '是', value: 1 },
    { label: '否', value: 0 },
  ],
  trueOrFalse: [
    { label: '否', value: false },
    { label: '是', value: true },
  ],

  approvalStatus: [
    { label: '通过', value: true },
    { label: '不通过', value: false },
  ],
  isConfirmedEnum: [
    { label: '已确认', value: 1 },
    { label: '未确认', value: 0 },
  ],

  trueOrFalseEn: [{ label: 'Y', value: true }],

  effectiveOrInactive: [
    { label: '未失效', value: 0 },
    { label: '生效', value: 1 },
  ],

  effective: [
    { label: '失效', value: 0 },
    { label: '生效', value: 1 },
  ],
  effectiveEnum: [
    { label: '失效', value: false },
    { label: '生效', value: true },
  ],

  allowOrNo: [
    { label: '已签约', value: 1 },
    { label: '待签约', value: 0 },
  ],

  //文件类型
  fileType: [
    { label: 'PDF', value: '.pdf' },
    { label: 'WORD', value: '.doc' },
  ],
  //归档状态
  archivingStatus: [
    { label: '已归档', value: 'OVER', color: 'green' },
    { label: '待补充', value: 'WAITE', color: 'orange' },
  ],
  // 借阅状态
  fileStatus: [
    { label: '未借阅', value: '未借阅', color: 'default' },
    { label: '已借阅', value: '已借阅', color: 'green' },
    { label: '借阅审批中', value: '借阅审批中', color: 'orange' },
  ],
  //「优良」、「适中」、「较差」
  sourceLevel: [
    { label: '优良', value: 1 },
    { label: '适中', value: 2 },
    { label: '较差', value: 3 },
  ],
  // 启用、禁用
  earlyWarningState: [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 },
  ],
  // 还款频率 融资管理中使用
  repaymentFrequencyEnum: [
    { label: '按月', value: 'MONTH' },
    { label: '按双月', value: 'DOUBLE_MONTH' },
    { label: '按季', value: 'QUARTER' },
    { label: '按半年', value: 'HALF_YEAR' },
    { label: '按年', value: 'YEAR' },
    { label: '不规则', value: 'LRREGULAR' },
  ],
  // 预警
  warnStarEnum: [
    { label: '一星', value: 1 },
    { label: '二星', value: 2 },
    { label: '三星', value: 3 },
  ],
  warnLevelEnum: [
    { label: '绿灯', value: 1 },
    { label: '黄灯', value: 2 },
    { label: '红灯', value: 3 },
  ],
  // 五级分类
  afterLeaseReviewRiskEnum: [
    { label: '评委会阶段', value: 'ASSET_CLASSIFY_REVIEW_MEETING' },
    { label: '风委会阶段', value: 'ASSET_CLASSIFY_RISK_MEETING' },
  ],
  templateStatus: [
    { label: '是', value: '1' },
    { label: '否', value: '0' },
    { label: '不适用', value: '-1' },
  ],
  collectionWriteOffStatusLocalEnum: [
    { label: '未到期', value: 'TO_BE_WRITE_OFF', color: '#9f9f9f' },
    { label: '未核销', value: 'UNCOLLECTION', color: '#fd3845' },
    { label: '部分核销', value: 'PORTION_WRITTEN_OFF', color: '#ec824c' },
    { label: '核销完毕', value: 'WRITE_OFF_COMPLETED', color: '#24a573' },
  ],
  paymentWriteOffStatusEnum: [
    { label: '未到期', value: 'TO_BE_WRITE_OFF', color: '#9f9f9f' },
    { label: '未核销', value: 'NO_PAID', color: '#fd3845' },
    { label: '核销完毕', value: 'WRITTEN_OFF', color: '#24a573' },
    { label: '部分核销', value: 'PART_WRITTEN_OFF', color: '#ec824c' },
  ],
  financingFlowWriteOffStatusEnum: [
    { label: '部分核销', value: 'PART_WRITE_OFF', color: '#ec824c' },
    { label: '未核销', value: 'NO_WRITE_OFF', color: '#fd3845' },
    { label: '自动核销', value: 'AUTO_WRITE_OFF', color: '' },
    { label: '全部核销', value: 'COMPLETE_WRITE_OFF', color: '#24a573' },
    { label: '手工核销', value: 'HAND_WRITE_OFF', color: '#BEE7E9' },
    { label: '手工+自动', value: 'AUTO_HAND_WRITE_OFF', color: '#f28c58' },
  ],
  fundReceiptRepayCashFlowState: [
    { label: '未核销', value: 'NO_WRITE_OFF', color: '#fd3845' },
    { label: '核销中', value: 'WRITE_OFF_ING', color: '#fd3845' },
    { label: '部分核销', value: 'PART_WRITE_OFF', color: '#ec824c' },
    { label: '核销完毕', value: 'WRITTEN_OFF', color: '#24a573' },
    { label: '超额核销', value: 'BEYOND_WRITTEN_OFF', color: '#f28c58' },
  ],
  customerRatStatus: [
    { label: '已生效', value: 'true' },
    { label: '未生效', value: 'false' },
  ],
  workbenchOperationStatistics: [
    { label: '当期平均', value: 'CURRENT_TERM' },
    { label: '去年同期', value: 'LAST_TERM' },
    { label: '本年平均', value: 'CURRENT_YEAR' },
    { label: '去年平均', value: 'LAST_YEAR' },
  ],
  currencyEnum: [{ label: '人民币', value: 'CNY' }],
  litigationStageEnum: [
    { label: '一审', value: 'firstInstance' },
    { label: '二审', value: 'secondInstance' },
    { label: '再审', value: 'retrial' },
  ],
  periodUnderObservation: [
    { label: '六个月', value: '6' },
    { label: '十二个月', value: '12' },
  ],
  // 规则设置中的启用状态
  opinionEnableStatus: [
    { label: '启用', value: 1 },
    { label: '停用', value: 0 },
  ],
  // 预算收集状态
  budgetCollectionStatusEnum: [
    { label: '收集中', value: 'COLLECTING', color: '#1890ff' },
    { label: '收集完成', value: 'COLLECT_FINISH', color: '#52c41a' },
    { label: '已确认', value: 'CONFIRM', color: '#24a573' },
  ],
  yunyingPriorityEnum: [
    { label: '0', value: 0 },
    { label: '1', value: 1 },
    { label: '2', value: 2 },
    { label: '3', value: 3 },
    { label: '4', value: 4 },
    { label: '5', value: 5 },
    { label: '6', value: 6 },
    { label: '7', value: 7 },
    { label: '8', value: 8 },
    { label: '9', value: 9 },
  ],
  monthEnum: [
    { label: '1月', value: '1' },
    { label: '2月', value: '2' },
    { label: '3月', value: '3' },
    { label: '4月', value: '4' },
    { label: '5月', value: '5' },
    { label: '6月', value: '6' },
    { label: '7月', value: '7' },
    { label: '8月', value: '8' },
    { label: '9月', value: '9' },
    { label: '10月', value: '10' },
    { label: '11月', value: '11' },
    { label: '12月', value: '12' },
  ],
  quarterEnum: [
    { label: '1季度', value: '1' },
    { label: '2季度', value: '2' },
    { label: '3季度', value: '3' },
    { label: '4季度', value: '4' },
  ],
}
