import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns } from '@/utils'
import { POJ_COLUMNS, FIN_COLUMNS } from './Column'
import { Tabs } from 'antd'
import { saveServer } from '@/utils'

const Index = ({ store, operationColumns }) => {
  return (
    <>
      <Tabs
        defaultActiveKey="1"
        items={[
          {
            label: '项目端',
            key: '1',
            children: (
              <Table
        columnsFilter={'CreateDrawer_StampDuty_1'}
        onFilter={(key,val) => saveServer('CreateDrawer_StampDuty_1',val)}

                rowClassName={(record, rowIndex) => {
                  return !record?.isEffect ? 'table-row-remove' : ''
                }}
                extra={[
                  {
                    name: '导出',
                    type: 'primary',
                    onClick: () =>
                      store.exportExcel({
                        moduleType: 'STAMP_DUTY_PROJ',
                        ...store.stampDutyProjTable.getParams(),
                      }),
                  },
                ]}
                columns={[
                  ...getTableColumns(POJ_COLUMNS, POJ_COLUMNS, true),
                  operationColumns('ASSET_SIDE_COST_STAMP_DUTY'),
                ]}
                store={store.stampDutyProjTable}
                columnWidth={180}
              />
            ),
          },
          {
            label: '资金端',
            key: '2',
            children: (
              <Table
        columnsFilter={'CreateDrawer_StampDuty_2'}
        onFilter={(key,val) => saveServer('CreateDrawer_StampDuty_2',val)}

                extra={[
                  {
                    name: '导出',
                    type: 'primary',
                    onClick: () =>
                      store.exportExcel({
                        moduleType: 'STAMP_DUTY_FIN',
                        ...store.stampDutyFinTable.getParams(),
                      }),
                  },
                ]}
                columns={[
                  ...getTableColumns(FIN_COLUMNS, FIN_COLUMNS, true),
                  operationColumns('FINANCE_SIDE_COST_STAMP_DUTY'),
                ]}
                store={store.stampDutyFinTable}
                columnWidth={180}
              />
            ),
          },
        ]}
      ></Tabs>
    </>
  )
}

export default observer(Index)
