import { OverviewTitle as Title } from '@/components/Dashboard'
import { Table } from '@zswl/components'
import { FiledFormat, AmountFormat } from '@/components/Format'
import Api from '@/api/dashboard/overview'
import styles from './index.less'
import { saveServer } from '@/utils'

const Index = ({ title, dataDate }) => {
  const table = Table.useStore({
    request: async () => {
      return await Api.postDashboardOverdueprojectList()
    },
  })
  return (
    <div>
      <Title
        title={title}
        extra={dataDate && <div className={styles.extra}>数据截止时间：{dataDate}</div>}
      ></Title>
      <Table
              columnsFilter={'overView_OverdueProject_1'}
              onFilter={(key,val) => saveServer('overView_OverdueProject_1',val)}
        className={styles.table}
        // serial
        resizable
        scroll={{ x: true }}
        store={table}
        columns={[
          {
            title: '逾期项目',
            dataIndex: 'projectName',
            render: (val) => <FiledFormat title={val} />,
          },
          {
            title: '逾期金额(元)',
            dataIndex: 'overdueAmount',
            align: 'right',
            render: (val) => <AmountFormat value={val.value} initFormat={1} />,
          },
          {
            title: '逾期天数(天)',
            dataIndex: 'overdueDays',
            render: (val) => <AmountFormat value={val.value} initFormat={1} />,
          },
          {
            title: '所属部门',
            dataIndex: 'deptName',
            render: (val) => <FiledFormat title={val} />,
          },
          {
            title: '所属业务',
            dataIndex: 'sponsorName',
            render: (val) => <FiledFormat title={val} />,
          },
        ]}
      ></Table>
    </div>
  )
}

export default Index
