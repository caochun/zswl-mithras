import { useEffect, useState } from 'react'
import { Page } from '@zswl/components'
import { Collapse, Divider, Skeleton, Badge } from 'antd'
import BaseInfo from './BaseInfo'
import BaoJia from './BaoJia'
import { observer } from '@zswl/admin'
import styles from './index.less'
import store from './store'
import { ChangeLogDiff } from '@/components/ChangeLogDiff/ChangeLogDiffEntries'

const { Panel } = Collapse

const bizTypePriceDetail = {
  BL: 'BL_PRICE',
  ZL: 'ZL_PRICE',
  ZZ: 'ZL_PRICE',
  ZR: 'ZR_PRICE',
}

function Index({ params: { id }, query: { bizType } }) {
  const { compareData, getBaseInfoData, getBoajiaData } = store
  const [baseInfoDetail, setBaseInfoDetail] = useState(null)
  const [baojiaDetail, setBaojiaDetail] = useState(null)

  useEffect(() => {
    store.init(id)
  }, [id])

  useEffect(() => {
    if (Object.keys(compareData).length > 0) {
      // 基本信息
      setBaseInfoDetail({
        isChange: compareData.moduleChanged.BASE_INFO,
        before: getBaseInfoData(compareData.oldData.BASE_INFO[0], 'old'),
        after: getBaseInfoData(compareData.newData.BASE_INFO[0], 'new'),
      })
      // 报价方案
      setBaojiaDetail({
        isChange: compareData.moduleChanged[bizTypePriceDetail[bizType]],
        before: getBoajiaData(compareData.oldData[bizTypePriceDetail[bizType]]?.[0], 'old'),
        after: getBoajiaData(compareData.newData[bizTypePriceDetail[bizType]]?.[0], 'new'),
      })
    }
  }, [compareData])

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
                    {baseInfoDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="1"
              >
                <BaseInfo detail={baseInfoDetail.before} showValue bizType={bizType}></BaseInfo>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <BaseInfo
                  detail={baseInfoDetail.after}
                  showValue
                  isLog
                  bizType={bizType}
                ></BaseInfo>
              </Panel>
            )}
            {baojiaDetail && (
              <Panel
                header={
                  <div>
                    <span>报价方案变更日志</span>
                    {baojiaDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="2"
              >
                <BaoJia detail={baojiaDetail.before} showValue bizType={bizType}></BaoJia>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <BaoJia detail={baojiaDetail.after} showValue isLog bizType={bizType}></BaoJia>
              </Panel>
            )}
            <Panel header="文件变更日志" key="file" forceRender>
              <ChangeLogDiff
                version={id}
                moduleType="PROJ_ESTABLISH"
                options="projEstablishMaterialsEnum"
                functionCode="projestablishfilelistversioncompare"
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
