const TEMPLATE_LIST = [
  {
    name: '租后检查报告(产业类)',
    value: 'NON_PUBLIC',
    children: [
      { name: '租后检查报告（现场/非现场）（一般风险业务）', value: 0 },
      { name: '财务数据', value: 3 },
      { name: '承租人财务情况', value: 4 },
      { name: '担保人财务情况', value: 5 },
    ],
  },
  {
    name: '租后检查报告(公共事业、民生消费类)',
    value: 'PUBLIC',
    children: [
      { name: '租后检查报告(公用事业、民生消费类)', value: 2 },
      { name: '财务数据', value: 3 },
      { name: '承租人财务情况', value: 4 },
      { name: '担保人财务情况', value: 5 },
    ],
  },
  {
    name: '租后检查报告(低风险业务)',
    value: 'LOW_RISK',
    children: [{ name: '租后检查报告(低风险业务)', value: 6 }],
  },
  {
    name: '租后检查报告(公交类)',
    value: 'BUS',
    children: [
      { name: '租后检查报告(公交类)', value: 7 },
      { name: '财务数据', value: 3 },
      { name: '承租人财务情况', value: 4 },
      { name: '担保人财务情况', value: 5 },
    ],
  },
  {
    name: '租后检查报告(国有资产类)',
    value: 'STATE_OWNED_ASSET',
    children: [
      { name: '租后检查报告(国有资产类)', value: 8 },
      { name: '财务数据', value: 3 },
      { name: '承租人财务情况', value: 4 },
      { name: '担保人财务情况', value: 5 },
    ],
  },
]

export { TEMPLATE_LIST }
