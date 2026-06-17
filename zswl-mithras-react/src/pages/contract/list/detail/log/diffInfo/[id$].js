import { useEffect, useState } from 'react'
import { Page } from '@zswl/components'
import { Collapse, Divider, Skeleton, Badge } from 'antd'
import BaseInfo from './BaseInfo'
import BaoJia from './BaoJia'
import GaiSuanZuJin from './GaiSuanZuJin'
import ShiJiZuJin from './ShiJiZuJin'
import ChengZuRen from './ChengZuRen'
import ShouKuan from './ShouKuan'
import HuiKuan from './HuiKuan'
import ZuLinWu from './ZuLinWu'
import DanBao from './DanBao'
import DiYa from './DiYa'
import ZhiYa from './ZhiYa'
import FileDiff from '@/components/FileDiff'
import { observer } from '@zswl/admin'
import styles from './index.less'
import store from './store'
import { bizTypeMapText } from '@/components/Contract/bizTypeConfig'

const { Panel } = Collapse

const bizTypePriceDetail = {
  BL: 'BL_PRICE',
  ZL: 'ZL_PRICE',
  ZZ: 'ZL_PRICE',
  ZR: 'ZR_PRICE',
}

const HKUseEnum = {
  BL: 'BANK_ACCOUNT_BLHK',
  ZR: 'BANK_ACCOUNT_ZRHK',
}

const SKUseEnum = {
  BL: 'BANK_ACCOUNT_BLSK',
  ZR: 'BANK_ACCOUNT_ZRSK',
  ZL: 'BANK_ACCOUNT_ZLSK',
  ZZ: 'BANK_ACCOUNT_ZZSK',
}

function Index({ params: { id }, query: { bizType, contractId, leaseLog } }) {
  const BL_ZR = bizType === 'BL' || bizType === 'ZR'

  const { compareData, getBaseInfoData, getBoajiaData } = store

  const [baseInfoDetail, setBaseInfoDetail] = useState(null)
  const [baojiaDetail, setBaojiaDetail] = useState(null)
  const [gaisuanDetail, setGaisuanDetail] = useState(null)
  const [shijiDetail, setShijiDetail] = useState(null)
  const [chenzuDetail, setChenzuDetail] = useState(null)
  const [shoukuanDetail, setShoukuanDetail] = useState(null)
  const [huikuanDetail, setHuikuanDetail] = useState(null)
  const [zulinwuDetail, setZulinwuDetail] = useState(null)
  const [danbaoDetail, setDanbaoDetail] = useState(null)
  const [diyaDetail, setDiYaDetail] = useState(null)
  const [zhiyaDetail, setZhiYaDetail] = useState(null)

  useEffect(() => {
    store.init(id)
  }, [id])

  useEffect(() => {
    if (Object.keys(compareData).length > 0) {
      if (!leaseLog) {
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
        // 概算
        setGaisuanDetail({
          isChange: compareData.moduleChanged.RENT_ESTIMATE,
          before: compareData.oldData.RENT_ESTIMATE,
          after: compareData.newData.RENT_ESTIMATE,
        })
        // 实际
        setShijiDetail({
          isChange: compareData.moduleChanged.ACTUAL_ESTIMATE,
          before: compareData.oldData.ACTUAL_ESTIMATE,
          after: compareData.newData.ACTUAL_ESTIMATE,
        })
        // 承租人
        setChenzuDetail({
          isChange: compareData.moduleChanged.TENANTRY,
          before: compareData.oldData.TENANTRY,
          after: compareData.newData.TENANTRY,
        })
        // 回款账户
        setHuikuanDetail({
          isChange: compareData.moduleChanged[HKUseEnum[bizType]],
          before: compareData.oldData[HKUseEnum[bizType]],
          after: compareData.newData[HKUseEnum[bizType]],
        })
        // 收款账户
        setShoukuanDetail({
          isChange: compareData.moduleChanged[SKUseEnum[bizType]],
          before: compareData.oldData[SKUseEnum[bizType]],
          after: compareData.newData[SKUseEnum[bizType]],
        })

        // 担保
        setDanbaoDetail({
          isChange: compareData.moduleChanged.GUARANTOR,
          before: compareData.oldData.GUARANTOR,
          after: compareData.newData.GUARANTOR,
        })
        //抵押
        setDiYaDetail({
          isChange: compareData.moduleChanged.MORTGAGE,
          before: compareData.oldData.MORTGAGE,
          after: compareData.newData.MORTGAGE,
        })
        //质押
        setZhiYaDetail({
          isChange: compareData.moduleChanged.PLEDGE,
          before: compareData.oldData.PLEDGE,
          after: compareData.newData.PLEDGE,
        })
      }

      // 租赁物
      setZulinwuDetail({
        isChange: compareData.newData.LEASE_ITEM?.[0]?.version?.isChange,
        before: compareData.newData.LEASE_ITEM?.[0]?.version?.beforeValue,
        after: compareData.newData.LEASE_ITEM?.[0]?.version?.value,
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
                <BaseInfo detail={baseInfoDetail.before?.detail} bizType={bizType}></BaseInfo>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <BaseInfo
                  detail={baseInfoDetail.after?.newDetail}
                  isLog={baseInfoDetail.after?.isLog}
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
                <BaoJia
                  detail={baojiaDetail.before?.detail}
                  contractId={baseInfoDetail.before?.detail?.id}
                ></BaoJia>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <BaoJia
                  detail={baojiaDetail.after?.newDetail}
                  showValue
                  isLog={baojiaDetail.after?.isLog}
                  contractId={baseInfoDetail.after?.newDetail?.id}
                ></BaoJia>
              </Panel>
            )}
            {gaisuanDetail && (
              <Panel
                header={
                  <div>
                    <span>{`概算${bizTypeMapText[bizType]?.rentTitle}`}变更日志</span>
                    {gaisuanDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="3"
              >
                <GaiSuanZuJin detail={gaisuanDetail.before}></GaiSuanZuJin>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <GaiSuanZuJin detail={gaisuanDetail.after} isLog></GaiSuanZuJin>
              </Panel>
            )}
            {shijiDetail && (
              <Panel
                header={
                  <div>
                    <span>{`实际${bizTypeMapText[bizType]?.rentTitle}`}变更日志</span>
                    {shijiDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="4"
              >
                <ShiJiZuJin detail={shijiDetail.before}></ShiJiZuJin>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <ShiJiZuJin detail={shijiDetail.after} isLog></ShiJiZuJin>
              </Panel>
            )}
            {chenzuDetail && (
              <Panel
                header={
                  <div>
                    <span>{BL_ZR ? '债权人/债务人' : '承租人'}变更日志</span>
                    {chenzuDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="5"
              >
                <ChengZuRen detail={chenzuDetail.before}></ChengZuRen>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <ChengZuRen detail={chenzuDetail.after} isLog></ChengZuRen>
              </Panel>
            )}

            {huikuanDetail && BL_ZR && (
              <Panel
                header={
                  <div>
                    <span>{BL_ZR ? '保理回款账户' : '保理回款账户'}变更日志</span>
                    {huikuanDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="16"
              >
                <HuiKuan detail={huikuanDetail.before}></HuiKuan>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <HuiKuan detail={huikuanDetail.after} isLog></HuiKuan>
              </Panel>
            )}

            {shoukuanDetail && (
              <Panel
                header={
                  <div>
                    <span>{BL_ZR ? '卖方收款账户' : '收款账户'}变更日志</span>
                    {shoukuanDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="6"
              >
                <ShouKuan detail={shoukuanDetail.before}></ShouKuan>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <ShouKuan detail={shoukuanDetail.after} isLog></ShouKuan>
              </Panel>
            )}
            {zulinwuDetail && (
              <Panel
                header={
                  <div>
                    <span>租赁物清单变更日志</span>
                    {zulinwuDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="7"
              >
                <ZuLinWu
                  businessVersion={zulinwuDetail.before}
                  contractId={contractId ?? baseInfoDetail.before?.detail?.id}
                  isLog
                ></ZuLinWu>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <ZuLinWu
                  isChange={zulinwuDetail.isChange}
                  businessVersion={zulinwuDetail.after}
                  contractId={contractId ?? baseInfoDetail.after?.newDetail?.id}
                  isLog
                ></ZuLinWu>
              </Panel>
            )}
            {danbaoDetail && (
              <Panel
                header={
                  <div>
                    <span>担保措施变更日志</span>
                    {danbaoDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="8"
              >
                <DanBao detail={danbaoDetail.before}></DanBao>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <DanBao detail={danbaoDetail.after} isLog></DanBao>
              </Panel>
            )}
            {diyaDetail && (
              <Panel
                header={
                  <div>
                    <span>抵押措施变更日志</span>
                    {diyaDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="9"
              >
                <DiYa detail={diyaDetail.before}></DiYa>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <DiYa detail={diyaDetail.after} isLog></DiYa>
              </Panel>
            )}
            {zhiyaDetail && (
              <Panel
                header={
                  <div>
                    <span>质押措施变更日志</span>
                    {zhiyaDetail.isChange && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
                key="10"
              >
                <ZhiYa detail={zhiyaDetail.before}></ZhiYa>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <ZhiYa detail={zhiyaDetail.after} isLog></ZhiYa>
              </Panel>
            )}
            {!leaseLog && (
              <Panel header="文件变更日志" key="file" forceRender>
                <FileDiff
                  version={id}
                  moduleType="CONTRACT"
                  options="contractTypeEnum"
                  functionCodeList={{ download: 'newContractFileDownload' }}
                  functionCode="contractfilelistversioncompare"
                />
              </Panel>
            )}
          </Collapse>
        </div>
      ) : (
        <Skeleton></Skeleton>
      )}
    </Page>
  )
}

export default observer(Index)
