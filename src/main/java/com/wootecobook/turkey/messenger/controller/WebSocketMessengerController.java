package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.messenger.service.MessengerService;
import com.wootecobook.turkey.messenger.service.dto.MessageRequest;
import com.wootecobook.turkey.messenger.service.dto.MessageResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

@Controller
public class WebSocketMessengerController {

    private final MessengerService messengerService;

    public WebSocketMessengerController(final MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @MessageMapping("/messenger/{roomId}")
    @SendTo("/topic/messenger/{roomId}")
    public MessageResponse sendMessage(@DestinationVariable Long roomId, MessageRequest messageRequest, SimpMessageHeaderAccessor messageHeaderAccessor) {
        UserSession sender = getLoginUser(messageHeaderAccessor);
        return messengerService.sendMessage(roomId, sender.getId(), messageRequest.getMessage());
    }

    private UserSession getLoginUser(SimpMessageHeaderAccessor messageHeaderAccessor) {
        return (UserSession) ((HttpSession) messageHeaderAccessor.getSessionAttributes().get("session")).getAttribute("loginUser");
    }

}
