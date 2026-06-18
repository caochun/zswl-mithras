import { useEffect, useMemo, useState } from 'react'
import { Page } from '@zswl/components'
import { Collapse, Divider, Skeleton, Badge } from 'antd'
import { CreditEstablishDetailBaseInfo as BaseInfo } from '@/components/Credit/CreditEntries'
import { observer } from '@zswl/admin'
import styles from './index.less'
import store from './store'
import { compareDetail } from '@/utils'
import FileDiff from '@/components/FileDiff'

const { Panel } = Collapse

const DETAIL_MAP = {
  BASE_INFO: '基础信息',
}

function Index({ params: { id }, query: { bizType } }) {
  const { compareData } = store
  const { moduleChanged, newData } = compareData
  useEffect(() => {
    store.init(id)
  }, [id])

  const baseInfoDetail = useMemo(() => {
    return compareDetail(newData?.BASE_INFO?.[0])
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
                    {moduleChanged.BASE_INFO && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="1"
              >
                <BaseInfo detail={baseInfoDetail.detail} canEdit={false} />
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
              <FileDiff
                version={id}
                moduleType="GROUP_CREDIT_ESTABLISH"
                options="groupCreditEstablishMaterialsEnum"
                functionCode="gcefilelistversioncompare"
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
