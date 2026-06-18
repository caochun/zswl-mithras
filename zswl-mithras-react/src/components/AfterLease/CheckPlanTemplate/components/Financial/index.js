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

function Financial({ id, canEdit, canImportFinancial, activeTag, projectId, businessVersion }) {
  const store = useMemo(() => {
    return new Store({ businessVersion })
  }, [businessVersion])
  const { active, orgTypeID } = store
  useEffect(() => {
    id && store.getCommerceDetail({ clientId: id, projectId, businessVersion }, canEdit, activeTag)
  }, [id, businessVersion, projectId, canEdit, activeTag])
  useEffect(() => {
    active && store.getSnopdata()
  }, [active, id, activeTag, businessVersion])

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

  const Content = useMemo(() => {
    const Comp = {
      CAPITAL_BALANCE: <Debt canEdit={canEdit} id={id} store={store} />,
      PROFIT: <Profit canEdit={canEdit} id={id} store={store} />,
      CASH_FLOW: <Cash canEdit={canEdit} id={id} store={store} />,
      BIZ_INDEX: <Biz canEdit={canEdit} id={id} store={store} />,
      GOV_CAPITAL_BALANCE: <Gov canEdit={canEdit} id={id} store={store} />,
      INCOME_EXPEND: <Income canEdit={canEdit} id={id} store={store} />,
    }
    return Comp[active] ?? null
  }, [active, id, canEdit])

  return (
    <div>
      <div className={styles.financialSheets}>
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
                  {/* <Radio.Button value="BIZ_INDEX">业务指标表</Radio.Button> */}
                </>
              )}
              {orgTypeID != 1 && orgTypeID != null && (
                <>
                  <Radio.Button value="GOV_CAPITAL_BALANCE">资产负债表</Radio.Button>
                  <Radio.Button value="INCOME_EXPEND">收入支出表</Radio.Button>
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
            {canImportFinancial && (
              <Upload {...propsUpload}>
                <Button type="primary">财报导入</Button>
              </Upload>
            )}
          </div>
        </div>
        {Content}
      </div>
    </div>
  )
}

export default observer(Financial)
