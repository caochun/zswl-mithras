import EditDescription from '@/components/Table/EditDescription'
import { useMemo, useRef, useState } from 'react'
import { RESULT_COLUMNS } from '@/components/AfterLease/Level5ClassifyColumns'
import { isAssetJon } from '@/utils'
import { FileCommonParams } from '@/components/AfterLease/Level5ClassifyConfig'
import { observer } from '@zswl/admin'
import { useEffect } from 'react'
import styles from '../../index.less'
import { NoEnumFileTable } from '@/components'
import BoBei from './BoBei'
import RiskFactor from './RiskFactor'

function Index({ store, detail = {}, canEdit = true, businessVersion }) {
  const { id: mainId } = store.page.getParams()
  const { suggestFlag } = store.page.getData()
  const hasPermission = canEdit && isAssetJon()
  const editDescRef = useRef({})
  const [isAdjust, setIsAdjust] = useState(false)

  useEffect(() => {
    setIsAdjust(suggestFlag === 1)
  }, [suggestFlag])

  const params = {
    mainId,
    businessVersion,
    materialsType: 'ASSET_CLASSIFY_REVIEW',
    ...FileCommonParams.chufen,
  }

  const columns = useMemo(() => {
    return RESULT_COLUMNS({ isAdjust, setIsAdjust })
  }, [isAdjust, setIsAdjust])

  return (
    <>
      <div className={styles.content}>
        <EditDescription
          title={'初分结果'}
          detail={detail}
          saveData={store.modifyBaseInfo}
          canEdit={hasPermission}
          columns={columns}
          ref={editDescRef}
          onEditStatusChange={(value) => {
            setIsAdjust(suggestFlag === 1)
            const form = editDescRef.current?.form
            form.resetFields()
          }}
        />
        <div className={styles.fileWrap}>
          <BoBei canEdit={hasPermission} id={mainId}></BoBei>
        </div>
        <div className={styles.fileWrap}>
          <NoEnumFileTable
            title={'附件'}
            canEdit={hasPermission}
            params={params}
            columns={[
              { title: '资料名称', dataIndex: 'filename' },
              { title: '上传人', dataIndex: 'createByName' },
              { title: '上传时间', dataIndex: 'createTime' },
            ]}
          />
        </div>
        <div className={styles.fileWrap}>
          <RiskFactor canEdit={hasPermission} id={mainId}></RiskFactor>
        </div>
      </div>
    </>
  )
}

export default observer(Index)
