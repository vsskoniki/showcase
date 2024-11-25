import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import { createStompClient } from './stompClient'; // Import the stomp client utility

const LeaderBoard: React.FC = () => {
  const [client, setClient] = useState<Client | null>(null);
  const [messages, setMessages] = useState<string[]>([]); // Store received messages
  const [messageInput, setMessageInput] = useState<string>(''); // Input field state
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [columns, setColumns] = useState([]);
  const [rows, setRows] = useState([]);
  const [holes, setHoles] = useState([]);
  const [pars, setPars] = useState([]);
  const [fields, setFields] = useState([]);
  const [scores, setScores] = useState([]);

  // Fetch data from API
  const fetchTournament = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/tournament');
      const data = await response.json();
      setData(data);
      console.log("received "+data);
      setHoles(data["holes"]);
      setPars(data["pars"]);
    } catch (error) {
      console.error("Error fetching data: ", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchScores= async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/score-board');
      const scoreJsonData = await response.json();
      console.log("received "+scoreJsonData);
      setFields(scoreJsonData["fields"]);
      setScores(scoreJsonData["rows"]);
    } catch (error) {
      console.error("Error fetching data: ", error);
    } finally {
      setLoading(false);
    }
  };

  
  // Create the STOMP client and manage connections
  useEffect(() => {
    fetchTournament();
    fetchScores();
   
    const stompClient = createStompClient();

    stompClient.activate(); // Establish WebSocket connection

    stompClient.onConnect = () => {
      console.log('STOMP connected');
      
      stompClient.subscribe('/topic/scores', (message) => {
        if (message.body) {
          setMessages((prevMessages) => [...prevMessages, message.body]);
          var msg = JSON.parse(message.body);
          if(msg["alert"]) {
            alert(msg["alert"]);
          }
        }
        fetchScores();
      });
    };

    setClient(stompClient);

    return () => {
      // Cleanup: Disconnect when the component is unmounted
      if (stompClient) {
        stompClient.deactivate();
      }
    };
  }, []);

  
  return (
    <div>
      <h1>Leader Board</h1>
      <div className='small-text'>
        <h3>Tournament</h3>
         <table>
          <tbody>
          <tr>
            <td>Holes</td>
            {
              holes.map((msg, index) => (
                <td key={index}>{msg}</td>
              ))
            }
          </tr>
          <tr>
            <td>Pars</td>
            {
              pars.map((msg, index) => (
                <td key={index}>{msg}</td>
              ))
            }
          </tr>
          </tbody>
         </table>
      </div>
      <div className='small-text'>
        <h3>Scores</h3>
         <table style={{ width: '100vw' }}>
          
          <tbody>
          
          <tr>{
              fields.map((msg, index) => (
                <td key={index}>{msg}</td>
              ))
            }
          </tr>
          {scores.map((row, rowIndex) => (
          <tr key={rowIndex}>
            {(row as []).map((cell, cellIndex) => (
              <td key={cellIndex}>{cell}</td>
            ))}
          </tr>
        ))}
          </tbody>
         </table>
      </div>
  
    </div>
  );
};

export default LeaderBoard;
