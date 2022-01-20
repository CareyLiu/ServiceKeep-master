/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jack.com.servicekeep;

import java.io.Serializable;
import java.util.List;


public class AppResponse<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;

    public String is_added;//是否添加过主机
    public String available_balance;
    public String msg_code;
    public String msg;
    public String next;//0 否 1 是
    public String typeNext;//
    public String change_state;
    public List<T> data;
    public String wares_id_data;//商品列表
    public String mqtt_connect_state;
    public String mqtt_connect_prompt;
    public String fkjkr_id;

    @Override
    public String toString() {
        return "AppResponse{\n" +//
                "\tmsg_code=" + msg_code + "\n" +//
                "\tmsg='" + msg + "\'\n" +//
                "\tdata=" + data + "\n" +//
                '}';
    }
}
