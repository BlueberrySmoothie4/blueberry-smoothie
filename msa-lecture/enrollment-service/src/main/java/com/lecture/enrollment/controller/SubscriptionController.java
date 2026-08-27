package com.lecture.enrollment.controller;

import com.lecture.enrollment.dto.SubscriptionDto;
import com.lecture.enrollment.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * POST /subscriptions - 구독 신청
     * Gateway에서 X-User-Id 헤더로 사용자 ID 전달
     */
    @PostMapping
    public ResponseEntity<SubscriptionDto.ApiResponse<SubscriptionDto.SubscriptionResponse>> subscribe(
            @Valid @RequestBody SubscriptionDto.SubscribeRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        SubscriptionDto.SubscriptionResponse response =
                subscriptionService.subscribe(userId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubscriptionDto.ApiResponse.success(response));
    }

    /**
     * GET /subscriptions/my - 내 구독 목록 조회
     * Gateway가 전달한 X-User-Id 헤더를 사용
     */
    @GetMapping("/my")
    public ResponseEntity<SubscriptionDto.ApiResponse<List<SubscriptionDto.SubscriptionResponse>>> getMySubscriptions(
            @RequestHeader("X-User-Id") Long userId) {

        List<SubscriptionDto.SubscriptionResponse> response =
                subscriptionService.getSubscriptionsByUser(userId);
        return ResponseEntity.ok(SubscriptionDto.ApiResponse.success(response));
    }

    /**
     * GET /subscriptions/user/{userId} - 특정 사용자 구독 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<SubscriptionDto.ApiResponse<List<SubscriptionDto.SubscriptionResponse>>> getSubscriptions(
            @PathVariable Long userId) {

        List<SubscriptionDto.SubscriptionResponse> response =
                subscriptionService.getSubscriptionsByUser(userId);
        return ResponseEntity.ok(SubscriptionDto.ApiResponse.success(response));
    }

    /**
     * GET /subscriptions/internal/history/{userId} - 구독 이력 조회 (Recommend Service용)
     */
    @GetMapping("/internal/history/{userId}")
    public ResponseEntity<SubscriptionDto.SubscriptionHistoryResponse> getSubscriptionHistory(
            @PathVariable Long userId) {

        return ResponseEntity.ok(subscriptionService.getSubscriptionHistory(userId));
    }
}
