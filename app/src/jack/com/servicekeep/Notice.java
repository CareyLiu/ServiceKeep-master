package jack.com.servicekeep;


/**
 * 消息实体类
 * type消息类型
 * content消息内容
 */
public class Notice {
    public int type;
    public Object content;

    public Notice(int type) {
        this.type = type;
    }

    public Notice(int type, Object content) {
        this.type = type;
        this.content = content;
    }

    public Notice() {
    }

    public String devId;

    public String input_type;
}
