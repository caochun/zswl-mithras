import { FiledFormat, InputColumn, MatchOptionColumn } from '@/components/Format'
import { FounderSelect, ApiSelect } from '@/components/Select'

const ALL_COLUMNS = [
  {
    title: '拜访对象',
    dataIndex: 'clientName',
  },
  {
    title: '拜访人',
    dataIndex: 'userId',
    editable: {
      element: <FounderSelect canJump={false} />,
      // functionCode: 'assetStrategyclientList',
    },
    render: (val, { createdName }) => <FiledFormat title={createdName} />,
  },
  {
    title: '拜访时间',
    dataIndex: 'checkInDate',
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          checkInDate: undefined,
          visitTimeFrom: startDataTime?.format('yyyy-MM-DD'),
          visitTimeTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  MatchOptionColumn({
    title: '拜访阶段',
    dataIndex: 'visitPhase',
    matchOption: 'visitPhaseStatus',
  }),
  MatchOptionColumn({
    title: '打卡类型',
    dataIndex: 'visitWay',
    matchOption: 'visitWayStatus',
  }),
  InputColumn({
    title: '部门',
    dataIndex: 'deptName',
  }),
  MatchOptionColumn({
    title: '拜访类型',
    dataIndex: 'visitType',
    matchOption: 'visitTypeStatus',
  }),
  InputColumn({
    title: '打卡地点/补卡地点',
    dataIndex: 'checkInLocation',
    width: 300,
  }),
  InputColumn({
    title: '关联项目编号',
    dataIndex: 'projCode',
  }),
  InputColumn({
    title: '关联合同编号',
    dataIndex: 'contractCode',
  }),
  InputColumn({
    title: '关联租后检查计划',
    dataIndex: 'checkPlanName',
  }),
  InputColumn({
    title: '人员',
    dataIndex: 'createdName',
  }),
  InputColumn({
    title: '拜访总次数',
    dataIndex: 'visitCount',
    _columnType: 'amount',
  }),
  InputColumn({
    title: '拜访总家数',
    dataIndex: 'clientCount',
    _columnType: 'amount',
  }),
]
export default ALL_COLUMNS
