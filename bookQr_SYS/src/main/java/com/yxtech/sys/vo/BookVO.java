package com.yxtech.sys.vo;

import com.yxtech.sys.domain.Book;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**

 * 图书vo
 * Created by yanfei on 2015/10/19.
 */

public class BookVO extends Book{

    // 图书课件二维码文件ID
    private String qrcodeUrl;

    //审核邮箱
    private  String email;

    //提示信息
    private String tip;

    private Integer editorId;


    //是否创建 true：创建    false:未创建
    private boolean createFlag;


    public boolean isCreateFlag() {
        return createFlag;
    }

    public void setCreateFlag(boolean createFlag) {
        this.createFlag = createFlag;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }


    public Integer getEditorId() {
        return editorId;
    }

    public void setEditorId(Integer editorId) {
        this.editorId = editorId;
    }

    public BookVO(){

    }

    public BookVO(Book book){
        this.setId(book.getId());
        this.setAuthor(book.getAuthor());
        this.setCode(book.getCode());
        this.setCategory(book.getCategory());
        this.setCategoryId(book.getCategoryId());
        this.setCategorySuperId(book.getCategorySuperId());
        this.setCover(book.getCover());
        this.setIsbn(book.getIsbn());
        this.setName(book.getName());
        this.setPress(book.getPress());
        this.setPressId(book.getPressId());
        this.setRemark(book.getRemark());
        this.setLogo(book.getLogo());
        this.setWidth(book.getWidth());
        this.setOnwer(book.getOnwer());
        this.setAuditorEmail(book.getAuditorEmail());
        this.setAuditorId(book.getAuditorId());
        this.setBookaskUrl(book.getBookaskUrl());
        this.setQuoteid(book.getQuoteid());
        this.setStatus(book.getStatus());
        this.setViewNum(book.getViewNum());
        this.setTip(book.getTip());
        this.setCreateTime(book.getCreateTime());
        this.setEditor(book.getEditor());
    }

    public static   void  main(String[] arg) {

        String name = "测试";
        if (name.matches("^.*[(/)|(\\\\)|(:)|(\\*)|(\\?)|(\")|(<)|(>)].*$")){
            System.out.println(name);
        }
        Pattern pattern = Pattern.compile("^.*[(/)|(\\\\)|(:)|(\\*)|(\\?)|(\")|(<)|(>)].*$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()){
            System.out.println(matcher.group());
        }

       /* if (matcher.find()) {
            System.out.println(matcher.group());
        }*/

    }
}

