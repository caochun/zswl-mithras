import { observer } from '@zswl/admin'
import { Form } from '@zswl/components'
import { DatePicker, Input } from 'antd'
import UploadMedia from './UploadMedia'
import styles from './index.less'
import { useEffect } from 'react'

const { Item } = Form
const { TextArea } = Input

const Index = ({ form, store }) => {
  const { bannerDetail, pageStatus } = store

  useEffect(() => {
    const { content, title, expirationFrom, expirationTo } = bannerDetail ?? {}
    form.setFieldsValue({
      content,
      title,
      expiration: [
        expirationFrom && moment(expirationFrom),
        expirationFrom && moment(expirationTo),
      ],
    })
  }, [form, pageStatus, bannerDetail])
  return (
    <Form
      layout="vertical"
      form={form}
      labelCol={{ span: 6 }}
      preserve={false}
      className={styles.formModal}
    >
      <Item name="banner">
        <UploadMedia name="banner" store={store}></UploadMedia>
      </Item>
      <Item name="title" label="标题" rules={[{ required: true, message: '请输入' }]}>
        <Input placeholder="请输入标题" />
      </Item>
      <Item name="content" label="内容">
        <TextArea
          autoSize={{ minRows: 4, maxRows: 20 }}
          placeholder="请输入公告正文"
          maxLength={2000}
        />
      </Item>
      <Item name="expiration" label="有效期">
        <DatePicker.RangePicker />
      </Item>
    </Form>
  )
}

export default observer(Index)
