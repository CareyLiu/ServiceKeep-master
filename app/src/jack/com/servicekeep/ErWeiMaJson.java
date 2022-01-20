package jack.com.servicekeep;

import java.io.Serializable;

public class ErWeiMaJson implements Serializable {


    /**
     * cqc_id : 1
     * inst_id : 532
     * doorName : 入口
     * host : 39.106.18.182
     * port : 8080
     * gpsSwitch : 0
     * type : 1
     */

    private String cqc_id;
    private String inst_id;
    private String doorName;
    private String host;
    private String port;
    private String gpsSwitch;
    private String type;




    public String getCqc_id() {
        return cqc_id;
    }

    public void setCqc_id(String cqc_id) {
        this.cqc_id = cqc_id;
    }

    public String getInst_id() {
        return inst_id;
    }

    public void setInst_id(String inst_id) {
        this.inst_id = inst_id;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getGpsSwitch() {
        return gpsSwitch;
    }

    public void setGpsSwitch(String gpsSwitch) {
        this.gpsSwitch = gpsSwitch;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
