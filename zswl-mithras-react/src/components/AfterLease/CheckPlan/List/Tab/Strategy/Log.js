import { Table, Drawer } from '@zswl/components'
import { FiledFormat } from '@/components/Format'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  return (
    <Drawer title="修改记录" store={store.$updateDrawer} width={900} extra={null}>
      <Table
        autoRequest={false}
        store={store.$updateTable}
        scroll={{ x: true }}
        columnWidth={180}
        columnsFilter={'Tab_Strategy_Log'}
                onFilter={(key,val) => saveServer('Tab_Strategy_Log',val)}

        columns={[
          {
            title: '日期',
            dataIndex: 'createTime',
            render: (val) => <FiledFormat title={val} />,
          },
          {
            title: '变更人',
            dataIndex: 'createByName',
            render: (val) => <FiledFormat title={val} />,
          },
          {
            title: '变更前内容',
            dataIndex: 'oldContent',
            render: (val) => <FiledFormat title={val} />,
          },
          {
            title: '变更后内容',
            dataIndex: 'nowContent',
            render: (val) => <FiledFormat title={val} />,
          },
        ]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
