import React, { useEffect, useMemo } from 'react'
import { Radio, Upload } from 'antd'
import { Button } from '@zswl/components'
import Debt from './Debt'
import Profit from './Profit'
import Cash from './Cash'
import Biz from './Biz'
import Gov from './Gov'
import Income from './Income'
import Store from './store'
import { observer } from '@zswl/admin'
import styles from './index.less'

function Financial({ id, commonExtra, canEditFlag }) {
  const store = useMemo(() => new Store({ id, canEditFlag }), [id, canEditFlag])
  const { active, orgTypeID } = store
  useEffect(() => {
    store.getCommerceDetail({ clientId: id })
  }, [])
  const propsUpload = {
    name: 'file',
    multiple: false,
    beforeUpload(info) {
      let formdata = new FormData()
      formdata.append('excelFile', info)
      formdata.append('clientId', id)
      store.uploadExcel(formdata, active)
      return false
    },
  }

  return (
    <div>
      <div className={styles.financialSheets}>
        <div className={styles.operation}>{commonExtra}</div>
        <div className={styles.financialWrap}>
          <div className={styles.sheets}>
            <Radio.Group
              style={{ marginBottom: 16 }}
              onChange={(e) => {
                store.setActive(e.target.value)
              }}
              value={active}
            >
              {(orgTypeID == 1 || orgTypeID == null) && (
                <>
                  <Radio.Button value="CAPITAL_BALANCE">资产负债表</Radio.Button>
                  <Radio.Button value="PROFIT">利润表</Radio.Button>
                  <Radio.Button value="CASH_FLOW">现金流量表</Radio.Button>
                  <Radio.Button value="BIZ_INDEX">业务指标表</Radio.Button>
                </>
              )}
              {orgTypeID != 1 && orgTypeID != null && (
                <>
                  <Radio.Button value="GOV_CAPITAL_BALANCE">资产负债表</Radio.Button>
                  <Radio.Button value="PROFIT">利润表</Radio.Button>
                  <Radio.Button value="CASH_FLOW">现金流量表</Radio.Button>
                </>
              )}
            </Radio.Group>
          </div>
          <div className={styles.operationBtn}>
            <Button
              style={{
                // width: 100,
                marginRight: 10,
              }}
              onClick={store.download}
              items={[
                { name: '企业法人', key: '1' },
                { name: '事业单位', key: '2' },
              ]}
            >
              模版下载
            </Button>
            {canEditFlag && (
              <Upload {...propsUpload}>
                <Button type="primary">财报导入</Button>
              </Upload>
            )}
          </div>
        </div>
        {
          {
            CAPITAL_BALANCE: <Debt store={store} />,
            PROFIT: <Profit store={store} />,
            CASH_FLOW: <Cash store={store} />,
            BIZ_INDEX: <Biz store={store} />,
            GOV_CAPITAL_BALANCE: <Gov store={store} />,
            INCOME_EXPEND: <Income store={store} />,
          }[active]
        }
      </div>
    </div>
  )
}

export default observer(Financial)
