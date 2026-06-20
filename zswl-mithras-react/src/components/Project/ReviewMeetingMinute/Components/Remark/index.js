import { FormItemContent } from '@/components/Form'
import { observer } from '@zswl/admin'
import { Descriptions,Input,Form } from 'antd'
import styles from './index.less'

function Index({ showValue, form, detail, businessKey }) {
    const getDetailValue = (key) => {
        return detail[key]
    }
    return (
        // <Descriptions dataSource={detail} title="" bordered column={2} labelStyle={{ background: '#F5F6FA' }} size={'small'} className={styles.des} >
        //     <Descriptions.Item span={2}  >
        <>
            <p>补充说明</p>
            <Form.Item
                name={'suppleRemark'}
                >
                <Input.TextArea
                    disabled={showValue}
                    autoSize={{ minRows: 4, maxRows: 20 }}
                    placeholder={!showValue&&"请输入补充说明"}
                />
            </Form.Item>
        </>

                // {/* <FormItemContent
                //     formDataShow
                //     formContent={ */}

        //             }
        //             value={getDetailValue('pledgeDescribe')}
        //             showValue={showValue}
        //         />
        //     </Descriptions.Item>
        // </Descriptions>
  )
}

export default observer(Index)
