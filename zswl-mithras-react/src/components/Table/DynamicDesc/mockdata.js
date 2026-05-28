const mock = [
  {
    groupName: '当地区域经济情况',
    contentList: [
      {
        templateId: 8,
        templateTitle: '承租人所在区域GDP、一般公共预算收入是否较上一年度下滑超过20%',
        templateContentInputType: 'radio',
        templateOptionList: [
          {
            label: '是',
            value: 1,
          },
          {
            label: '否',
            value: 0,
          },
          {
            label: '不适用',
            value: -1,
          },
        ],
        content: null,
      },
      {
        templateId: 18,
        templateTitle: '承租人所在区域内是否有融资主体出现违约行为',
        templateContentInputType: 'radio',
        templateOptionList: [
          {
            label: '是',
            value: 1,
          },
          {
            label: '否',
            value: 0,
          },
          {
            label: '不适用',
            value: -1,
          },
        ],
        content: null,
      },
    ],
  },
  // {
  //   groupName: '承租人经营情况',
  //   contentList: [
  //     {
  //       templateId: 28,
  //       templateTitle: '承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 38,
  //       templateTitle: '承租人本期是否出现住所、通讯地址、联系人、联系方式变更',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 48,
  //       templateTitle: '承租人主要职能定位及经营业务是否发生重大变化',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 58,
  //       templateTitle: '承租人融资渠道是否通畅',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 68,
  //       templateTitle: '是否存在被关闭或划转兼并的明确安排',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //   ],
  // },
  // {
  //   groupName: '租赁物',
  //   contentList: [
  //     {
  //       templateId: 78,
  //       templateTitle: '承租人是否将租赁物进行再次销售、转让',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 88,
  //       templateTitle: '承租人是否将租赁物进行了转租',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 98,
  //       templateTitle: '承租人是否将租赁物进行再次抵押、质押',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 108,
  //       templateTitle: '承租人是否将租赁物进行了投资入股、抵偿',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 118,
  //       templateTitle: '承租人是否将租赁物进行诉讼担保、是否对租赁物进行了保全担保等处置行为',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 128,
  //       templateTitle: '承租人是否以其他任何方式进行了侵害出租人对租赁设备的所有权的行为',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 138,
  //       templateTitle: '租赁物是否能够正常使用',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 148,
  //       templateTitle: '租赁物是否发生过升级换代、改造',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 158,
  //       templateTitle: '租赁物的位置是否被移动',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //     {
  //       templateId: 168,
  //       templateTitle: '租赁物是否发生过重大停产停运、重大故障、维修情况',
  //       templateContentInputType: 'radio',
  //       templateOptionList: [
  //         {
  //           label: '是',
  //           value: 1,
  //         },
  //         {
  //           label: '否',
  //           value: 0,
  //         },
  //         {
  //           label: '不适用',
  //           value: -1,
  //         },
  //       ],
  //       content: null,
  //     },
  //   ],
  // },
]

export default mock
