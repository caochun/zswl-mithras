import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, Page, SearchBar } from '@zswl/components'
import { DatePicker } from 'antd'
import ModalDetail from '@/pages/kpi/baseSet/ModalDetail'
import moment from 'moment'
import { isAdminAccount } from '@/utils'
import ParameterModal from './ParameterModal'
import CopyItemModal from './CopyItemModal'
import Store from './store'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function Index() {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <Page>
      <Table
        columnsFilter={'kpi_baseSet_1'}
        onFilter={(key, val) => saveServer('kpi_baseSet_1', val)}
        scroll={false}
        store={store.$table}
        searchbar={{
          items: [
            <Item
              label="生效月份"
              name="effectMonth"
              transform={(values) => values && moment(values).endOf('months').format('yyyy-MM-DD')}
            >
              <DatePicker picker="month"></DatePicker>
            </Item>,
          ],
        }}
        columns={[
          {
            title: '生效月份',
            dataIndex: 'effectMonth',
            render: (val) => val && moment(val).format('yyyy-MM'),
          },
          {
            title: '创建人',
            dataIndex: 'createByName',
          },
          {
            title: '操作',
            fixed: 'right',
            width: 200,
            actions(record, rowIndex) {
              return [
                {
                  name: '查看',
                  key: 'view',
                  onClick: () => store.editItem({ ...record, isEdit: false }),
                },
                {
                  name: '编辑',
                  key: 'edit',
                  disabled: !isAdminAccount(),
                  onClick: () => store.editItem({ ...record, isEdit: true }),
                },
                {
                  name: '复制到',
                  key: 'copy',
                  disabled: !isAdminAccount(),
                  onClick: () => {
                    return store.copyItemModal.open({
                      ...record,
                      effectMonth: moment(),
                    })
                  },
                },
                {
                  name: '删除',
                  key: 'delete',
                  disabled: !isAdminAccount(),
                  onClick: ({ id }) => store.delete({ id }),
                  confirm: true,
                },
              ].filter(Boolean)
            },
          },
        ]}
      />
      <ModalDetail store={store}></ModalDetail>
      <ParameterModal store={store}></ParameterModal>
      <CopyItemModal store={store}></CopyItemModal>
    </Page>
  )
}

export default observer(Index)
