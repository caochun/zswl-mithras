import IconFont from '@/components/Icon'
import { history, observer } from '@zswl/admin'
import styles from '../../index.less'
import BaseModule from '../BaseModule'
import store from '../../store'
import { amountFormat } from '@/utils'
import NoData from '../NoData'
const Establishment = () => {
  const { projEstablish } = store.page.getData()
  const {
    processStatus,
    createTime,
    approveTime,
    applyCreditAmount,
    processType,
    currentNode,
    projectId,
  } = projEstablish || {}
  return (
    <BaseModule
      style={{ marginBottom: 16 }}
      onClick={() => {
        history.push(`/project/establishment/detail/${projectId}?canEditFlag=true`)
      }}
      content={
        <>
          <div className={styles.title}>{'项目立项'}</div>
          {projEstablish ? (
            <div className={styles.establishmentContent}>
              <div className={styles.item} style={{ width: 200, textAlign: 'left' }}>
                <IconFont type="icon-shenpiguanli-copy" />
                {createTime || '-'} ～ {approveTime || '-'}
              </div>
              <div className={styles.item}>
                <IconFont type="icon-fukuanguanli" />
                {amountFormat(applyCreditAmount / 10000)}
              </div>
            </div>
          ) : (
            <NoData />
          )}
        </>
      }
      processStatus={processStatus}
      processType={processType}
      currentNode={currentNode}
    />
  )
}

export default observer(Establishment)
