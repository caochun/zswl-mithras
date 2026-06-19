import { useEffect, useMemo } from 'react'
import { Page } from '@zswl/components'
import { Collapse, Divider, Skeleton, Badge } from 'antd'
import BaseInfo from '../../BaseInfo'
import { observer } from '@zswl/admin'
import styles from './index.less'
import store from './store'
import { ChangeLogDiff } from '@/components/ChangeLogDiff/ChangeLogDiffEntries'

const { Panel } = Collapse

function Index({ params: { id } }) {
  const { compareData } = store
  const { moduleChanged, newData } = compareData
  useEffect(() => {
    store.init(id)
  }, [id])

  const getDetail = (data = {}) => {
    const newDetail = {}
    const oldDetail = {}
    const isLog = {}
    Object.entries(data).forEach(([field, { value, beforeValue, isChange }]) => {
      newDetail[field] = value
      oldDetail[field] = beforeValue
      if (isChange) isLog[field] = true
    })
    return { newDetail, oldDetail, isLog }
  }
  const baseInfoDetail = useMemo(() => {
    return getDetail(newData?.BASE_INFO?.[0])
  }, [newData?.BASE_INFO])
  return (
    <Page store={store} header={null}>
      {Object.keys(compareData).length > 0 ? (
        <div className={styles.diffLog}>
          <h3>变更日志版本对比</h3>
          <Collapse defaultActiveKey={[]} accordion className={styles.collapse}>
            {baseInfoDetail && (
              <Panel
                header={
                  <div>
                    <span>基本信息变更日志</span>
                    {moduleChanged?.BASE_INFO && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="1"
              >
                <BaseInfo detail={baseInfoDetail.oldDetail} canEdit={false} />
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <BaseInfo
                  detail={baseInfoDetail.newDetail}
                  isLog={baseInfoDetail.isLog}
                  canEdit={false}
                />
              </Panel>
            )}
            <Panel header="文件变更日志" key="file" forceRender>
              <ChangeLogDiff
                version={id}
                moduleType="GROUP_CREDIT_REVIEW"
                options="projReviewMaterialsEnum"
                functionCode="grrfilelistversioncompare"
              />
            </Panel>
          </Collapse>
        </div>
      ) : (
        <Skeleton></Skeleton>
      )}
    </Page>
  )
}

export default observer(Index)
