import React, {useState} from 'react';
import './TopMenu.css'; // Import the CSS file for styling

const TopMenu: React.FC = () => {
 
  return (
    <nav className="top-menu">
      <ul className="menu-list">
        <li><a href="#home">Home</a></li>
        <li><a href="#playerScore">Player Score</a></li>
        
      </ul>
    </nav>
  );
};

export default TopMenu;