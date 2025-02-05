package com.ryanheise.just_audio;

import android.content.Context;
import androidx.annotation.NonNull;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.*;
import static java.util.Collections.emptyMap;

public class MainMethodCallHandler implements MethodCallHandler {

    private final Context applicationContext;
    private final BinaryMessenger messenger;

    private final Map<String, AudioPlayer> players = new HashMap<>();

    public MainMethodCallHandler(Context applicationContext,
            BinaryMessenger messenger) {
        this.applicationContext = applicationContext;
        this.messenger = messenger;
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        final Map<?, ?> request = call.arguments();
        switch (call.method) {
        case "init": {
            String id = (String)request.get("id");
            if (players.containsKey(id)) {
                result.error("Platform player " + id + " already exists", null, null);
                break;
            }
            players.put(id, new AudioPlayer(applicationContext, messenger, id));
            result.success(null);
            break;
        }
        case "disposePlayer": {
            String id = (String)request.get("id");
            AudioPlayer player = players.get(id);
            if (player != null) {
                player.dispose();
                players.remove(id);
            }
            result.success(emptyMap());
            break;
        }
        case "disposeAllPlayers": {
            Set<String> disposedPlayers = players.keySet();
            this.dispose();

            result.success(singletonMap("disposedPlayers", new ArrayList<>(disposedPlayers)));
            break;
        }
        default:
            result.notImplemented();
            break;
        }
    }

    void dispose() {
        for (AudioPlayer player : players.values()) {
            player.dispose();
        }
        players.clear();
    }
}
