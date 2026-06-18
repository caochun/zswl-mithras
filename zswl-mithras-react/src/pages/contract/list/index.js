import { useMemo, useEffect, useState } from 'react'
import { Table, App, Page, Form, Access, SearchBar } from '@zswl/components'
import IconFont from '@/components/Icon'
import { getQuery, observer } from '@zswl/admin'
import { amountFormat, getKeyOptionsLabelMapPlus, formatPercent, hasValue } from '@/utils'
import store from './store'
import styles from './index.less'
import { saveServer, getUserInfo, isProjmanager } from '@/utils'
import { isTeamleader } from '@/utils/auth'
import CreateModal from './CreateModal'
import ChangeModal from './ChangeModal'
import { PageListDown } from '@/components'
import { ClientSelect, OrgSelect, FounderSelect } from '@/components/Select'
import { Button, Modal } from 'antd'

const { Item } = SearchBar

function Index() {
  const [show, setShow] = useState(false)
  const { optionsType } = App.getData()
  const userInfo = getUserInfo()
  const { rows, keys } = store.$table.getSelected()
  const CONTRACT_STATUS = rows[0]?.contractStatus || ''
  const CONTRACT_PROCESS_STATUS = rows[0]?.contractProcessStatus || ''
  const PROJ_SPONSOR_USERID = rows[0]?.projSponsorUserId || ''
  const openModal = getQuery('openModal')
  const contractStatus = getQuery('contractStatus')

  useEffect(() => {
    openModal === 'true' && store.createModal.open()
  }, [openModal])

  useEffect(() => {
    if (contractStatus) {
      store.$table.setParams({
        contractStatus,
      })
      store.$table.search()
    }
  }, [contractStatus])
  const columns = useMemo(() => {
    return [
      {
        title: '合同编号',
        width: 280,
        fixed: 'left',
        dataIndex: 'contractCode',
        actions({ contractCode, id }) {
          return [
            {
              name: contractCode,
              to: `/contract/list/detail/${id}`,
            },
          ]
        },
      },
      {
        title: '项目名称',
        dataIndex: 'projName',
        width: 300,
      },
      {
        title: '业务类型',
        width: 100,
        dataIndex: 'bizType',
        render: (item) => {
          return getKeyOptionsLabelMapPlus('projEstablishBizType')[item]
        },
      },
      {
        title: '合同金额(元)',
        dataIndex: 'applyCreditAmount',
        width: 140,
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
        },
      },
      {
        title: '业务部门',
        dataIndex: 'bizDeptName',
        width: 140,
      },
      {
        title: '项目主办',
        dataIndex: 'projSponsorUserName',
        width: 130,
      },
      {
        title: '项目协办',
        width: 140,
        dataIndex: 'projCosponsorUserNames',
        render: (val) => {
          return val?.join(',') || '-'
        },
      },
      {
        title: '客户名称',
        width: 300,
        dataIndex: 'clientName',
      },
      {
        title: '合同状态',
        dataIndex: 'contractStatus',
        width: 100,
        render: (val) => {
          return val ? getKeyOptionsLabelMapPlus('contractStatus')[val] : '-'
        },
      },
      {
        title: '合同流状态',
        dataIndex: 'contractProcessName',
        width: 180,
      },
      {
        title: '创建时间',
        width: 180,
        dataIndex: 'createTime',
      },
      {
        title: '更新时间',
        width: 180,
        dataIndex: 'updateTime',
      },
      {
        title: '操作',
        width: 80,
        fixed: 'right',
        isAction: true,
        actions(record) {
          return [
            Access.validate('contractcancel') && {
              name: '作废',
              onClick: () => store.remove(record),
              disabled: record.contractStatus !== 'NEW',
            },
          ].filter(Boolean)
        },
      },
    ]
  }, [])
  console.log('columns',isTeamleader(), PROJ_SPONSOR_USERID , userInfo.id)
  return (
    <Page store={store}>
      <div className={styles.customerWrap}>
        <Table
      columnsFilter="contract_list_1"
      onFilter={(key,val) => saveServer('contract_list_1',val)}

          resizable
          store={store.$table}
          selectable={{
            type: 'radio',
          }}
          searchbar={{
            // initialValues: { contractStatus },
            labelCol: { span: 6 },
            items: [
              {
                label: '合同编号',
                name: 'contractCode',
              },
              {
                label: '项目名称',
                name: 'projName',
              },
              {
                label: '业务类型',
                name: 'bizType',
                options: optionsType.projEstablishBizType || [],
                allowClear: true,
              },
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-2"></ClientSelect>
              </Item>,
              <Item label="业务部门" name="bizDeptId" key="bizDeptId">
                <OrgSelect functionCode="selectorgs-1"></OrgSelect>
              </Item>,
              <Item label="项目主办" name="projSponsorUserId" key="projSponsorUserId">
                <FounderSelect functionCode="selectfounder-1"></FounderSelect>
              </Item>,
              <Item label="项目协办" name="projCosponsorUserId" key="projCosponsorUserId">
                <FounderSelect functionCode="selectfounder-1"></FounderSelect>
              </Item>,
              { label: '创建时间', name: 'createDate', type: 'rangePicker' },
              { label: '更新时间', name: 'updateDate', type: 'rangePicker' },
              {
                label: '合同状态',
                name: 'contractStatus',
                options: optionsType.contractStatus,
              },
            ],
          }}
          extra={[<PageListDown key="1" module="contract" table={store.$table} />]}
          actions={[
            {
              name: (
                <span>
                  <IconFont type="icon-icon_add" />
                  创建合同
                </span>
              ),
              access: 'contractbaseinfoadd',
              onClick: store.createModal.open,
              type: 'primary',
            },
            {
              name: '合同起租',
              onClick: store.onContractStartRent,
              access: 'contractflowstartsubmit',
              disabled:
                (CONTRACT_STATUS && CONTRACT_STATUS !== 'TAKE_EFFECT') ||
                CONTRACT_PROCESS_STATUS === 'SETTLE_PASS', // 合同生效时
            },
            {
              name: '新增投放',
              onClick: store.onCreateReceipt,
              access: 'contractreceiptadd',
              disabled:
                (CONTRACT_STATUS && !['START_RENT', 'TAKE_EFFECT'].includes(CONTRACT_STATUS)) ||
                CONTRACT_PROCESS_STATUS === 'SETTLE_PASS', // 合同生效、起租
            },
            {
              name: '合同变更',
              access: 'contractflowchangesubmit',
              onClick: store.onContractChange,
              disabled:
                (CONTRACT_STATUS && !['START_RENT', 'TAKE_EFFECT'].includes(CONTRACT_STATUS)) ||
                CONTRACT_PROCESS_STATUS === 'SETTLE_PASS', // 合同生效、起租
            },
            {
              name: '提前还款',
              access: 'contractflowchangesubmit',
              onClick: store.onContractPrepayment,
              disabled:
                (CONTRACT_STATUS && !['START_RENT', 'TAKE_EFFECT'].includes(CONTRACT_STATUS)) ||
                CONTRACT_PROCESS_STATUS === 'SETTLE_PASS', // 合同生效、起租
            },
            {
              name: '合同结清',
              onClick: store.onContractSettlement,
              access: 'contractflowsettlesubmit',
              disabled:
                (CONTRACT_STATUS && CONTRACT_STATUS !== 'START_RENT') ||
                CONTRACT_PROCESS_STATUS === 'SETTLE_PASS', // 合同起租时
            },
            {
              name: '保证金退抵',
              onClick: store.onMarginRefund,
              access: 'contractdepostcheck',
              disabled: !(isTeamleader() || PROJ_SPONSOR_USERID === userInfo.id) && ["SETTLE","START_RENT","TAKE_EFFECT"].includes(CONTRACT_STATUS)
            },
          ].filter(Boolean)}
          scroll={{
            x: 1300,
          }}
          columns={columns}
        />
        <CreateModal />
        <ChangeModal />
      </div>
    </Page>
  )
}

export default observer(Index)
