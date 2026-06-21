import { Access, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import ALL_COLUMNS from '../ParameterShared/Column'
import { useMemo } from 'react'
import Store from './store'
import ModalDetail from '../ParameterShared/ModalDetail'
import ModalEditTable from './ModalDetail/ModalEditTable'

function BudgetManagementParameterConfiguration() {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <Page>
      <Table
        store={store.$table}
        resizable
        scroll={false}
        columns={[
          ...ALL_COLUMNS,
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
      <ModalDetail store={store} ModalEditTable={ModalEditTable}></ModalDetail>
    </Page>
  )
}

export default observer(BudgetManagementParameterConfiguration)
