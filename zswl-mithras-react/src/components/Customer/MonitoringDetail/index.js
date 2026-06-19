import { Tabs, Card, Tag, Table, Divider, Space } from 'antd'
import styles from './style.less'
import { Page, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import SafetyCertificateOutlined from './svgs/SafetyCertificateOutlined.svg'
import Bootm from './components/Bootm/index'
import store from './store'
import BreadcrumbList from '@/layout/BreadcrumbList'

const CustomerDetail = ({ path, params: { id }, query, pathname }) => {
  const { enterpriseName, uscc } = query
  const detail = store.page.getData()

  // 表格列定义
  const columns = [
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: '公司',
      dataIndex: 'company',
      key: 'company',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: '代码',
      dataIndex: 'code',
      key: 'code',
    },
  ]

  return (
    <Page noStyle params={{ id }} store={store.page}>
      <div className={styles['customer-detail']}>
        {/* 顶部企业基本信息 */}
        <BreadcrumbList pathname={pathname} />
        <div className={styles['company-header']}>
          <div className={styles['company-name']}>
            <Space className={styles['title']}>
              {enterpriseName}
              {!!detail?.clientStatus && (
                <Tag color="green">
                  {App.matchOption('clientStatus', detail.clientStatus)?.label}
                </Tag>
              )}
              {detail?.belongDeptName && <Tag color="#a274fa">{detail?.belongDeptName}</Tag>}
            </Space>
          </div>
          <div
            className={styles['company-desc']}
            style={{
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              display: '-webkit-box',
              WebkitLineClamp: 6,
              WebkitBoxOrient: 'vertical',
              width: '75%',
              fontSize: 12,
            }}
          >
            {store?.detail?.bizScope ?? store?.detail?.description}
          </div>

          <div className={styles.header}>
            <div className={styles.ratingInfo}>
              <div className={styles.rating}>
                <div className={styles.icon}>
                  <img src={'/public/assets/risk/customerView/icon-yq.svg'} alt="评级" />
                </div>
                <div className={styles.grade}>
                  <span className={styles.value}>
                    {
                      store.yqDetail.filter(
                        (item) =>
                          (item.date && item.opinionStatus === 'PEND_HANDLE') ||
                          item.opinionStatus === 'HANDLE_ING'
                      ).length
                    }
                    <span>/{store.yqDetail.filter((item) => item.date).length}</span>
                  </span>
                  <div style={{ fontSize: 12 }}>未完成/全部舆情</div>
                </div>
              </div>
              <Divider type="vertical" style={{ height: 'auto' }} />
              <div className={styles.rating}>
                <div className={styles.icon}>
                  <img src={'/public/assets/risk/customerView/icon-yj.svg'} alt="得分" />
                </div>
                <div className={styles.score}>
                  <span className={styles.value}>
                    {
                      store.yjDetail.filter(
                        (item) =>
                          (item.date && item.warnStatus === 'PEND_HANDLE') ||
                          item.opinionStatus === 'HANDLE_ING'
                      ).length
                    }
                    <span>/{store.yjDetail.filter((item) => item.date).length}</span>
                  </span>
                  <div style={{ fontSize: 12 }}>未完成/全部预警</div>
                </div>
              </div>
              {/* <div className={styles.toopl}>注:处理中舆情、预警/历史全部舆情、预警</div> */}
            </div>
            {/* <div className={styles.card}>
            <CustomCard
              onlyTotal={true}
              icon={<SafetyCertificateOutlined />}
              title="被执行信息"
              total={111}
            />
          </div> */}
          </div>
        </div>
        <div className={styles['company-tabs']}>
          <div className={styles.bomCustomer}>
            <div className={styles.title}>客户详情</div>
            <Bootm
              id={id}
              status={store.riskControlOpinionHandleStatus}
              yqDetail={store.yqDetail}
              yjDetail={store.yjDetail}
            ></Bootm>
          </div>
        </div>
      </div>
    </Page>
  )
}

export default observer(CustomerDetail)
