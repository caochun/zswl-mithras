import { observer } from '@zswl/admin'
import { Anchor } from 'antd'
import BaseInfo from './BaseInfo'
import ChengZuRen from './ChengZuRen'
import ChaXunZongJie from './ChaXunZongJie'
import styles from './index.less'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import { Tooltip } from 'antd'

const { Link } = Anchor

const Index = ({ canEditFlag, clientGroup, store }) => {
  // const anchorList = [
  //   {
  //     label: '基本信息',
  //   },
  // ]
  // Object.keys(clientGroup).map((key) => {
  //   return clientGroup[key].map((item, index) => {
  //     const linkTitle = `${getKeyOptionsLabelMapPlus('clientRole')[item.clientRole]}${
  //       item.clientRole === 'MAIN_LESSEE' || clientGroup[key].length < 2 ? '' : index + 1
  //     }`
  //     anchorList.push({
  //       label: linkTitle,
  //     })
  //     return {
  //       label: linkTitle,
  //     }
  //   })
  // })

  return (
    <>
      <div className={styles.wrap}>
        <div className={styles.contentWrap}>
          <div className={styles.anchorWrap}>
            <div className={styles.anchor}>
              <Anchor offsetTop={20}>
                <Link href="#baseInfo" title={'基本信息'} />
                {Object.keys(clientGroup).map((key) => {
                  return clientGroup[key].map((item, index) => {
                    const linkTitle = `${getKeyOptionsLabelMapPlus('clientRole')[item.clientRole]}${
                      item.clientRole === 'MAIN_LESSEE' || clientGroup[key].length < 2
                        ? ''
                        : index + 1
                    }`
                    return (
                      <Link
                        href={`#chengZuRen_${item.id}`}
                        title={<Tooltip title={linkTitle}>{linkTitle}</Tooltip>}
                        key={item.id}
                      />
                    )
                  })
                })}
                <Link href="#chaXunZongJie" title={'查询总结'} />
              </Anchor>
            </div>
          </div>
          <div className={styles.content}>
            <div id="baseInfo">
              <BaseInfo canEditFlag={canEditFlag} store={store}></BaseInfo>
            </div>
            <div id="chengZuRen">
              {Object.keys(clientGroup).map((key) => {
                return clientGroup[key].map((item, index) => {
                  return (
                    <>
                      <div key={`${key}_${index}`}>
                        <ChengZuRen
                          title={`${getKeyOptionsLabelMapPlus('clientRole')[item.clientRole]}${
                            item.clientRole === 'MAIN_LESSEE' || clientGroup[key].length < 2
                              ? ''
                              : index + 1
                          }`}
                          canEditFlag={canEditFlag}
                          detail={item}
                          anchorId={`chengZuRen_${item.id}`}
                          store={store}
                        ></ChengZuRen>
                      </div>
                    </>
                  )
                })
              })}
            </div>
            <div id="chaXunZongJie">
              <ChaXunZongJie canEditFlag={canEditFlag} store={store}></ChaXunZongJie>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

export default observer(Index)
