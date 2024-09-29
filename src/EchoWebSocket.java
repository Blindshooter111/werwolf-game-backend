import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@WebSocket
public class EchoWebSocket {
    private static final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static final Map<Session, Integer> clients = new ConcurrentHashMap<>();
    private int i = 0;

    @OnWebSocketConnect
    public void connected(Session session){
        sessions.add((session));
        clients.put(session, i);
        i++;
        System.out.println(i);

    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason){
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);
        String[] parts = message.split(" ", 3);
        String command = parts[0];

        if (parts.length < 2) {
            session.getRemote().sendString("Invalid command format. Expected: COMMAND LOBBY_ID [MESSAGE]");
            return; // Exit the method early if the command format is incorrect
        }

        String lobbyId = parts[1];

        switch (command) {
            case "CREATE_LOBBY":
                createLobby(session, lobbyId);
                break;
            case "JOIN_LOBBY":
                joinLobby(session, lobbyId);
                break;
            case "START_GAME":
                startGame(lobbyId);
                break;
            case "CHAT":
                if (parts.length < 3) {
                    session.getRemote().sendString("CHAT command requires a message.");
                } else {
                    broadcastMessage(lobbyId, session, parts[2]);
                }
                break;
            default:
                session.getRemote().sendString("Unknown command");
        }
    }

    private void createLobby(Session session, String lobbyId) throws IOException {
        if (lobbies.containsKey(lobbyId)) {
            session.getRemote().sendString("Lobby already exists");
        } else {
            lobbies.put(lobbyId, new Lobby(lobbyId));
            session.getRemote().sendString("LOBBY_CREATED " + lobbyId);
        }
    }

    private void joinLobby(Session session, String lobbyId) throws IOException {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            session.getRemote().sendString("Lobby does not exist");
        } else {
            lobby.addPlayer(session);
            session.getRemote().sendString("LOBBY_JOINED " + lobbyId);
        }
    }

    private void startGame(String lobbyId) throws IOException {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            System.out.println("Lobby does not exist");
            return;
        }
        for (Session player : lobby.getPlayers()) {
            player.getRemote().sendString("Game is starting in lobby: " + lobbyId);
        }
    }

    private void broadcastMessage(String lobbyId, Session sender, String message) throws IOException {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            for (Session player : lobby.getPlayers()) {
                if (player != sender) {
                    player.getRemote().sendString("CHAT " + message);
                }
            }
        } else {
            sender.getRemote().sendString("Lobby does not exist.");
        }
    }

    // Inner class to manage a lobby
    static class Lobby {
        private final String id;
        private final List<Session> players;

        Lobby(String id) {
            this.id = id;
            this.players = new ArrayList<>();
        }

        void addPlayer(Session session) {
            players.add(session);
        }

        List<Session> getPlayers() {
            return players;
        }
    }
}
