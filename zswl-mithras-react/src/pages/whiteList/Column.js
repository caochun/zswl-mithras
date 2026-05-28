import { OrgSelect } from '@/components'
import {
  FiledFormat,
  MatchOptionColumn,
  DateColumn,
  InputEditable,
  InputColumn,
  FounderColumn,
} from '@/components/Format'
import { App } from '@zswl/components'

/**
 * 白名单管理表格列定义
 * 定义白名单列表和表单中使用的所有列配置
 */
const ALL_COLUMNS = [
  InputColumn({
    title: '评估机构名称',
    width: 250,
    dataIndex: 'companyName',
    search: true,
  }),
  {
    title: '社会统一信用代码',
    requiredMark: true,
    width: 200,
    dataIndex: 'uscCode',
    editable: {
      ...InputEditable({ required: true, disabled: false, placeholder: '请输入社会统一信用代码' }),
      rules: [
        { required: true, message: '请输入社会统一信用代码' },
        {
          pattern: /^[0-9A-HJ-NPQRTUWXY]{2}\d{6}[0-9A-HJ-NPQRTUWXY]{10}$/,
          message: '请输入正确的社会统一信用代码',
        },
      ],
    },
    render: (val) => <FiledFormat value={val} />,
  },
  MatchOptionColumn({
    title: '状态',
    dataIndex: 'recordStatus',
    matchOption: App.getData().optionsType.recordStatus.filter((v) => v.value !== 'CLOSED'),
    width: 120,
    search: true,
  }),
  MatchOptionColumn({
    title: '流程状态',
    dataIndex: 'processStatus',
    width: 120,
    matchOption: 'appraisalCompanyWhitelistProcessStatusEnum',
    search: true,
  }),
  DateColumn({
    title: '到期日',
    dataIndex: 'recordExpireDate',
    width: 180,
    search: true,
  }),
  DateColumn({
    title: '生效日',
    dataIndex: 'recordEffectDate',
    width: 180,
  }),
  FounderColumn({
    title: '创建人',
    dataIndex: 'createBy',
    renderField: 'createByName',
    params: { job: 'projManager' },
    // functionCode,
    search: true,
  }),
  FounderColumn({
    title: '最近更新人',
    dataIndex: 'lastOperatorId',
    renderField: 'lastOperatorName',
    params: { job: 'projManager' },
    search: false,
  }),

  {
    title: '创建部门',
    dataIndex: 'deptId',
    search: { element: <OrgSelect /> },
    render: (val, record) => <FiledFormat title={record?.deptName} />,
  },

  DateColumn({
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
  }),
  DateColumn({
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 180,
    search: true,
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
  }),

  DateColumn({
    title: '成立日期',
    dataIndex: 'establishDate',
    width: 180,
  }),
  DateColumn({
    title: '营业许可证到期日',
    dataIndex: 'licenseExpireDate',
    width: 180,
  }),
  MatchOptionColumn({
    title: '营业许可证是否为长期',
    dataIndex: 'licenseIsLongTerm',
    width: 180,
    matchOption: 'yesOrNo',
  }),
  {
    title: '关联项目名称',
    dataIndex: 'relatedProjectList',
    width: 180,
    render: (val) => val?.map((item) => item.value).join(',') || '-',
  },
  {
    title: '业务范围',
    dataIndex: 'businessScope',
    width: 180,
    span: 2,
  },
]

export default ALL_COLUMNS
