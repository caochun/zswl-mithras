import BiView from '../../BiView'
import { observer } from '@zswl/admin'
import { Empty, Tree, Divider } from 'antd'
import { Button, Page, Flex } from '@zswl/components'
import styles from './index.less'
import { AlignRightOutlined, DoubleLeftOutlined } from '@ant-design/icons'
import YeWuYunYingFenXi from '../../Operation/YeWuYunYingFenXi'
import HeTongShiXiaoJianKong from '../../Operation/HeTongShiXiaoJianKong'
import YunYingDaiBan from '../../Operation/YunYingDaiBan/ReportOperationPending'
import classNames from 'classnames'
import store from './Store'
import { useMemo } from 'react'

const ReportMap = {
  YE_WU_YUN_XING_FEN_XI: <YeWuYunYingFenXi></YeWuYunYingFenXi>,
  YUN_YING_DAI_BAN: <YunYingDaiBan></YunYingDaiBan>,
  HE_TONG_SHI_XIAO: <HeTongShiXiaoJianKong></HeTongShiXiaoJianKong>,
}

const { DirectoryTree } = Tree

function Index() {
  const { pageStore, currentReport, isFold, setIsFold, handleRefresh, handleSelect } = store

  // return <HeTongShiXiaoJianKong></HeTongShiXiaoJianKong>

  const treeData = pageStore.getData().list || []

  const ReportContent = useMemo(() => {
    return (
      <div className={styles.content}>
        <Flex
          justify="space-between"
          style={{
            padding: 8,
          }}
        >
          <Flex align="center">
            <div className={styles.icon} onClick={() => setIsFold(!isFold)}>
              {isFold ? (
                <AlignRightOutlined className={styles.fold} />
              ) : (
                <DoubleLeftOutlined className={styles.fold} />
              )}
            </div>
            <h4 style={{ margin: 0 }}>{currentReport.title}</h4>
          </Flex>
          {currentReport.hasRefresh && (
            <Button type="primary" onClick={handleRefresh}>
              刷新
            </Button>
          )}
        </Flex>
        {currentReport.href?.startsWith('http') ? (
          <BiView
            url={currentReport.href}
            id="newReportIframe"
            style={{ border: 0, width: '100%', height: '100%' }}
          />
        ) : ReportMap[currentReport.key] ? (
          <div className={styles.customTable}>{ReportMap[currentReport.key]}</div>
        ) : (
          <Flex
            align="center"
            justify="center"
            style={{ width: '100%', height: '100%', background: '#fff' }}
          >
            <Empty description="请先选中管报" />
          </Flex>
        )}
      </div>
    )
  }, [currentReport, isFold])

  return (
    <Page store={pageStore} className={styles.page}>
      <div className={styles.pageContent}>
        <div className={styles.left}>
          <div className={classNames(styles.anchorWrap, isFold && styles.collapsed)}>
            <DirectoryTree
              height={500}
              style={{ marginRight: 20 }}
              treeData={treeData}
              onSelect={handleSelect}
            ></DirectoryTree>
          </div>
        </div>
        <div className={styles.right}>{ReportContent}</div>
      </div>
    </Page>
  )
}

export default observer(Index)
