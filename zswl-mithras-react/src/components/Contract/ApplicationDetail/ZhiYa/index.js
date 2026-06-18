import { useMemo, useEffect } from 'react'
import { Button, Table, App } from '@zswl/components'
import { Tooltip, Space } from 'antd'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal'
import JumpClient from '@/components/JumpClient'
import RenderColumn from '@/components/RenderColumn'
import Store from './store'
import styles from './index.less'
import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import { saveServer } from '@/utils'

const Index = ({ baseStore, canEditFlag }) => {
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()
  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, contractId, baseStore })
  }, [businessVersion, isFormApproval])
  baseStore.zhiYaStore = store

  const columns = useMemo(() => {
    return [
      {
        title: '质押合同编号',
        dataIndex: 'pledgeContractCode',
        width: 300,
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '质押类型',
        dataIndex: 'contractPledgeType',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="pledgeTypeEnum"
            ></RenderColumn>
          )
        },
      },
      {
        title: '质押物清单',
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
        title: '出质人类型',
        dataIndex: 'pledgeType',
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
        title: '出质人名称',
        dataIndex: 'pledgeInfo',
        render(val, t) {
          if (isFormApproval) {
            return (
              <JumpClient
                value={t.pledgeInfo?.value}
                isChange={t.pledgeInfo?.isChange}
              ></JumpClient>
            )
          }
          return <JumpClient value={val}> </JumpClient>
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
        title: '质押物描述',
        dataIndex: 'pledgeDescribe',
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
        width: 150,
        fixed: 'right',
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
        <div className={styles.title}>质押措施</div>
        <Space>
          <Button onClick={store.generateContractNo} disabled={!canEditFlag}>
            生成合同编号
          </Button>
          <DownloadTemplate
            disabled={isFormApproval}
            params={{
              templateName: 'TEMPLATE_OSS_NAME_PLEDGE_ITEM',
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
        scroll={{ x: 1500 }}
        store={store.$table}
        columns={columns}
        autoRequest={false}
        resizable
        columnsFilter="contract_zhiya"
        onFilter={(key, val) => saveServer('contract_zhiya', val)}
      ></Table>
      <CreateModal store={store}></CreateModal>
    </div>
  )
}

export default observer(Index)
