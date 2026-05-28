import IconFont from '@/components/Icon'
import { history, observer } from '@zswl/admin'
import { App } from '@zswl/components'
import { Tooltip } from 'antd'
import styles from '../../index.less'
import store from '../../store'
const Client = () => {
  const { page } = store
  const { optionsType } = App.getData()
  const clientTypeEumn = optionsType.clientType
  const data = page.getData()
  return (
    <div className={styles.moduleWrap}>
      <div className={styles.projectTitle}>
        <span>
          {data.projName}({data.projCode})
          <Tooltip title={`项目主办：${data.projSponsorUserName}(${data.projSponsorDeptName})`}>
            <IconFont style={{ fontSize: 20, marginLeft: 4 }} type="icon-icon_user" />
          </Tooltip>
        </span>
        <span className={styles.type}>业务类型：{data.bizType}</span>
      </div>
      <div className={styles.title}>
        <div className={styles.left}>
          <span style={{ marginRight: 12 }}>客户信息</span>
          <div className={styles.clientNum}>
            {data.clientInfos?.length > 99 ? '99+' : data.clientInfos?.length || 0}
          </div>
        </div>
      </div>
      {data.clientInfos && data.clientInfos.length > 0 && (
        <div className={styles.scrollList}>
          <div className={styles.clientList}>
            {data.clientInfos?.map((item, index) => {
              const { clientType: clientTypeText, clientName, clientId, clientCategory } = item
              const clientType = clientTypeEumn.find((i) => i.label === clientTypeText)?.value
              return (
                <div
                  key={index}
                  className={styles.item}
                  onClick={() => {
                    history.push(
                      `/customer/maintain/detail/${clientId}?clientType=${clientType}&flag=info&typeId=create`
                    )
                  }}
                >
                  <div className={styles.name}>{clientCategory}</div>
                  <div className={styles.type}>
                    {clientType === 'NORMAL' ? (
                      <IconFont style={{ marginRight: 4 }} type="icon-icon_user" />
                    ) : (
                      <IconFont style={{ marginRight: 4 }} type="icon-a-icon_legalperson" />
                    )}

                    {clientTypeText}
                  </div>
                  <Tooltip title={clientName}>
                    <div className={styles.company}>{clientName}</div>
                  </Tooltip>
                </div>
              )
            })}
          </div>
        </div>
      )}
    </div>
  )
}

export default observer(Client)
