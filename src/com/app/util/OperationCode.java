package com.app.util;

public class OperationCode {
    
    public static final int REGISTER = 0;   // 注册
    public static final int LOGIN = 1;      // 登陆
    public static final int UPLOAD_CONTACT_BOOK = 2;  // 上传通讯录
    public static final int FRIEND_RECOMMEND = 3;     // 好友推荐
    public static final int ADD_FRIEND = 4;           // 添加好友
    public static final int CHANGE_SETTINGS = 5;      // 更改设置
    public static final int LAUNCH_EVENT = 6;         // 发起活动
    public static final int GET_EVENTS = 7;           // 根据活动id获取活动具体内容
    public static final int GET_MY_EVENTS = 8;    // 获取我参加的活动
    public static final int GET_RELEVANT_EVENTS = 9;       // 获取与我相关的活动
    public static final int GET_RECOM_EVENTS= 10;    // 获取推荐的活动
    public static final int GET_IMPORTANT_INFO = 11;            // 获取活动的重要消息
    public static final int ADD_GOOD = 12;     // 增加一个好评
    public static final int GET_COMMENTS = 13;      // 获取评论
    public static final int ADD_COMMENT = 14;    // 发评论
    public static final int DELETE_COMMENT = 15;  // 删除一条评论
    public static final int SEARCH_EVENT = 16;         // 查找活动
    public static final int GET_SALT_VALUE = 17;      // 获取盐值
    public static final int SYNCHRONIZE = 18;         // 同步好友列表
    public static final int SEARCH_FRIEND = 19;		// 搜索好友
    public static final int PARTICIPATE_EVENT = 20;   // 参加活动（请求、同意或拒绝）
    public static final int INVITE_FRIENDS = 21;             // 活动发起者邀请好友参加活动
    public static final int NOTE = 22;                                 // 记事本功能
    public static final int UPLOAD_AVATAR = 23;        // 上传头像
    public static final int GET_AVATAR = 24;                 // 获取头像
    public static final int LOGOUT = 25;                              // 登出
    public static final int GET_USER_INFO = 26;            // 获取单个用户信息(用id)
    public static final int CHANGE_PW = 27;                 // 更改密码
    public static final int ADD_PCOMMENT = 28;       // 发图片评论
    public static final int GET_PCOMMENTS = 29;        // 获取图片评论
    public static final int DELETE_PCOMMENT = 30; // 删除图片评论
    public static final int UPLOAD_PHOTO = 31; // 上传图片
    public static final int DOWNLOAD_PHOTO = 32; // 下载图片
    public static final int GET_PHOTO_LIST = 33; // 拉取图片id列表
}
