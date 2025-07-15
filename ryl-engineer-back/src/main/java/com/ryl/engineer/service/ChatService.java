package com.ryl.engineer.service;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.chat.ConversationDTO;
import com.ryl.engineer.dto.chat.MessageDTO;

import java.util.List;
import java.util.Map;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 获取会话列表
     * @param userId 用户ID
     * @param type 会话类型(single-单聊, group-群聊, all-全部)
     * @param isTaskRelated 是否只显示任务相关会话
     * @param keyword 搜索关键词
     * @param onlyUnread 是否只显示有未读消息的会话
     * @param page 页码
     * @param size 每页大小
     * @return 会话分页列表
     */
    PageResult<ConversationDTO> getConversationList(Long userId, String type, Boolean isTaskRelated, 
                                                  String keyword, Boolean onlyUnread, 
                                                  Integer page, Integer size);
    
    /**
     * 创建聊天会话
     * @param creatorId 创建者ID
     * @param type 会话类型
     * @param name 会话名称
     * @param memberIds 会话成员ID列表
     * @param isTaskRelated 是否与任务相关
     * @param relatedTaskId 关联任务ID
     * @param avatar 会话头像
     * @return 创建的会话信息
     */
    ConversationDTO createConversation(Long creatorId, String type, String name, List<Long> memberIds,
                                      Boolean isTaskRelated, String relatedTaskId, String avatar);
    
    /**
     * 获取聊天消息列表
     * @param userId 当前用户ID
     * @param conversationId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消息分页列表
     */
    PageResult<MessageDTO> getMessageList(Long userId, String conversationId, 
                                         Integer page, Integer size, 
                                         String startTime, String endTime);
    
    /**
     * 发送消息
     * @param senderId 发送者ID
     * @param conversationId 会话ID
     * @param content 消息内容
     * @param messageType 消息类型
     * @param contentType 内容类型
     * @param replyTo 回复的消息ID
     * @return 发送的消息信息
     */
    Map<String, Object> sendMessage(Long senderId, String conversationId, String content,
                               String messageType, String contentType, String replyTo);
    
    /**
     * 上传消息图片
     * @param imageUrl 图片URL
     * @param imageName 图片名称
     * @param imageSize 图片大小
     * @param width 图片宽度
     * @param height 图片高度
     * @return 图片信息
     */
    Map<String, Object> uploadMessageImage(String imageUrl, String imageName, 
                                     Long imageSize, Integer width, Integer height);
    
    /**
     * 标记消息已读
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param messageId 消息ID
     * @return 会话未读消息数
     */
    int markMessageRead(Long userId, String conversationId, String messageId);
    
    /**
     * 更新会话设置
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param isSticky 是否置顶
     * @param isMute 是否静音
     * @return 更新后的会话设置信息
     */
    Map<String, Object> updateConversationSettings(Long userId, String conversationId, 
                                             Boolean isSticky, Boolean isMute);
    
    /**
     * 修改群聊信息
     * @param userId 当前用户ID
     * @param conversationId 会话ID
     * @param name 群聊名称
     * @param avatar 群聊头像
     * @return 更新后的会话信息
     */
    ConversationDTO updateConversation(Long userId, String conversationId, 
                                      String name, String avatar);
    
    /**
     * 管理会话成员
     * @param userId 当前用户ID
     * @param conversationId 会话ID
     * @param action 操作类型(add-添加, remove-移除)
     * @param memberIds 成员ID列表
     * @return 操作后的会话信息
     */
    ConversationDTO manageConversationMembers(Long userId, String conversationId, 
                                             String action, List<Long> memberIds);
    
    /**
     * 退出会话
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 退出结果
     */
    boolean exitConversation(Long userId, String conversationId);

    /**
     * 发送系统消息给指定用户
     * @param recipientId 接收者用户ID
     * @param content 消息内容
     * @return 发送的消息信息
     */
    Map<String, Object> sendSystemMessage(Long recipientId, String content);
} 