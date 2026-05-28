import { observer } from '@zswl/admin'
import { Drawer, Table, SearchBar } from '@zswl/components'
import { getSearchColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { COMMON_COLUMNS } from '@/pages/report/Operation/Column'
import { ApiSelect } from '@/components'
import Api from '../api'
import { reportTitle } from '../index'
import { saveServer } from '@/utils'

const { Item } = SearchBar

const Index = ({ store }) => {
  const { listDrawerTable, listDrawer } = store

  const columns = ALL_COLUMNS
  const searchItem = getSearchColumns(COMMON_COLUMNS, ['岗位'])

  return (
    <Drawer
      store={listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={`${reportTitle}_明细`}
      onClose={listDrawer.close}
    >
      <Table
        scroll={{ x: true }}
        bordered
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: () => store.detailExport({ columns }),
          },
        ]}
        editable={false}
        columnsFilter={`管报_${reportTitle}_详情`}
        onFilter={(key,val) => saveServer(`管报_${reportTitle}_详情`,val)}
        store={listDrawerTable}
        searchbar={{
          initialValues: {},
          items: [
            <Item name="processInstanceIdList" label="流程ID">
              <ApiSelect
                placeholder="多个查询"
                debounceSearch
                maxTagCount={10}
                mode="multiple"
                api={Api.getList}
                transformResult={(res) => res?.list}
                searchField={'processInstanceId'}
                fieldNames={{
                  label: 'processInstanceId',
                  value: 'processInstanceId',
                }}
              ></ApiSelect>
            </Item>,
          ],
        }}
        columns={[...columns]}
      ></Table>
    </Drawer>
  )
}

export default observer(Index)
