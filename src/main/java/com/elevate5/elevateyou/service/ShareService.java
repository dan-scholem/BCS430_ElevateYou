package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.dao.ShareDao;

import java.util.concurrent.CompletableFuture;

public class ShareService {

    private final ShareDao shareDao = new ShareDao();

    public CompletableFuture<Boolean> isSharing(String ownerUid, String viewerUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return shareDao.isSharing(ownerUid, viewerUid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> setSharing(String ownerUid, String viewerUid, boolean enabled) {
        return CompletableFuture.runAsync(() -> {
            try {
                shareDao.setSharing(ownerUid, viewerUid, enabled);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<java.util.Map<String, Boolean>> getAllShares(String ownerUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return shareDao.getAllShares(ownerUid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}