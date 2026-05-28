import styles from '../index.less'
import { Button, Dropdown, Input, InputNumber, Menu, message } from 'antd'
import { useEffect, useMemo } from 'react'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CashFlowStatementTable from './Table'
import DataUpload from '@/components/DataUpload'
import { PayCircleOutlined } from '@ant-design/icons'
import Store from './store'

const CashFlowStatement = ({ id, canEdit = true, isProjSponsor, businessVersion, rootStore }) => {
  const store = useMemo(() => new Store({ rootStore }), [rootStore])
  store.projPricingId = id

  useEffect(() => {
    if (id) {
      store.table.search({ projPricingId: id })
    }
  }, [id])

  const menu = (
    <Menu
      items={[
        {
          key: '1',
          label: (
            <div
              onClick={() => {
                store.exportRent(id, '租金表.xlsx', businessVersion)
              }}
            >
              导出租金表
            </div>
          ),
        },
        {
          key: '2',
          label: (
            <div
              onClick={() => {
                store.exportCashFlow(id, '现金流量表.xlsx', businessVersion)
              }}
            >
              导出现金流量表
            </div>
          ),
        },
      ]}
    />
  )
  const auth = isProjSponsor
  return (
    <>
      <div className={styles.titleWrap}>
        <div className={'z-sub-title'}>
          现金流计划表
          {canEdit && (
            <Button icon={<PayCircleOutlined />} style={{ marginLeft: 8 }} onClick={store.generate}>
              生成现金流
            </Button>
          )}
        </div>

        <div className={styles.btnWrap}>
          {canEdit && (
            <>
              <Button
                // loading={store.reportLoading}
                disabled={!isProjSponsor}
                onClick={() => {
                  store.download(id, '现金流计划表.xlsx')
                }}
                style={{ marginRight: 8 }}
                type="primary"
              >
                <IconFont type="icon-icon_download" />
                下载模板
              </Button>
              {/* <Button
            loading={store.reportLoading}
            type="primary"
            style={{ marginRight: 8 }}
            onClick={() => store.generate(id)}
          >
            自动生成
          </Button> */}
              <DataUpload
                accept=".xlsx"
                maxCount={1}
                onChange={(file) => store.onFileChange(file, id)}
              >
                <Button
                  type="primary"
                  disabled={!isProjSponsor || !rootStore.QSShowValue}
                  style={{ marginRight: 8 }}
                >
                  数据导入
                </Button>
              </DataUpload>
            </>
          )}
          <Dropdown overlay={menu} placement="bottomLeft">
            <Button>导出</Button>
          </Dropdown>
        </div>
      </div>
      <CashFlowStatementTable tableStore={store.table} />
    </>
  )
}

export default observer(CashFlowStatement)
