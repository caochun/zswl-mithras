import { useEffect, useMemo } from 'react'
import { Table, Button, Form, App } from '@zswl/components'
import { DownOutlined, PayCircleOutlined } from '@ant-design/icons'
import { observer } from '@zswl/admin'
import { Dropdown, Space, Menu, DatePicker, message } from 'antd'
import moment from 'moment'
import CreateModal from './CreateModal'
import RenderColumn from '@/components/RenderColumn'
import { bizTypeMapText } from '@/pages/contract/list/bizType.config'
import Store from './store'
import styles from './index.less'
import GenerateModal from './GenerateModal'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'
import { saveServer } from '@/utils'

const { Item } = Form

function Index({ canEditFlag = true, formChangeOther, contractStatus, baseStore }) {
  const { bizType, baoJiaShowValue } = baseStore
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()
  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, contractId, bizType, baoJiaShowValue })
  }, [businessVersion, isFormApproval, contractId, bizType, baoJiaShowValue])

  const { planStartDate } = store
  // 只有 合同创建、变更其它类型才能导入
  store.scene = formChangeOther === 'true' ? 'CHANGE_OTHER' : 'CREATE'
  const showBtnAsSence = formChangeOther === 'true' || contractStatus === 'NEW'

  useEffect(() => {
    if (contractId) {
      store.$table.search()
    }
  }, [contractId])

  const menu = (
    <Menu
      onClick={store.handleMenuClick}
      items={[
        {
          label: `导出${bizTypeMapText[bizType]?.rentTitle}`,
          key: '1',
          disabled: store.$table.getList().length === 0,
        },
        {
          label: '导出现金流表',
          key: '2',
          disabled: store.$table.getList().length === 0,
        },
      ]}
    />
  )

  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  const templateName = useMemo(() => {
    return ['BL', 'ZR'].includes(bizType)
      ? 'TEMPLATE_OSS_NAME_ESTIMATE_PAYMENT_ITEM'
      : 'TEMPLATE_OSS_NAME_ESTIMATE_RENT_ITEM'
  }, [bizType])
  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>
          {`概算${bizTypeMapText[bizType]?.rentTitle}`}
          {canEditFlag && (
            <Button icon={<PayCircleOutlined />} style={{ marginLeft: 8 }} onClick={store.generate}>
              生成现金流
            </Button>
          )}
        </div>
        <div className={styles.rightWrap}>
          <Space>
            {planStartDate && (
              <Item label={'计划起租日'}>
                <DatePicker
                  placeholder={'请选择'}
                  style={{ width: 200 }}
                  disabled
                  value={moment(planStartDate)}
                />
              </Item>
            )}
            <Item>
              <Space>
                <DownloadTemplate
                  params={{
                    templateName,
                    moduleType: 'CONTRACT',
                  }}
                />
                {showBtnAsSence && (
                  <Button
                    onClick={() => {
                      // if (!baoJiaShowValue) {
                      //   message.error('请先填写报价信息')
                      //   return false
                      // }
                      store.$createModal.open({
                        planStartDate,
                      })
                    }}
                    disabled={!canEditFlag}
                  >
                    {`导入${bizTypeMapText[bizType]?.rentTitle}`}
                  </Button>
                )}

                <Dropdown overlay={menu}>
                  <Button>
                    导出
                    <DownOutlined />
                  </Button>
                </Dropdown>
              </Space>
            </Item>
          </Space>
        </div>
      </div>
      <Table
              columnsFilter={'detail_GaiSuanZuJin_1'}
              onFilter={(key,val) => saveServer('detail_GaiSuanZuJin_1',val)}
        rowKey={(record) => {
          return record.id?.value ?? record.id
        }}
        // autoRequest={false}
        store={store.$table}
        columns={[
          {
            title: '日期',
            dataIndex: 'date',
            width: 150,
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
            },
          },
          {
            title: '期项',
            width: 100,
            dataIndex: 'phase',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
            },
          },
          {
            title: `${bizTypeMapText[bizType]?.rentText}(元)`,
            dataIndex: 'rent',
            align: 'right',
            width: 160,
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
          {
            title: '本金(元)',
            dataIndex: 'principal',
            align: 'right',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
          {
            title: '利息(元)',
            dataIndex: 'interest',
            align: 'right',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
          {
            title: '剩余本金(元)',
            dataIndex: 'remainingPrincipal',
            align: 'right',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
        ]}
      />
      <CreateModal store={store} />
      <GenerateModal store={store.generateModal} />
    </div>
  )
}

export default observer(Index)
