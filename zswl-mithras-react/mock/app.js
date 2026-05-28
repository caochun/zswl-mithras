module.exports = {
  '/init': {
    access: '*',
    menu: [
      {
        title: '首页',
        path: '/',
      },
      {
        title: '权限管理',
        children: [
          {
            title: '机构管理',
            path: '/permission/organization',
          },
          {
            title: '角色管理',
            path: '/permission/role',
          },
          {
            title: '用户管理',
            path: '/permission/user',
          },
        ],
      },
      {
        title: '客户管理',
        children: [
          {
            title: '客户维护',
            path: '/customer/maintain',
          },
        ],
      },
    ],
  },
  'POST:/login': {
    token: '@integer',
  },
  'POST:/logout': {},
  'POST:/kpi/baseSet/suilv': [
    { id: 1, sopeName: '100分（含）以上', sopeValue: '=(P-60)/30*0.35+0.6' },
    { id: 2, sopeName: '95分（含）-100分', sopeValue: 2.3 },
    { id: 3, sopeName: '90分（含）-95分', sopeValue: 1.2 },
    { id: 4, sopeName: '85（含）-90分', sopeValue: 0.8 },
    { id: 5, sopeName: '80（含）-85分', sopeValue: 0.9 },
    { id: 6, sopeName: '70（含）-80分', sopeValue: 1.2 },
  ],
  'POST:/kpi/baseSet/suilv/save': {
    data: null,
  },
  'POST:/kpi/projectdistribution/history': [
    {
      version: '001',
      changeReason: '其他',
      operateDate: '2023-06-08',
      effectMonth: '11',
      effectYear: '2023',
      weightInfoWithTagList: [
        {
          id: 1,
          weightType: 'PROJECT_SPONSOR',
          weightTypeName: '项目主办',
          weightTarget: 68,
          weightTargetName: '李玉龙(liyulong)',
          weightValue: 12000,
          weightTargetNameRed: true,
          weightValueRed: false,
        },
        {
          id: 2,
          weightType: 'BUSINESS_DEPT',
          weightTypeName: '业务部门',
          weightTarget: 6,
          weightTargetName: '金属事业部',
          weightValue: 12000,
          weightTargetNameRed: true,
          weightValueRed: true,
        },
        {
          id: 3,
          weightType: 'BUSINESS_DEPT',
          weightTypeName: '业务部门',
          weightTarget: 7,
          weightTargetName: '化工建材业务部',
          weightValue: 12000,
          weightTargetNameRed: false,
          weightValueRed: false,
        },
      ],
    },
  ],
  'POST:/kpi/projectdistribution/weight/detail': {
    year: '2023',
    month: '12',
    weightInfoList: [
      {
        id: 1,
        weightType: 'PROJECT_SPONSOR',
        weightTypeName: '项目主办',
        weightTarget: 68,
        weightTargetName: '李玉龙(liyulong)',
        weightValue: 12000,
      },
      {
        id: 2,
        weightType: 'BUSINESS_DEPT',
        weightTypeName: '业务部门',
        weightTarget: 6,
        weightTargetName: '金属事业部',
        weightValue: 12000,
      },
      {
        id: 3,
        weightType: 'BUSINESS_DEPT',
        weightTypeName: '业务部门',
        weightTarget: 7,
        weightTargetName: '化工建材业务部',
        weightValue: 12000,
      },
    ],
  },

  'POST:/kpi/projectdistribution/pagelist': {
    list: [
      {
        id: -930579643276060,
        contractCode: '5CdSkYK',
        distributionStatus: -407444078593384,
        projName: 'Fcl',
        contractStartDate: 'o@oUsE',
        belongDeptId: -7974567111157132,
        belongDeptName: 'tmM0',
        sponsorUserId: -2588712334942876,
        sponsorUserName: '周超',
        weightInfoList: [
          {
            id: 1,
            weightType: 'PROJECT_SPONSOR',
            weightTypeName: '项目主办',
            weightTarget: '68',
            weightTargetName: '李玉龙李玉龙',
            weightValue: 12000,
          },
          {
            id: 2,
            weightType: 'BUSINESS_DEPT',
            weightTypeName: '业务部门',
            weightTarget: '68',
            weightTargetName: '交通运输部',
            weightValue: 12000,
          },
          {
            id: 2,
            weightType: 'BUSINESS_DEPT',
            weightTypeName: '业务部门',
            weightTarget: '68',
            weightTargetName: '交通运输部',
            weightValue: 12000,
          },
        ],
      },
    ],
    total: 6893337214900392,
    pages: -6086323905891644,
    pageSize: -8374521036117540,
    currentPage: -6046325207114132,
    others: {
      KEY: {},
    },
  },
}
