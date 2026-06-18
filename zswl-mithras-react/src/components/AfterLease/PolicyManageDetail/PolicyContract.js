import { observer } from '@zswl/admin'
import { Table, TableStore } from '@zswl/components'
import { PolicyColumns as ALL_COLUMNS } from '@/components/PolicyColumns/PolicyColumnsEntries'
import { getTableColumns } from '@/utils'
import IconFont from '@/components/Icon'
import { message, Space } from 'antd'
import Api from '@/api/afterLease/policyLedgerApi'
import { useEffect, useState, useMemo } from 'react'
import { saveServer } from '@/utils'

const nameColumns = [
  '保险单号',
  '保险机构',
  '险种',
  '保单金额(元)',
  '保险起始日',
  '保险到期日',
  '是否续保',
  '标识信息',
  '备注',
  '创建人',
  '创建时间',
]

const titleStyle = {
  color: 'rgba(0, 0, 0, 0.85)',
  fontWeight: 'bold',
  fontSize: '16px',
}

const Index = ({ detail: policyDetail }) => {
  const [expandKeys, setExpandKeys] = useState([])

  const columns = getTableColumns(ALL_COLUMNS, nameColumns)

  const getId = (id) => {
    return id?.children ?? id
  }
  const $table = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          if (policyDetail.contractId) {
            const data = await Api.postContractPolicy({
              ...params,
              contractId: policyDetail.contractId,
            })

            setExpandKeys(data?.map((item) => item.id))
            return data
          }
          return []
        },
      }),
    [policyDetail]
  )
  const batchDown = async () => {
    const { keys } = $table.getSelected()
    if ($table.getList().length === 0) {
      message.info('列表为空')
      return
    }
    await Api.postPolicyExport({
      policyIds: keys,
      contractId: policyDetail.contractId,
    })
  }
  return (
    <div>
      <Table
        columnsFilter="afterlease_policyManage_PolicyContract"
        onFilter={(key, val) => saveServer('afterlease_policyManage_PolicyContract', val)}
        actions={<div style={titleStyle}>合同保单</div>}
        editable={false}
        store={$table}
        selectable
        columns={columns}
        columnWidth={180}
        expandable={{
          expandedRowKeys: expandKeys,
          onExpand: (expanded, record) => {
            console.log('expanded: ', expanded)
            if (expanded) {
              setExpandKeys([...expandKeys, getId(record.id)])
            } else {
              setExpandKeys(expandKeys.filter((item) => item !== record.id))
            }
          },
        }}
        scroll={{
          x: 1500,
        }}
        extra={[
          {
            name: (
              <Space>
                <IconFont type="icon-icon_download" />
                批量下载
              </Space>
            ),
            type: 'primary',
            onClick: batchDown,
          },
        ].filter(Boolean)}
      ></Table>
    </div>
  )
}

export default observer(Index)
