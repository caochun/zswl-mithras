import { Button, Page, SearchBar, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { JSONRender } from '@/components/Format'
import { ClientSelect, FounderSelect } from '@/components'
import { saveServer } from '@/utils'

const { Item } = SearchBar
function Index({ path }) {
  const columns = [
    { title: '接口路径', dataIndex: 'url' },
    { title: '操作人', dataIndex: 'userName' },
    { title: '接口参数', dataIndex: 'reqData', render: JSONRender },
    { title: '操作时间', dataIndex: 'operateTime' },
  ]

  return (
    <Page>
      <Table
        columnsFilter={'permission_log_1'}
        onFilter={(key, val) => saveServer('permission_log_1', val)}
        store={store.table}
        editable={false}
        selectable
        scroll={{ x: 'auto' }}
        resizable
        searchbar={{
          labelCol: { span: 6 },
          items: [
            <Item label="操作人" name="userId">
              <FounderSelect params={{ job: null }}></FounderSelect>
            </Item>,
          ],
        }}
        columns={columns}
      />
    </Page>
  )
}

export default observer(Index)
