package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_client")
public class Client {
    @Id
    private Integer id;

    /**
     * 姓名
     */
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
    @Column(name = "leader_Phone")
    private String leaderPhone;

    /**
     * 想要出版的图书名称
     */
    @Column(name = "book_Name")
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
    @Column(name = "plan_Time")
    private String planTime;

    /**
     * 1.计划中；2.写作中；3.已定稿；4.有讲义
     */
    private Integer progress;

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

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取姓名
     *
     * @return name - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取职务
     *
     * @return job - 职务
     */
    public String getJob() {
        return job;
    }

    /**
     * 设置职务
     *
     * @param job 职务
     */
    public void setJob(String job) {
        this.job = job;
    }

    /**
     * 获取专业方向
     *
     * @return major - 专业方向
     */
    public String getMajor() {
        return major;
    }

    /**
     * 设置专业方向
     *
     * @param major 专业方向
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * 获取教授课程名
     *
     * @return lesson - 教授课程名
     */
    public String getLesson() {
        return lesson;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * 设置教授课程名
     *

     * @param lesson 教授课程名
     */
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    /**
     * 获取学校名称
     *
     * @return school - 学校名称
     */
    public String getSchool() {
        return school;
    }

    /**
     * 设置学校名称
     *
     * @param school 学校名称
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * 获取院系名称
     *
     * @return depart - 院系名称
     */
    public String getDepart() {
        return depart;
    }

    /**
     * 设置院系名称
     *
     * @param depart 院系名称
     */
    public void setDepart(String depart) {
        this.depart = depart;
    }

    /**
     * 获取联系电话
     *
     * @return phone - 联系电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置联系电话
     *
     * @param phone 联系电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取QQ号码
     *
     * @return qq - QQ号码
     */
    public String getQq() {
        return qq;
    }

    /**
     * 设置QQ号码
     *
     * @param qq QQ号码
     */
    public void setQq(String qq) {
        this.qq = qq;
    }

    /**
     * 获取预计出版数量（册/年）
     *
     * @return num - 预计出版数量（册/年）
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置预计出版数量（册/年）
     *
     * @param num 预计出版数量（册/年）
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * 获取领导电话
     *
     * @return leader_Phone - 领导电话
     */
    public String getLeaderPhone() {
        return leaderPhone;
    }

    /**
     * 设置领导电话
     *
     * @param leaderPhone 领导电话
     */
    public void setLeaderPhone(String leaderPhone) {
        this.leaderPhone = leaderPhone;
    }

    /**
     * 获取想要出版的图书名称
     *
     * @return book_Name - 想要出版的图书名称
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * 设置想要出版的图书名称
     *
     * @param bookName 想要出版的图书名称
     */
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    /**
     * 获取是否愿意在我出版社出版
     *
     * @return desire - 是否愿意在我出版社出版
     */
    public Integer getDesire() {
        return desire;
    }

    /**
     * 设置是否愿意在我出版社出版
     *
     * @param desire 是否愿意在我出版社出版
     */
    public void setDesire(Integer desire) {
        this.desire = desire;
    }

    /**
     * 获取是否编写过教材
     *
     * @return editor - 是否编写过教材
     */
    public Integer getEditor() {
        return editor;
    }

    /**
     * 设置是否编写过教材
     *
     * @param editor 是否编写过教材
     */
    public void setEditor(Integer editor) {
        this.editor = editor;
    }

    /**
     * 获取申请密码所发送到的邮箱
     *
     * @return email - 申请密码所发送到的邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置申请密码所发送到的邮箱
     *
     * @param email 申请密码所发送到的邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取索取教学资源目的
     *
     * @return objective - 索取教学资源目的
     */
    public String getObjective() {
        return objective;
    }

    /**
     * 设置索取教学资源目的
     *
     * @param objective 索取教学资源目的
     */
    public void setObjective(String objective) {
        this.objective = objective;
    }

    /**
     * 获取是否有出版计划
     *
     * @return plan - 是否有出版计划
     */
    public Integer getPlan() {
        return plan;
    }

    /**
     * 设置是否有出版计划
     *
     * @param plan 是否有出版计划
     */
    public void setPlan(Integer plan) {
        this.plan = plan;
    }

    /**
     * 获取计划出版的时间
     *
     * @return plan_Time - 计划出版的时间
     */
    public String getPlanTime() {
        return planTime;
    }

    /**
     * 设置计划出版的时间
     *
     * @param planTime 计划出版的时间
     */
    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }

    /**
     * 获取1.计划中；2.写作中；3.已定稿；4.有讲义
     *
     * @return progress - 1.计划中；2.写作中；3.已定稿；4.有讲义
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * 设置1.计划中；2.写作中；3.已定稿；4.有讲义
     *
     * @param progress 1.计划中；2.写作中；3.已定稿；4.有讲义
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
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
}