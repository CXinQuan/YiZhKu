package global;

/**
 * Created by lenovo on 2018/7/15.
 */

public class Constants {
    /**
     * 性别
     */
    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 0;
    /**
     * 失物招领类型  遗失：1    捡到：0
     */
    public static final int TYPE_LOST = 1;
    public static final int TYPE_FOUND = 0;
    /**
     * 验证码长度
     */
    public static final int YZM_LENGTH = 4;
    /**
     * 分页加载  一次加载的item数
     */
    public static final int PAGING_LEGTH = 15;

    /**
     * 用户信息
     */
    public static final String USER = "user";
    public static final String USER_NAME = "user_name";
    public static final String USER_PHONE = "user_phone";
    public static final String USER_DORMITORY = "user_dormitory";
    public static final String USER_FLOOR = "user_floor";
    public static final String USER_DORM_NUMBER = "user_dorm_number";
    public static final String USER_SEX = "user_sex";

    public static final String ORDER_INFO = "order_info";

    /**
     * 选择 订单的 用户信息
     */
    public static final String SELECT_ORDER_USER_INFO = "select_order_user_info";

    /**
     * 切换用户的请求码
     */
    public static final int CHANGE_ADDRESS_INFO_REQUEST_CODE = 101;

    /**
     * Intent 之间传递 图片的 url ，显示放大图片
     */
    public static final String IMAGEVIEW_URL = "ImageView_url";
    public static String IMAGEVIEW_URL_VALUE;

    /**
     * 送水订单  是否送达   1  送达    0未送达
     */
    public static final int FINISH = 1;
    public static final int UNFINISH = 0;


    /**
     * 获取 图片的  请求码
     */
    public static final int REQUEST_PHOTO = 201;

    /**
     * 维修状态   SUBMISSION  已提交    ADMISSIBLE  已受理    COMPLETED 已完成
     */
    public static final int SUBMISSION = 0;
    public static final int ADMISSIBLE = 1;
    public static final int COMPLETED = 2;


}
