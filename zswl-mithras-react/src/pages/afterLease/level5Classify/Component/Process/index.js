import { GroupKey, MapStatus } from '@/pages/afterLease/level5Classify/config'
import { hasValue, isAssetJon } from '@/utils'
import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'
import { Spin, Steps } from 'antd'
import FirstPartModal from './FirstPartModal'
import ProcessDataModal from './ProcessDataModal'
import TipsModal from './TipsModal'
import styles from './index.less'

const { Step } = Steps

const Index = ({ store }) => {
  const {
    processData,
    processDataLoading,
    residueWorkday,
    assetClassifyStatus,
    isOnlyShowDefaultProcess,
  } = store

  const showBtn = (item, index) => {
    // 如果发起了季中初分，不显示发起按钮
    if (isOnlyShowDefaultProcess) {
      return false
    }
    return (
      GroupKey.includes(item.key) &&
      item.nodeStatue === 'WAIT' &&
      processData[index - 1]?.nodeStatue === 'FINISH' &&
      assetClassifyStatus !== 1
    )
  }

  // const showBtn = (item, index) => {
  //   return (
  //     ((GroupKey.includes(item.key) && item.nodeStatue === 'WAIT') ||
  //       (item.nodeStatue === 'PROCESS' && item.key === 'REVIEW')) &&
  //     processData[index - 1]?.nodeStatue === 'FINISH' &&
  //     assetClassifyStatus !== 1
  //   )
  // }

  return (
    <div className={styles.content}>
      <div className={styles.header}>
        <div className={styles.title}>定级流程</div>
        {assetClassifyStatus === 0 ? (
          hasValue(residueWorkday) ? (
            <div className={styles.workDay}>剩余工作日：{residueWorkday}天</div>
          ) : null
        ) : (
          <span className={styles.workDay}>流程已结束</span>
        )}
      </div>
      <div className={styles.step}>
        {!processDataLoading ? (
          <Steps progressDot>
            {processData?.map((item, index) => {
              return (
                <Step
                  key={item.key}
                  title={
                    GroupKey.includes(item.key) && item.nodeStatue === 'PROCESS'
                      ? item.nodeLable + '审批中'
                      : item.nodeLable
                  }
                  description={
                    <div>
                      {item.nodeStatue === 'FINISH' && item.endTime}
                      {showBtn(item, index) && (
                        <Button
                          type="primary"
                          onClick={() => store.sponsor(item)}
                          className={styles.btn}
                          disabled={!isAssetJon() || store.isQuarterEndFlowDisabled}
                        >
                          发起
                        </Button>
                      )}
                      {['INIT'].includes(item.key) && !isOnlyShowDefaultProcess && (
                        <div>
                          <Button
                            type="primary"
                            onClick={() => store.firstPart(item)}
                            className={styles.btn}
                            access="assetclassifyManualInitialDivision"
                            disabled={
                              [
                                processData[1].nodeStatue,
                                processData[2].nodeStatue,
                                processData[3].nodeStatue,
                              ].includes('FINISH') || !store.canClickQuarterEndInit
                            }
                          >
                            系统初分
                          </Button>
                        </div>
                      )}
                    </div>
                  }
                  status={MapStatus[item.nodeStatue]}
                />
              )
            })}
          </Steps>
        ) : (
          <div className={styles.spin}>
            <Spin></Spin>
          </div>
        )}
      </div>
      <ProcessDataModal store={store}></ProcessDataModal>
      <FirstPartModal store={store}></FirstPartModal>
      <TipsModal
        store={store.tipsModal}
        footer={[
          <Button key="cancel" onClick={() => store.tipsModal.close()}>
            取消
          </Button>,
          <Button
            type="primary"
            key="submit"
            loading={store.approvalLoading}
            onClick={() => {
              store.submitApproval()
            }}
          >
            确定
          </Button>,
        ]}
      />
    </div>
  )
}

export default observer(Index)
