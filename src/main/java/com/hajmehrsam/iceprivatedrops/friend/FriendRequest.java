package com.hajmehrsam.iceprivatedrops.friend;

import java.util.UUID;

public class FriendRequest {
    private final UUID sender;
    private final UUID target;
    private final long expiryTimestamp;

    public FriendRequest(UUID sender, UUID target, long expiryTimestamp) {
        this.sender = sender;
        this.target = target;
        this.expiryTimestamp = expiryTimestamp;
    }

    public UUID getSender() { return sender; }
    public UUID getTarget() { return target; }
    public boolean isExpired() { return System.currentTimeMillis() >= expiryTimestamp; }
}