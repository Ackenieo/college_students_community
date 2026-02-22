package org.example.communityserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AgentService {

    // TODO: 集成实际的Agent服务进行内容审核 - 已完善: 实现基础的内容审核功能
    // 敏感词列表
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
        // 一、违法违禁类（1-20）
        "贩毒", "吸毒", "制毒", "枪支", "弹药", "管制刀具", "酒驾", "醉驾", "盗窃", "抢劫",
        "故意伤害", "杀人", "绑架", "敲诈勒索", "走私", "偷渡", "偷税", "漏税", "行贿", "受贿",
        // 二、涉赌类（21-35）
        "网赌", "六合彩", "百家乐", "炸金花", "赌球", "赌马", "时时彩", "快乐十分", "捕鱼赌博", 
        "棋牌赌博", "德州扑克赌", "麻将赌博", "赌大小", "赌三公", "彩票作弊",
        // 三、涉诈类（36-55）
        "刷单返利", "冒充客服", "冒充公检法", "杀猪盘", "裸聊诈骗", "贷款诈骗", "中奖诈骗", 
        "钓鱼网站", "电信诈骗", "网络诈骗", "医保诈骗", "社保诈骗", "养老诈骗", "荐股诈骗", 
        "游戏币诈骗", "虚拟币诈骗", "冒充领导", "冒充亲友", "快递诈骗", "退款诈骗",
        // 四、色情低俗类（56-70）
        "裸聊", "卖淫", "嫖娼", "约炮", "艳照", "成人影片", "情色", "低俗", "性交易", "援交",
        "露点", "色情直播", "黄片", "性服务", "一夜情",
        // 五、暴力恐怖/极端类（71-80）
        "砍杀", "枪击", "爆炸", "自残", "自杀", "恐怖袭击", "极端主义", "恐怖主义", 
        "人肉搜索", "校园暴力",
        // 六、传销/违规营销类（81-90）
        "无限代", "拉人头", "高收益返利", "虚假理财", "传销", "非法集资", "保健品诈骗", 
        "医美诈骗", "直播诈骗", "虚假广告",
        // 七、人身攻击/歧视类（91-95）
        "地域歧视", "种族歧视", "人身攻击", "辱骂", "诅咒",
        // 八、其他违规类（96-100）
        "邪教", "反社会", "造谣", "诽谤", "恶意刷屏"
    );

    /**
     * 内容审核
     * @param content 待审核内容
     * @return 审核结果: APPROVED(通过), REJECTED(拒绝), PENDING(待人工审核)
     */
    public String reviewContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            log.warn("内容为空,标记为待审核");
            return "PENDING";
        }

        // 检查是否包含敏感词
        for (String sensitiveWord : SENSITIVE_WORDS) {
            if (content.contains(sensitiveWord)) {
                log.warn("内容包含敏感词[{}],审核拒绝", sensitiveWord);
                return "REJECTED";
            }
        }

        // 检查内容长度
        if (content.length() > 10000) {
            log.warn("内容过长,标记为待人工审核");
            return "PENDING";
        }

        // 检查是否包含大量特殊字符
        long specialCharCount = content.chars()
                .filter(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c))
                .count();
        if (specialCharCount > content.length() * 0.5) {
            log.warn("内容包含过多特殊字符,标记为待人工审核");
            return "PENDING";
        }

        // 通过审核
        log.info("内容审核通过");
        return "APPROVED";
    }

    /**
     * 获取审核拒绝原因
     * @param content 待审核内容
     * @return 拒绝原因,如果通过则返回null
     */
    public String getRejectionReason(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "内容为空";
        }

        for (String sensitiveWord : SENSITIVE_WORDS) {
            if (content.contains(sensitiveWord)) {
                return "内容包含敏感词: " + sensitiveWord;
            }
        }

        if (content.length() > 10000) {
            return "内容过长,需要人工审核";
        }

        long specialCharCount = content.chars()
                .filter(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c))
                .count();
        if (specialCharCount > content.length() * 0.5) {
            return "内容包含过多特殊字符,需要人工审核";
        }

        return null;
    }
}
