package jack.com.servicekeep.bean;

import java.util.List;

/**
 * Created by Sl on 2019/6/10.
 */

public class Message {

    /**
     * msg_code : 0000
     * msg : ok
     * data : [{"phone_check":"1","sms_id":"2111"}]
     */

    private String msg_code;
    private String msg;
    private List<DataBean> data;

    public String getMsg_code() {
        return msg_code;
    }

    public void setMsg_code(String msg_code) {
        this.msg_code = msg_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * phone_check : 1
         * sms_id : 2111
         */

        private String phone_check;
        private String sms_id;

        public String getPhone_check() {
            return phone_check;
        }

        public void setPhone_check(String phone_check) {
            this.phone_check = phone_check;
        }

        public String getSms_id() {
            return sms_id;
        }

        public void setSms_id(String sms_id) {
            this.sms_id = sms_id;
        }
    }
}
