import { ClientSelect } from '@/components/Select'
import { Select, App } from '@zswl/components'
import { FiledFormat, PercentageRender, MatchFormat } from '@/components/Format'
import { Input } from 'antd'
import { rangePresets } from '@/utils'
import { dateRangeTransform } from '@/utils/transform'
const { optionsType } = App.getData()

const ALL_COLUMNS = () => {
  return [
    {
      title: '计划名称',
      dataIndex: 'planName',
      width: 300,
      editable: {
        element: <Input />,
      },
    },
    {
      title: '客户名称',
      dataIndex: 'clientId',
      width: 300,
      editable: {
        element: <ClientSelect canJump={false} />,
        functionCode: 'assetStrategyclientList',
      },
      render: (val, { clientName }) => <FiledFormat title={clientName} />,
    },
    {
      title: '部门',
      dataIndex: 'bizDeptId',
      width: 220,
      render: (val, { bizDeptName }) => <FiledFormat title={bizDeptName} />,
    },
    {
      title: '风险敞口(元)',
      dataIndex: 'riskExposure',
      align: 'right',
      render: (val) => PercentageRender(val),
    },
    {
      title: '投放日期',
      dataIndex: 'paymentDate',
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '五级分类',
      dataIndex: 'classifyResult',
      render: (val) => <MatchFormat value={val} matchOption="assetClassifySuggestEnum" />,
    },
    {
      title: '项目经理',
      dataIndex: 'sponsorName',
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '检查人员',
      dataIndex: 'riskManagerName',
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '跟进频率',
      dataIndex: 'termName',
      render: (val) => <MatchFormat value={val} matchOption="afterLeaseCheckTermEnum" />,
    },

    {
      title: '上次跟进时间',
      dataIndex: 'lastEndDate',
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '上次跟进形式',
      dataIndex: 'lastCheckWay',
      render: (val) => <MatchFormat value={val} matchOption="afterLeaseCheckWayEnum" />,
    },
    // {
    //   title: '下次跟进时间',
    //   dataIndex: 'deadLine',
    //   render: (val) => <FiledFormat title={val} />,
    // },
    {
      title: '下次跟进时间',
      dataIndex: 'deadLineFrom',
      type: 'rangePicker',
      ranges: rangePresets,
      dateFormat: 'yyyy-MM-DD',
      itemProps: {
        transform: (val) => dateRangeTransform(val, 'deadLineFrom', 'deadLineTo'),
      },
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '下次跟进形式',
      dataIndex: 'checkWay',
      editable: {
        element: (
          <Select
            options={optionsType.afterLeaseCheckWayEnum.filter(
              (item) => item.value !== 'WITHOUT_CHECK'
            )}
          />
        ),
      },
      render: (val) => <MatchFormat value={val} matchOption="afterLeaseCheckWayEnum" />,
    },
    {
      title: '计划状态',
      dataIndex: 'approvalStatus',
      render: (val) => (
        <MatchFormat value={val} matchOption="afterLeaseCheckPlanProcessStatusEnum" />
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
    },
    {
      title: '变更时间',
      dataIndex: 'updateTime',
    },
  ]
}

export default ALL_COLUMNS
