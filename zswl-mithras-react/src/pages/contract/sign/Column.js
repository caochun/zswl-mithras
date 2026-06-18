import { MatchOptionColumn, InputColumn, FiledFormat, DateColumn } from '@/components/Format'
import { FounderSelect, ClientSelect } from '@/components/Select'

export const ALL_COLUMNS = [
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
    search: true,
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projectName',
    search: true,
  }),
  InputColumn({
    title: '客户名称',
    dataIndex: 'clientId',
    search: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'clientlist-2',
    },
    render: (value, { clientName }) => <FiledFormat title={clientName}></FiledFormat>,
  }),
  InputColumn({
    title: '项目主办',
    dataIndex: 'projSponsorUserId',
    search: {
      element: <FounderSelect />,
      functionCode: 'dashboardWorkbenchSelectFounder',
    },
    render: (value, { projSponsorUserName }) => (
      <FiledFormat title={projSponsorUserName}></FiledFormat>
    ),
  }),
  MatchOptionColumn({
    title: '签约方式',
    dataIndex: 'signingWay',
    matchOption: 'signingWayEnum',
    search: true,
  }),
  DateColumn({
    title: '推送时间',
    dataIndex: 'pushTime',
    search: true,
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
  }),
]
