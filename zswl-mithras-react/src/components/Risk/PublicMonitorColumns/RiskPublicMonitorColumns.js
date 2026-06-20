import { Input, Radio, Rate, Tooltip, Typography } from 'antd'
import { FiledFormat } from '@/components/Format'
import { Select, App } from '@zswl/components'
import IconFont from '@/components/Icon'
const { Paragraph } = Typography

const { TextArea } = Input

const warnLevelColor = ['', 'green', '#f7cf07', 'red']

const ALL_COLUMNS = ({ canEdit, approvalCanEdit } = { canEdit: true }) => [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 160,
    editable: false,
  },
  {
    title: '客户名称',
    dataIndex: 'chiName',
    width: 300,
    editable: canEdit
      ? {
          element: <Input allowClear />,
        }
      : false,
    render: (val) => val,
  },
  {
    title: '预警星级',
    dataIndex: 'warnStar',
    width: 160,
    matchOption: 'warnStarEnum',
    editable: false,
    render: (val) => {
      return <Rate value={val} disabled count={3} style={{ fontSize: 18 }}></Rate>
    },
  },
  {
    title: '预警信号',
    dataIndex: 'warnLevel',
    editable: false,
    width: 120,
    matchOption: 'warnLevelEnum',
    render: (val) => {
      if (!val) return '-'
      return (
        <Tooltip title={App.matchOption('warnLevelEnum', val).label}>
          <IconFont type="icon-yujingxinhaodeng" style={{ color: warnLevelColor[val], fontSize: 18 }}></IconFont>
          {/* {warnLevelMatch[val]} */}
        </Tooltip>
      )
    },
  },
  {
    title: '标题',
    dataIndex: 'title',
    width: 300,
    editable: canEdit
      ? {
          element: <Input allowClear />,
        }
      : false,
    actions: ({ title, titleTarget, dataSource, newsUrl }) => {
      //数据来源dataSource: FHC-金控, XINSIGHT-慧眼
      return [
        {
          name: title,
          onClick: () => {
            if (dataSource && dataSource === 'XINSIGHT' && newsUrl) {
              window.open(newsUrl)
            }
            if (!titleTarget) return
            window.open(titleTarget)
          },
        },
      ]
    },
  },
  {
    title: '是否处置',
    dataIndex: 'handleResult',
    requiredMark: true,
    matchOption: [
      { label: '处置', value: 1 },
      { label: '关闭', value: 0 },
    ],
    editable: approvalCanEdit && {
      element: (
        <Radio.Group>
          <Radio value={1}>处置</Radio>
          <Radio value={0}>关闭</Radio>
        </Radio.Group>
      ),
      rules: [{ required: true, message: '请选择' }],
    },
  },
  {
    title: '统一社会信用代码',
    dataIndex: 'creditCode',
    width: 200,
    editable: canEdit
      ? {
          element: <Input allowClear />,
        }
      : false,
    render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '处置意见',
    dataIndex: 'advisement',
    requiredMark: true,
    width: 200,
    editable: approvalCanEdit && {
      element: <TextArea />,
      rules: [{ required: true, message: '请选择' }],
    },
    render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '是否起租',
    dataIndex: 'zhejiangInnerGroup',
    width: 220,
    matchOption: 'yesOrNo',
    editable: canEdit
      ? {
          element: <Select options="yesOrNo"></Select>,
        }
      : false,
  },
  {
    title: '主体机构代码',
    dataIndex: 'majorOrgCode',
    width: 150,
    editable: false,
    render: (val, record) => <FiledFormat title={val || '-'} />,
  },
  {
    title: '状态',
    dataIndex: 'handleStatus',
    width: 150,
    matchOption: 'riskControlOpinionHandleStatus',
    editable: canEdit
      ? {
          element: <Select options="riskControlOpinionHandleStatus"></Select>,
        }
      : false,
  },
  {
    title: '来源渠道',
    dataIndex: 'sourceName',
    width: 150,
    render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '链接',
    dataIndex: 'linkAddress',
    fixed: 'right',
    width: 100,
    editable: false,
    render: (v, record) => {
      if (record.dataSource === 'XINSIGHT' && record.newsUrl) {
        return (
          <a href={record.newsUrl} target="_blank">
            查看
          </a>
        )
      }
      return v ? (
        <a href={v} target="_blank">
          查看
        </a>
      ) : (
        '-'
      )
    },
  },
  {
    title: '来源类型',
    width: 100,
    dataIndex: 'opinionType',
    editable: false,
  },
  {
    title: '创建人',
    width: 120,
    dataIndex: 'createName',
    editable: false,
  },
  {
    title: '关联关系描述',
    width: 120,
    dataIndex: 'relationTypeName',
    editable: false,
  },
  {
    title: '信息发布日期',
    width: 180,
    dataIndex: 'infoPublDate',
    type: 'rangePicker',
    editable: false,
    // itemProps: {
    //   transform: (val) => {
    //     const [beganDate, endDate] = val || []
    //     return {
    //       infoPublDate: undefined,
    //       publishDateFrom: beganDate?.format('yyyy-MM-DD'), //HH:mm:ss
    //       publishDateTo: endDate?.format('yyyy-MM-DD'),
    //     }
    //   },
    // },
  },
  {
    title: '变更后内容',
    dataIndex: 'afterChange',
    span: 2,
  },

  {
    title: '企业名称',
    dataIndex: 'companyName',
  },
  {
    title: '企业编码',
    dataIndex: 'enterpriseCode',
  },
  // 变更
  {
    title: '变更事项',
    dataIndex: 'change',
  },
  {
    title: '变更事项描述',
    dataIndex: 'changeNote',
  },
  {
    title: '变更前内容',
    dataIndex: 'beforeChange',
    span: 2,
  },
  // 法院公告
  {
    title: '公告类型',
    dataIndex: 'announcementType',
  },
  {
    title: '公告内容',
    dataIndex: 'content',
    span: 2,
    render: (v) => (
      <Paragraph
        ellipsis={{
          rows: 5,
          expandable: true,
        }}
      >
        {v}
      </Paragraph>
    ),
  },
  {
    title: '当事人名称',
    dataIndex: 'partyName',
  },
  {
    title: '法官姓名',
    dataIndex: 'judgeName',
  },
  {
    title: '法律程序级别',
    dataIndex: 'processLevel',
  },
  // 开庭公告
  {
    title: '法院地址',
    dataIndex: 'caseRoom',
  },
  {
    title: '合法日期',
    dataIndex: 'lawfulDay',
  },
  {
    title: '案由',
    dataIndex: 'subjectMatter',
  },
  {
    title: '案由代码',
    dataIndex: 'subjectMatterCode',
  },
  {
    title: '原告',
    dataIndex: 'plaintiff',
  },
  {
    title: '被告',
    dataIndex: 'defendant',
  },
  {
    title: '首席法官',
    dataIndex: 'chiefJudge',
  },
  {
    title: '省份信息',
    dataIndex: 'state',
  },
  {
    title: '省份代码',
    dataIndex: 'stateCode',
  },
  // 立案
  {
    title: '案件号',
    dataIndex: 'caseNumber',
  },
  {
    title: '法院名称',
    dataIndex: 'courtName',
  },
  {
    title: '立案日期',
    dataIndex: 'caseDate',
  },
  {
    title: '地区',
    dataIndex: 'area',
  },
  {
    title: '案件状态描述',
    dataIndex: 'caseStatusDesc',
  },
  {
    title: '链接地址',
    dataIndex: 'linkAddress',
    render: (v) => {
      return v ? (
        <a href={v} target="_blank">
          查看
        </a>
      ) : (
        '-'
      )
    },
  },
]
export default ALL_COLUMNS
