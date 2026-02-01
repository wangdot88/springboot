package dangod.springboot.core.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    
    MEMBER_NOT_FOUND(1001, "会员不存在"),
    MEMBER_EXISTS(1002, "会员已存在"),
    CARD_NOT_FOUND(1101, "会员卡不存在"),
    CARD_EXPIRED(1102, "会员卡已过期"),
    CARD_FROZEN(1103, "会员卡已冻结"),
    CARD_INSUFFICIENT(1104, "会员卡次数不足"),
    CARD_LEVEL_INSUFFICIENT(1105, "会员卡等级不足"),
    
    COURSE_NOT_FOUND(1201, "课程不存在"),
    COURSE_FULL(1202, "课程已满"),
    COURSE_ENDED(1203, "课程已结束"),
    COURSE_CANCELLED(1204, "课程已取消"),
    COURSE_DUPLICATE(1205, "不能重复约课"),
    COURSE_TIME_CONFLICT(1206, "上课时间冲突"),
    COURSE_NOT_AVAILABLE(1207, "课程不可预约"),
    COURSE_ALREADY_STARTED(1208, "课程已开始"),
    
    RESERVATION_NOT_FOUND(1301, "预约不存在"),
    RESERVATION_CANCELLED(1302, "预约已取消"),
    RESERVATION_CHECKIN_TIMEOUT(1303, "签到时间已过"),
    RESERVATION_ALREADY_CHECKIN(1304, "已签到"),
    CHECKIN_TOO_EARLY(1305, "签到过早，请在课程开始前30分钟内签到"),
    CHECKIN_TOO_LATE(1306, "签到过晚，课程已开始超过15分钟"),
    RESERVATION_EXISTS(1307, "已预约该课程"),
    RESERVATION_CONFLICT(1308, "预约时间冲突"),
    
    WAITING_LIST_EXISTS(1401, "已在排队中"),
    WAITING_LIST_FULL(1402, "排队人数过多"),
    ALREADY_IN_WAITING_LIST(1403, "已在排队中"),
    
    TEST_NOT_FOUND(1501, "体测数据不存在"),
    PLAN_NOT_FOUND(1601, "训练计划不存在"),
    
    EQUIPMENT_NOT_FOUND(1701, "器材不存在"),
    EQUIPMENT_OCCUPIED(1702, "器材正在使用中"),
    
    APPROVAL_PENDING(1801, "审批中"),
    APPROVAL_NOT_FOUND(1802, "审批记录不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
