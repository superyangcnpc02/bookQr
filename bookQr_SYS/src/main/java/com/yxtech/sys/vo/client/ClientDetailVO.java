package com.yxtech.sys.vo.client;

import com.yxtech.sys.domain.Book;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 编写申请信息VO
 * Created by yanfei on 2015/11/6.
 */
public class ClientDetailVO {

    private Integer id;

    /**
     * 二维码id
     */
    @NotNull(message = "二维码")
    private  Integer qrcodeId;
    /**
     * 姓名
     */
    @NotNull(message = "姓名")
    private String name;

    /**
     * 职务
     */
    private String job;

    /**
     * 专业方向
     */
    private String major;

    /**
     * 教授课程名
     */
    private String lesson;

    /**
     * 学校名称
     */
    private String school;

    /**
     * 院系名称
     */
    private String depart;

    /**
     * 联系电话
     */
    @NotNull(message = "联系电话不能为空")
    private String phone;

    /**
     * QQ号码
     */
    private String qq;

    /**
     * 预计出版数量（册/年）
     */
    private Integer num;

    /**
     * 领导电话
     */
    private String leaderPhone;

    /**
     * 想要出版的图书名称
     */
    private String bookName;

    /**
     * 是否愿意在我出版社出版
     */
    private Integer desire;

    /**
     * 是否编写过教材
     */
    private Integer editor;

    /**
     * 申请密码所发送到的邮箱
     */
    private String email;

    /**
     * 索取教学资源目的
     */
    private String objective;

    /**
     * 是否有出版计划
     */
    private Integer plan;

    /**
     * 计划出版的时间
     */
    private String planTime;

    /**
     * 1.计划中；2.写作中；3.已定稿；4.有讲义
     */
    private Integer progress;

    /**
     * 索取的资源名
     */
    private String applyBookName;

    /**
     * 地址
     */
    private String address;

    /**
     * 微信唯一标识
     */
    private String openId;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 所在地
     */
    private String seat;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 所授课程
     */
    private String teach;

    /**
     * 头像
     */
    private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    private List<ClientBookDetailVo> bookItems;

    public String getApplyBookName() {
        return applyBookName;
    }

    public void setApplyBookName(String applyBookName) {
        this.applyBookName = applyBookName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // 编写过的书籍
    private List<ClientBookVO> books;

    public List<ClientBookVO> getBooks() {
        return books;
    }

    public void setBooks(List<ClientBookVO> books) {
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getLeaderPhone() {
        return leaderPhone;
    }

    public void setLeaderPhone(String leaderPhone) {
        this.leaderPhone = leaderPhone;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Integer getDesire() {
        return desire;
    }

    public void setDesire(Integer desire) {
        this.desire = desire;
    }

    public Integer getEditor() {
        return editor;
    }

    public void setEditor(Integer editor) {
        this.editor = editor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public Integer getPlan() {
        return plan;
    }

    public void setPlan(Integer plan) {
        this.plan = plan;
    }

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getQrcodeId() {
        return qrcodeId;
    }

    public void setQrcodeId(Integer qrcodeId) {
        this.qrcodeId = qrcodeId;
    }

    /**
     * 获取地址
     *
     * @return address - 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTeach() {
        return teach;
    }

    public void setTeach(String teach) {
        this.teach = teach;
    }

    public List<ClientBookDetailVo> getBookItems() {
        return bookItems;
    }

    public void setBookItems(List<ClientBookDetailVo> bookItems) {
        this.bookItems = bookItems;
    }
}
