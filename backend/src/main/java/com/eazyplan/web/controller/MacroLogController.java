package com.eazyplan.web.controller;

import com.eazyplan.service.MacroLogService;
import com.eazyplan.web.dto.MacroLogRequest;
import com.eazyplan.web.dto.MacroLogResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * API REST de registros de macronutrientes.
 *
 * <pre>
 * GET    /api/users/{userId}/macro-logs   → 200 [MacroLogResponse]
 * POST   /api/users/{userId}/macro-logs   → 201 MacroLogResponse
 * DELETE /api/macro-logs/{id}             → 204
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class MacroLogController {

    private final MacroLogService macroLogService;

    public MacroLogController(MacroLogService macroLogService) {
        this.macroLogService = macroLogService;
    }

    @GetMapping("/users/{userId}/macro-logs")
    public List<MacroLogResponse> listUserLogs(@PathVariable Long userId) {
        return macroLogService.getUserLogs(userId).stream()
                .map(MacroLogResponse::from)
                .toList();
    }

    @PostMapping("/users/{userId}/macro-logs")
    public ResponseEntity<MacroLogResponse> createLog(@PathVariable Long userId,
                                                      @Valid @RequestBody MacroLogRequest request) {
        var saved = macroLogService.logMacros(userId, request.dietId(), request.toEntity());
        return ResponseEntity
                .created(URI.create("/api/macro-logs/" + saved.getId()))
                .body(MacroLogResponse.from(saved));
    }

    @DeleteMapping("/macro-logs/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        macroLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
