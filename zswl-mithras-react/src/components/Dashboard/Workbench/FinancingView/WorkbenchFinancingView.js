import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import Title from '../../Title'
import CardPanelFieldsFilter from '../../CardPanelFieldsFilter'
import StagePanel from '../../StagePanel'
import { columnsFilterKey, getItemConfigByGroupCode } from './Config'
import FinancingViewListDrawer from './ListDrawer/FinancingViewListDrawer'
import Store from './Store'
import styles from './index.less'
import { DatePicker, Tooltip } from 'antd'
import IconFont from '@/components/Icon'

const WorkbenchFinancingView = ({ title, iconType }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const disabledDate = (current) => {
    const earliestDate = moment('2025-11')
    return (
      (current && current >= moment().startOf('month')) || current < earliestDate.startOf('month')
    )
  }
  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <CardPanelFieldsFilter
        title={''}
        getFieldsApi={store.getFieldsApi}
        columnsFilterKey={columnsFilterKey}
        queryParams={{ date: store.date ? moment(store.date).format('YYYY-MM') : '' }} // 查询参数暂定
      >
        {(data) => {
          return (
            <>
              <div className={styles.search}>
                <span>选择日期：</span>
                <DatePicker
                  picker="month"
                  disabledDate={disabledDate}
                  value={store.date}
                  onChange={store.setDate}
                />
                <div className={styles.header_extra}>
                  {
                    <Tooltip title={'所选月份数据取自月末最后一天'}>
                      <IconFont className={styles.icon} type={'icon-icon_info'} />
                    </Tooltip>
                  }
                </div>
              </div>
              <div className={styles.content}>
                {data.map((item) => {
                  return (
                    <div className={styles.row}>
                      <StagePanel
                        data={item}
                        onClick={(data) => {
                          store.setCurCardData({
                            ...data,
                            queryDate: store.date ? moment(store.date).format('YYYY-MM') : '',
                          })
                          store.listDrawer.open()
                        }}
                        rowStyle={{
                          width: [
                            'FUND_FINANCE_LOAN_THIS_YEAR',
                            'FUND_FINANCE_LOAN_THIS_MONTH',
                          ].includes(item?.groupCode)
                            ? `calc(${100 / 3}% - 10px)`
                            : `calc(${100 / 2}% - 10px)`,
                        }}
                        fieldsConfig={({ groupCode }) => {
                          return getItemConfigByGroupCode(groupCode)?.fields ?? []
                        }}
                      />
                    </div>
                  )
                })}
              </div>
            </>
          )
        }}
      </CardPanelFieldsFilter>
      <FinancingViewListDrawer store={store}></FinancingViewListDrawer>
    </div>
  )
}

export default observer(WorkbenchFinancingView)
