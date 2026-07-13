package com.eazyplan.web.controller;

import com.eazyplan.service.MicroLogService;
import com.eazyplan.web.dto.MicroLogRequest;
import com.eazyplan.web.dto.MicroLogResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * API REST de registros de micronutrientes.
 *
 * <pre>
 * GET    /api/users/{userId}/micro-logs   → 200 [MicroLogResponse]
 * POST   /api/users/{userId}/micro-logs   → 201 MicroLogResponse
 * DELETE /api/micro-logs/{id}             → 204
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class MicroLogController {

    private final MicroLogService microLogService;

    public MicroLogController(MicroLogService microLogService) {
        this.microLogService = microLogService;
    }

    @GetMapping("/users/{userId}/micro-logs")
    public List<MicroLogResponse> listUserLogs(@PathVariable Long userId) {
        return microLogService.getUserLogs(userId).stream()
                .map(MicroLogResponse::from)
                .toList();
    }

    @PostMapping("/users/{userId}/micro-logs")
    public ResponseEntity<MicroLogResponse> createLog(@PathVariable Long userId,
                                                      @Valid @RequestBody MicroLogRequest request) {
        var saved = microLogService.logMinerals(userId, request.toEntity());
        return ResponseEntity
                .created(URI.create("/api/micro-logs/" + saved.getId()))
                .body(MicroLogResponse.from(saved));
    }

    @DeleteMapping("/micro-logs/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        microLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
