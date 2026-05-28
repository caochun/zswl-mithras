module.exports = {
  'POST:/risk/control/jzd/report/list': {
    list: [
      {
        id: 58,
        dataMonth: '2023-05-01',
        bizType: 'ZU_LIN',
        targetSubject: '332',
        bizAmountTotal: 12,
        bizAmountLeft: 34,
        clientName: '3232',
        clientSameTrade: '0',
        economicComposition: 'WSJJ',
        sponsorOrgName: '322332',
        bizStartDate: '2023-05-16',
        bizEndDate: '2023-09-16',
        ensureValue: 2233,
        guaranteeName: '4444',
        yjtjzValue: 33223,
        overdueDays: 32,
        overdueValue: 3,
        assetsCategory: 'SSL',
        reportStatus: 'NOT_REPORT',
        createTime: '2023-05-16 19:10:17',
        updateTime: '2023-05-16 19:10:17',
      },
    ],
    total: 1,
    pages: 1,
    pageSize: 10,
    currentPage: 1,
    others: {
      reportStatus: true,
    },
  },
  'POST:/risk/control/opinion/monitor/unresolved': {
    list: [
      {
        id: 1,
        title: '厂己得进名到区',
        chiName: '33ALnd',
        infoPublDate: '1989-02-01 20:56:21',
        warnStar: 63,
        warnLevel: 21,
      },
      {
        id: 51,
        title: '世织十北',
        chiName: 'xhG9j3',
        infoPublDate: '1972-01-16 03:07:58',
        warnStar: 27,
        warnLevel: 81,
      },
    ],
    total: 69,
    pages: 76,
    pageSize: 82,
    currentPage: 93,
  },
  'POST:/assetclassify/quarter/select': [
    {
      id: 1,
      quarter: 1,
      classificationAmounts: [
        {
          classifyResult: '正常',
          classifyAmount: 12,
        },
        {
          classifyResult: '关注',
          classifyAmount: 12,
        },
        {
          classifyResult: '次级',
          classifyAmount: 12,
        },
        {
          classifyResult: '可疑',
          classifyAmount: 12,
        },
        {
          classifyResult: '损失',
          classifyAmount: 12,
        },
      ],
    },
    {
      id: 2,
      quarter: 2,
      classificationAmounts: [
        {
          classifyResult: '正常',
          classifyAmount: 12,
        },
        {
          classifyResult: '关注',
          classifyAmount: 12,
        },
        {
          classifyResult: '次级',
          classifyAmount: 12,
        },
        {
          classifyResult: '可疑',
          classifyAmount: 12,
        },
        {
          classifyResult: '损失',
          classifyAmount: 12,
        },
      ],
    },
    {
      id: 3,
      quarter: 3,
      classificationAmounts: [
        {
          classifyResult: '正常',
          classifyAmount: 12,
        },
        {
          classifyResult: '关注',
          classifyAmount: 12,
        },
        {
          classifyResult: '次级',
          classifyAmount: 12,
        },
        {
          classifyResult: '可疑',
          classifyAmount: 12,
        },
        {
          classifyResult: '损失',
          classifyAmount: 12,
        },
      ],
    },
  ],

  'POST:/assetclassify/grade/process': [
    {
      nodeName: '初分',
      key: 'INIT',
      endTime: '2022-10-01',
      nodeStatue: 'FINISH',
    },
    {
      nodeName: '复核',
      key: 'REVIEW',
      endTime: '2022-10-01',
      nodeStatue: 'FINISH',
    },
    {
      nodeName: '评审会',
      key: 'REVIEW_MEETING',
      endTime: '2022-10-01',
      // nodeStatue: 'PROCESS',
      nodeStatue: 'FINISH',
    },
    {
      nodeName: '风委会',
      key: 'RISK_MEETING',
      endTime: '',
      nodeStatue: 'WAIT',
      // nodeStatue: 'PROCESS',
    },
    {
      nodeName: '董事会',
      key: 'BOARD_MEETING',
      endTime: '',
      nodeStatue: 'WAIT',
    },
  ],
}
