package com.yxtech.sys.vo.bookQr;

/**
 * desc:  <br>
 * date: 2019/6/6 0006
 *
 * @author cuihao
 */
public class GetWebUrlVo {
    private String playAuth;

    public String getPlayAuth() {
        return playAuth;
    }

    public void setPlayAuth(String playAuth) {
        this.playAuth = playAuth;
    }

    public GetWebUrlVo() {
    }

    public GetWebUrlVo(String playAuth) {
        this.playAuth = playAuth;
    }
}
