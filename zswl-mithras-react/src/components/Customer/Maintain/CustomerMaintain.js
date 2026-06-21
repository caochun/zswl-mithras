import { Table, Page, Form, App, SearchBar } from '@zswl/components'
import { observer } from '@zswl/admin'
import { userIsProjSponsor } from '@/utils'
import DataStore from './store'
import Create from './Create'
import { useEffect, useMemo } from 'react'
import styles from './index.less'
import HandoverModal from '../HandoverDetail/Modal'
import IconFont from '@/components/Icon'
import { PageListDownloadAction as PageListDown } from '@/components/Actions'
import ApplyPermissionModal from './ApplyPermissionModal'
import { MatchOptionColumn, InputColumn, AmountColumn } from '@/components/Format'
import { FounderSelect } from '@/components/Select'
import { transformClientStatus } from './utils'
import { ZInput } from '@/components/Form'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function CustomerMaintain() {
  const store = useMemo(() => new DataStore(), [])
  const { btnStatus } = store
  const { optionsType } = App.getData()

  useEffect(() => {
    store?.getButtonStatus()
  }, [])

  const columns = useMemo(() => {
    return [
      {
        title: '客户名称',
        dataIndex: 'clientName',
        width: 250,
        fixed: 'left',
        actions({ clientName, clientType, domesticOrAbroad, id }) {
          return [
            {
              name: clientName,
              onClick: () =>
                store.toDetail({
                  id,
                  clientType,
                  domesticOrAbroad,
                  flag: 'info',
                  typeId: 'create',
                }),
            },
          ]
        },
      },
      AmountColumn({
        title: '授信金额(万元)',
        dataIndex: 'applyCreditAmount',
        width: 160,
      }),
      AmountColumn({
        title: '剩余本金(万元)',
        dataIndex: 'lastPrincipal',
        width: 130,
      }),
      AmountColumn({
        title: '存量风险敞口(万元)',
        dataIndex: 'stockRiskExposure',
        width: 180,
      }),
      InputColumn({
        title: '所属主办',
        dataIndex: 'belongSponsorName',
        width: 130,
      }),
      InputColumn({
        title: '所属部门',
        width: 140,
        dataIndex: 'belongDeptName',
      }),
      InputColumn({
        title: '客户状态',
        dataIndex: 'clientStatus',
        width: 90,
        render: (value, record) => {
          const netClientStatus = transformClientStatus({
            clientStatus: value,
            isReleased: record.isReleased,
          })
          return App.matchOption(optionsType.clientStatus, netClientStatus).label || '-'
        },
      }),
      {
        title: '审批状态',
        dataIndex: 'processStatusName',
        width: 180,
      },
      InputColumn({
        title: '项目经理权限',
        dataIndex: 'maxAuthority',
        width: 130,
      }),
      InputColumn({
        title: '创建时间',
        dataIndex: 'createTime',
        width: 180,
      }),
      InputColumn({
        title: '更新时间',
        width: 180,
        dataIndex: 'updateTime',
      }),
      InputColumn({
        title: '创建人',
        dataIndex: 'creatorName',
        width: 130,
      }),
      {
        title: '操作',
        width: 80,
        fixed: 'right',
        actions(record) {
          return [
            {
              name: '删除',
              onClick: () => store.remove(record),
              disabled: !record.canDelete,
            },
          ]
        },
      },
    ]
  }, [])
  return (
    <Page>
      <div className={styles.customerWrap}>
        <Table
                columnsFilter={'customer_maintain_1'}
                onFilter={(key,val) => saveServer('customer_maintain_1',val)}
          resizable
          store={store.table}
          searchbar={{
            labelCol: { span: 6 },
            items: [
              // {
              //   label: '客户名称',
              //   name: 'clientName',
              //   placeholder: '请输入',
              // },
              <Item label="客户名称" name="clientName">
                <ZInput placeholder="请输入"></ZInput>
              </Item>,
              {
                label: '客户分类',
                name: 'clientType',
                options: 'clientType',
                allowClear: true,
                showSearch: false,
              },

              { label: '创建日期', name: 'createDate', type: 'rangePicker', allowClear: true },
              { label: '更新日期', name: 'updateDate', type: 'rangePicker', allowClear: true },
              {
                label: '客户状态',
                name: 'clientStatus',
                options: 'clientStatus',
                allowClear: true,
              },
              {
                label: '流程状态',
                name: 'processStatus',
                options: 'clientProcessStatus',
                allowClear: true,
              },
              <Item label="创建人" name="createBy" key="createBy">
                <FounderSelect
                  functionCode="selectfounder"
                  params={{ job: undefined }}
                ></FounderSelect>
              </Item>,
            ],
          }}
          extra={[<PageListDown key="1" module="customer" table={store.table} />]}
          actions={[
            {
              name: (
                <span>
                  <IconFont type="icon-icon_add" />
                  新增客户
                </span>
              ),
              onClick: store.createModal.open,
              type: 'primary',
            },
            { name: '客户移交', onClick: store.withdraw, disabled: !btnStatus.canTransferClient },
            {
              name: '申办权限申请',
              onClick: store.applyPermissionModal.open,
            },
          ]}
          scroll={{
            x: 1500,
          }}
          columnWidth={180}
          columns={columns}
        />
        <Create store={store} />
        <HandoverModal store={store} />
        <ApplyPermissionModal store={store} />
      </div>
    </Page>
  )
}

export default observer(CustomerMaintain)
