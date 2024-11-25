import { Client, IMessage } from '@stomp/stompjs';

// WebSocket URL for the STOMP server (replace with your actual endpoint)
const SOCKET_URL = 'ws://localhost:8080/websocket'; // Replace with your URL

// Create the STOMP client
export const createStompClient = () => {
  const client = new Client({
    brokerURL: SOCKET_URL,
    reconnectDelay: 5000, // Reconnect every 5 seconds if connection is lost
    debug: (str: string) => {
      console.log(str); // Logs debug messages (optional)
    },
    onConnect: () => {
      console.log('Connected to STOMP WebSocket');
    },
    onDisconnect: () => {
      console.log('Disconnected from STOMP WebSocket');
    },
    onStompError: (error: any) => {
      console.error('STOMP Error:', error);
    },
  });

  return client;
};
