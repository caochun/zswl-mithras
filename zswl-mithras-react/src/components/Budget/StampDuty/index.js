import { ClientSelect, OrgSelect } from '@/components/Select'
import { ImportAction } from '@/components/Actions'
import IconFont from '@/components/Icon'
import { OrgListSelect } from '@/components/Financial/SelectEntries'
import { amountFormat, formatPercent, hasValue, saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import { Page, SearchBar, Table } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import Create from './Create'
import styles from './index.less'
import DataStore from './store'

const { Item } = SearchBar

function Index() {
  const store = useMemo(() => new DataStore(), [])
  const [open, setOpen] = useState(false)
  const [modalK, setModalK] = useState('000')
  useEffect(() => {
    if (open) {
      setModalK(Math.random() + '_')
    }
  }, [open])

  const columns = useMemo(() => {
    return [
      {
        title: '申报税目名称',
        dataIndex: 'name',
        width: 150,
        fixed: 'left',
        matchOption: 'stampDutyBizTypeEnum',
      },
      {
        title: '业务部门',
        dataIndex: 'belongOrgName',
        width: 160,
      },
      {
        title: '客户名称/融资机构',
        dataIndex: 'clientName',
        width: 280,
      },
      {
        title: '合同编号/融资编号',
        dataIndex: 'belongCode',
        width: 260,
      },
      {
        title: '借据编号',
        width: 140,
        dataIndex: 'receiptCode',
      },
      {
        title: '实际起租日',
        dataIndex: 'startDate',
        width: 130,
      },
      {
        title: '不含税租金',
        dataIndex: 'rent',
        width: 160,
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
        },
      },
      {
        title: '不含税手续费',
        dataIndex: 'commission',
        width: 120,
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
        },
      },
      {
        title: '不含税咨询费',
        width: 130,
        dataIndex: 'consultingFee',
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
        },
      },
      {
        title: '金额',
        dataIndex: 'amount',
        width: 160,
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
        },
      },
      {
        title: '印花税率(%)',
        dataIndex: 'taxRate',
        width: 120,
        align: 'right',
      },
      {
        title: '印花税',
        dataIndex: 'stampDuty',
        width: 160,
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val), 2, false) : '-'
        },
      },
    ]
  }, [])
  const { rows } = store.table.getSelected()
  return (
    <Page>
      <div className={styles.customerWrap}>
        <Table
          columnsFilter={'budget_stamp_1'}
          onFilter={(key, val) => saveServer('budget_stamp_1', val)}
          resizable
          store={store.table}
          searchbar={{
            labelCol: { span: 7 },
            items: [
              {
                label: '申报税目名称',
                name: 'name',
                options: 'stampDutyBizTypeEnum',
                allowClear: true,
                showSearch: false,
              },
              <Item label="业务部门" name="belongOrgName" key="belongOrgName">
                <OrgSelect functionCode="stampDutyOrg" url="/stampDuty/orgs"></OrgSelect>
              </Item>,
              <Item label="客户名称" name="clientName" key="clientName">
                <ClientSelect canJump={false} functionCode="clientlist-1"></ClientSelect>
              </Item>,
              <Item label="融资机构" name="organizationName" key="organizationName">
                <OrgListSelect />
              </Item>,
              {
                label: '合同编号',
                name: 'contractCode',
                allowClear: true,
              },
              {
                label: '融资编号',
                name: 'financingCode',
                allowClear: true,
              },
              {
                label: '借据编号',
                name: 'receiptCode',
                allowClear: true,
              },
              {
                label: '实际起租日',
                name: 'createDate',
                type: 'rangePicker',
                allowClear: true,
              },
            ],
          }}
          selectable={true}
          actions={[
            {
              name: (
                <span>
                  <IconFont type="icon-icon_add" />
                  新增
                </span>
              ),
              onClick: () => setOpen(true),
              type: 'primary',
            },
            {
              name: '模板下载',
              onClick: store.downloadTemplate,
            },
            <ImportAction
              type="default"
              btnText="导入"
              upload={store.upload}
              beforeUpload={store.beforeUpload}
              mode="upload"
              accept="	.xlsx,.xls"
            />,
            {
              name: '删除',
              disabled: rows.length === 0,
              onClick: store.deleteRows,
            },
            {
              name: '批量导出',
              onClick: store.downloadBatch,
            },
          ]}
          scroll={{
            x: 1500,
          }}
          columnWidth={180}
          columns={columns}
        />
        <Create key={modalK} open={open} setOpen={setOpen} refresh={store.refresh} />
      </div>
    </Page>
  )
}

export default observer(Index)
