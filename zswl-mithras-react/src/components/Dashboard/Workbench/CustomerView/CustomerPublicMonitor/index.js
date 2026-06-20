import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import { RiskPublicMonitor as PublicMonitor } from '@/components/Risk/PublicMonitorListEntries'
import styles from './index.less'

const Index = () => {
  return (
    <div className={styles.content}>
      <CardPanelFieldsFilter title={'客户舆情'}>
        <PublicMonitor
          showPageStyle={false}
          canEdit={false}
          customNameColumns={[
            '标题',
            '客户名称',
            '统一社会信用代码',
            '状态',
            '预警星级',
            '预警信号',
            '来源类型',
            '创建时间',
            '创建人',
            '关联关系描述',
            '信息发布日期',
          ]}
          // customFormNameColumns={[]}
          customFormNameColumns={['标题', '客户名称', '状态', '预警星级', '预警信号']}
        />
      </CardPanelFieldsFilter>
    </div>
  )
}
export default Index
