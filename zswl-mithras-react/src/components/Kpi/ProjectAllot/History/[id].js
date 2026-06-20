import { Table, TableStore, Page } from '@zswl/components'
import allotApi from '@/api/kpi/projectAllot/allot'
import ProjectAllocateList from '../ProjectAllocateList/KpiProjectAllotProjectAllocateList'
import moment from 'moment'
import { saveServer } from '@/utils'

const Index = ({ params: { id: projectDistributionId } }) => {
  const table = new TableStore({
    request: async (params) => {
      const result = await allotApi.postProjectdistributionHistory({
        ...params,
        projectDistributionId,
      })
      return result
    },
  })
  return (
    <Page>
      <Table
        columnsFilter={'projectAllot_history_idjs'}
        onFilter={(key, val) => saveServer('projectAllot_history_idjs', val)}
        rowKey={'version'}
        scroll={{ x: 1000 }}
        store={table}
        columns={[
          { title: '操作日期', dataIndex: 'operateDate' },
          {
            title: '分润比',
            width: 400,
            dataIndex: 'weightInfoWithTagList',
            render: (value) => {
              return <ProjectAllocateList.Detail value={value}></ProjectAllocateList.Detail>
            },
          },
          {
            title: '生效月份',
            dataIndex: 'effectMonth',
            render: (value, record) => {
              const { effectMonth, effectYear } = record
              return effectYear ? `${effectYear}年${effectMonth}月` : '-'
            },
          },
          { title: '变更原因', dataIndex: 'changeReason' },
        ]}
      ></Table>
    </Page>
  )
}

export default Index
