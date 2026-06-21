import { RenderColumn } from '@/components/Format'
import { useMemo, useEffect } from 'react'
import { Button, Table, App } from '@zswl/components'
import { Tooltip, Space } from 'antd'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal/ContractMortgageCreateModal'
import Store from './store'
import styles from './index.less'
import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import { saveServer } from '@/utils'
import JumpClient from '../../JumpClient'

const ContractApplicationMortgage = ({ baseStore, canEditFlag }) => {
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()

  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, contractId, baseStore })
  }, [businessVersion, isFormApproval, contractId])
  baseStore.diYaStore = store

  const columns = useMemo(() => {
    return [
      {
        title: '抵押合同编号',
        dataIndex: 'mortgageContractCode',
        width: 300,
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '抵押类型',
        dataIndex: 'contractMortgageType',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="mortgageTypeEnum"
            ></RenderColumn>
          )
        },
      },
      {
        title: '抵押物清单',
        dataIndex: 'fileName',
        render(val, record) {
          if (isFormApproval) {
            if (!val.value) {
              return '-'
            }
            return (
              <Tooltip title={val.value} placement="topLeft">
                <a type="link" onClick={() => store.preview({ id: record.fileId.value })}>
                  {val.value}
                </a>
              </Tooltip>
            )
          } else {
            return (
              <>
                {val ? (
                  <Tooltip title={val} placement="topLeft">
                    <a type="link" onClick={() => store.preview({ id: record.fileId })}>
                      {val}
                    </a>
                  </Tooltip>
                ) : (
                  '-'
                )}
              </>
            )
          }
        },
      },
      {
        title: '抵押物类型',
        dataIndex: 'mortgageItemType',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="mortgageItemTypeEnum"
            ></RenderColumn>
          )
        },
      },
      {
        title: '抵押人类型',
        dataIndex: 'mortgageType',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="clientType"
            ></RenderColumn>
          )
        },
      },
      {
        title: '抵押人名称',
        dataIndex: 'mortgageInfo',
        render(val, t) {
          if (isFormApproval) {
            return (
              <JumpClient
                value={t.mortgageInfo?.value}
                isChange={t.mortgageInfo.isChange}
              ></JumpClient>
            )
          } else {
            return <JumpClient value={val}></JumpClient>
          }
        },
      },
      {
        title: '关联合同编号',
        dataIndex: 'relatContracts',
        width: 300,
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              formatText={(v) => {
                return v?.join(',')
              }}
            ></RenderColumn>
          )
        },
      },
      {
        title: '抵押物描述',
        dataIndex: 'mortgageDescribe',
        width: 180,
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '是否评估',
        dataIndex: 'assess',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="isConfirm"
            ></RenderColumn>
          )
        },
      },
      {
        title: '评估公司',
        dataIndex: 'appraisalCompany',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '评估日期',
        dataIndex: 'assessDate',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '评估编号',
        dataIndex: 'appraisalCode',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '是否最高额',
        dataIndex: 'highest',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="isConfirm"
            ></RenderColumn>
          )
        },
      },
      {
        title: '操作',
        dataIndex: 'id',
        fixed: 'right',
        width: 150,
        actions(record) {
          return [
            {
              name: '查看',
              onClick: () => store.$createModal.open({ ...record, isDetail: true }),
            },
            {
              name: '编辑',
              onClick: () => store.$createModal.open(record),
              disabled: !canEditFlag,
            },
            { name: '删除', onClick: () => store.remove(record), disabled: !canEditFlag },
          ]
        },
      },
    ]
  }, [canEditFlag])

  useEffect(() => {
    if (contractId) {
      store.$table.search({ contractId })
    }
  }, [contractId])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>抵押措施</div>
        <Space>
          <Button onClick={store.generateContractNo} disabled={!canEditFlag}>
            生成合同编号
          </Button>
          <DownloadTemplate
            params={{
              templateName: 'TEMPLATE_OSS_NAME_MORTGAGE_ITEM',
              moduleType: 'CONTRACT',
            }}
          />
          <Button
            onClick={store.$createModal.open}
            type="primary"
            icon={<IconFont type="icon-icon_add" />}
            disabled={!canEditFlag}
          >
            新增
          </Button>
        </Space>
      </div>
      <Table
        scroll={{ x: 2400 }}
        store={store.$table}
        columns={columns}
        autoRequest={false}
        resizable
        columnsFilter="contract_diya"
                onFilter={(key,val) => saveServer('contract_diya',val)}

      ></Table>
      <CreateModal store={store}></CreateModal>
    </div>
  )
}

export default observer(ContractApplicationMortgage)
