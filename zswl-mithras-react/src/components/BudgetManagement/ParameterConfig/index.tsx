import React, { useMemo } from 'react'
import { Access, Page, Table } from '@zswl/components'
import ALL_COLUMNS from './Column'
import Store from './store'
import ModalDetail from './ModalDetail'

const ParameterConfig: React.FC = () => {
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
      <ModalDetail store={store}></ModalDetail>
    </Page>
  )
}

export default ParameterConfig
