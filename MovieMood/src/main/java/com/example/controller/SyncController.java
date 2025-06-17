package com.example.controller;

import com.example.service.MovieSyncService;
import com.example.service.MovieSyncService.SyncStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired
    private MovieSyncService movieSyncService;

    /**
     * Get current sync status
     */
    @GetMapping("/status")
    public ResponseEntity<SyncStatus> getSyncStatus() {
        return ResponseEntity.ok(movieSyncService.getSyncStatus());
    }

    /**
     * Enable/disable sync service
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleSync(@RequestParam boolean enabled) {
        movieSyncService.setSyncEnabled(enabled);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sync service " + (enabled ? "enabled" : "disabled"));
        response.put("enabled", enabled);

        return ResponseEntity.ok(response);
    }

    /**
     * Trigger manual full resync (admin operation)
     */
    @PostMapping("/resync")
    public ResponseEntity<Map<String, String>> triggerResync() {
        try {
            movieSyncService.triggerFullResync();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Full resync started successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Trigger initial sync manually (useful for testing)
     */
    @PostMapping("/initial")
    public ResponseEntity<Map<String, String>> triggerInitialSync() {
        try {
            movieSyncService.performInitialSync();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Initial sync completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Initial sync failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}