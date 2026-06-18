import { Tooltip, Tabs } from 'antd'
import { Modal } from '@zswl/components'
import classnames from 'classnames'
import { observer } from '@zswl/admin'
import { LifeCycleRiskStrategyCard as Card } from '@/components/LifeCycle/LifeCycleEntries'
import { RiskPublicMonitor as PublicMonitor } from '@/components/Risk/PublicMonitorListEntries'
import styles from './index.less'
import cardStyles from './Card/index.less'
import { useMemo } from 'react'

const Index = ({ store }) => {
  const { clientInfo, addressList } = store
  const getAddress = useMemo(() => {
    const result = addressList?.map(({ provinceName, cityName, districtName }) => {
      return `${provinceName || ''}${cityName || ''}${districtName || ''}`
    })
    return result?.filter(Boolean)?.join('、') || '-'
  }, [addressList])
  return (
    <Modal
      title={'风险策略助手'}
      store={store.$riskStrategy}
      okText={'确定'}
      destroyOnClose
      width={1100}
      footer={null}
    >
      <div className={styles.wrap}>
        <div className={styles.wrap_header}>
          <div className={styles.wrap_header_l}>
            <div className={styles.wrap_header_l_img}>
              <img src={'/public/assets/image/risk.png'} />
            </div>
            <div className={styles.wrap_header_l_info}>
              <div className={styles.wrap_header_l_info_title}>{clientInfo?.clientName}</div>
              <div className={styles.wrap_header_l_info_subTitle}>{clientInfo?.corpCertCode}</div>
              <div className={styles.wrap_header_l_info_row}>
                <div className={styles.wrap_header_l_info_row_info}>
                  <span className={styles.wrap_header_l_info_row_info_label}>法定代表人：</span>
                  <span className={styles.wrap_header_l_info_row_info_value}>
                    <Tooltip title={clientInfo?.corpRepresent}>{clientInfo?.corpRepresent}</Tooltip>
                  </span>
                </div>
                <div className={styles.wrap_header_l_info_row_info}>
                  <span
                    className={classnames(
                      styles.wrap_header_l_info_row_info_label,
                      styles.wrap_header_l_info_row_info_stylelabel
                    )}
                  >
                    所属集团：
                  </span>
                  <span
                    className={classnames(
                      styles.wrap_header_l_info_row_info_value,
                      styles.wrap_header_l_info_row_info_stylevalue
                    )}
                  >
                    <Tooltip title={clientInfo?.belongGroupClientName}>
                      {clientInfo?.belongGroupClientName || '-'}
                    </Tooltip>
                  </span>
                </div>
              </div>
              <div className={styles.wrap_header_l_info_row}>
                <div className={styles.wrap_header_l_info_row_info}>
                  <span className={styles.wrap_header_l_info_row_info_label}>是否关联方：</span>
                  <span className={styles.wrap_header_l_info_row_info_value}>
                    {['否', '是'][clientInfo?.isRelated] || '-'}
                  </span>
                </div>
                <div className={styles.wrap_header_l_info_row_info}>
                  <span
                    className={classnames(
                      styles.wrap_header_l_info_row_info_label,
                      styles.wrap_header_l_info_row_info_stylelabel
                    )}
                  >
                    注册地址：
                  </span>
                  <span
                    className={classnames(
                      styles.wrap_header_l_info_row_info_value,
                      styles.wrap_header_l_info_row_info_stylevalue
                    )}
                  >
                    <Tooltip title={getAddress}>{getAddress}</Tooltip>
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div className={styles.wrap_header_r}>
            <Card styles={cardStyles} baseStore={store}></Card>
          </div>
        </div>
        <div className={styles.tab}>
          <Tabs
            defaultActiveKey="1"
            items={[
              {
                label: `舆情风险`,
                key: '1',
                children: (
                  <PublicMonitor customFormNameColumns={[]} chiName={clientInfo?.clientName} />
                ),
              },
            ]}
          ></Tabs>
        </div>
      </div>
    </Modal>
  )
}

export default observer(Index)
