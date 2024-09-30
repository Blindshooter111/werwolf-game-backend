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
    private static final Map<Session, String> clients = new ConcurrentHashMap<>();  // Changed to store UUID
    private int i = 0;

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
        System.out.println("Client connected");
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
        clients.remove(session);
        System.out.println("Client disconnected");
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);
        String[] parts = message.split(" ", 3);
        String command = parts[0];
        String lobbyId;
        if (parts.length < 2) {
            session.getRemote().sendString("Invalid command format. Expected: COMMAND LOBBY_ID [MESSAGE]");
            return; // Exit the method early if the command format is incorrect
        }


        switch (command) {
            case "CREATE_LOBBY":
                lobbyId = parts[1];
                createLobby(session, lobbyId);
                break;
            case "JOIN_LOBBY":
                lobbyId = parts[1];
                joinLobby(session, lobbyId);
                break;
            case "CHAT":
                if (parts.length < 3) {
                    session.getRemote().sendString("CHAT command requires a message.");
                } else {
                    lobbyId = parts[1];
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
            // Generate UUID only if it does not exist
            if (!clients.containsKey(session)) {
                String clientId = UUID.randomUUID().toString();
                clients.put(session, clientId);
                session.getRemote().sendString("UUID " + clientId);
                System.out.println("Client assigned UUID: " + clientId);
            }

            lobbies.put(lobbyId, new Lobby(lobbyId));
            session.getRemote().sendString("LOBBY_CREATED " + lobbyId);
        }
    }

    private void joinLobby(Session session, String lobbyId) throws IOException {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            session.getRemote().sendString("Lobby does not exist");
        } else {
            // Generate UUID only if it does not exist
            if (!clients.containsKey(session)) {
                String clientId = UUID.randomUUID().toString();
                clients.put(session, clientId);
                session.getRemote().sendString("UUID " + clientId);
                System.out.println("Client assigned UUID: " + clientId);
            }

            lobby.addPlayer(session, clients.get(session));  // Add player with their UUID
            session.getRemote().sendString("LOBBY_JOINED " + lobbyId);
        }
    }

    private void startGame(String lobbyId) throws IOException {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            System.out.println("Lobby does not exist");
            return;
        }
        for (Session player : lobby.getPlayers().keySet()) {
            player.getRemote().sendString("Game is starting in lobby: " + lobbyId);
        }
    }

    private void broadcastMessage(String lobbyId, Session sender, String message) throws IOException {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            String senderClientId = clients.get(sender);  // Hole die ClientId des Absenders
            for (Session player : lobby.getPlayers().keySet()) {
                if (player != sender) {
                    // Sende die Nachricht im Format "CLIENT_ID: MESSAGE"
                    player.getRemote().sendString("CHAT " + senderClientId + ": " + message);
                }
            }
        } else {
            sender.getRemote().sendString("Lobby does not exist.");
        }
    }

    // Inner class to manage a lobby
    static class Lobby {
        private final String id;
        private final Map<Session, String> players;  // Map to store session and client UUID

        Lobby(String id) {
            this.id = id;
            this.players = new ConcurrentHashMap<>();
        }

        void addPlayer(Session session, String clientId) {
            players.put(session, clientId);
        }

        Map<Session, String> getPlayers() {
            return players;
        }
    }
}
