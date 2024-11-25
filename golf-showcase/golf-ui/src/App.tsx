import React, { useState } from 'react';
import './App.css';
import TopMenu from './TopMenu';
import PlayerScore from './PlayerScore';
import LeaderBoard from './LeaderBoard'; 


function App() {
  return (
    <div className="App">
      <TopMenu />

      <div id="home" className="section">
      <LeaderBoard/>
      </div>
      
      <div id="playerScore" className="section">
        <h1>Player Score</h1>
        
        <PlayerScore/>
      </div>
      
        <p>
          Golf LeaderBoard
        </p>
        
    </div>
  );
}

export default App;
