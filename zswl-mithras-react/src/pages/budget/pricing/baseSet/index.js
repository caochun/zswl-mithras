import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Table, Page, Access } from '@zswl/components'
import ModalDetail from '@/pages/budget/pricing/baseSet/ModalDetail'
import Store from './store'
import { saveServer } from '@/utils'

function Index() {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <Page>
      <Table
        columnsFilter="pricing_baseSet_1"
        onFilter={(key, val) => saveServer('pricing_baseSet_1', val)}
        scroll={false}
        resizable
        store={store.$table}
        columns={[
          {
            title: '参数类型',
            dataIndex: 'title',
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
                  onClick: () =>
                    store.editItem({
                      ...record,
                      isEdit: false,
                    }),
                },
                {
                  name: '编辑',
                  key: 'edit',
                  disabled: !Access.validate('newFtpParameterSettingModify'),
                  onClick: () =>
                    store.editItem({
                      ...record,
                      isEdit: true,
                    }),
                },
              ].filter(Boolean)
            },
          },
        ]}
      />
      <ModalDetail store={store}></ModalDetail>
    </Page>
  )
}

export default observer(Index)
